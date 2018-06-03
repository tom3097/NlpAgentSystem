package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import com.nlpagentsystem.DebaterActor.StartDebate
import org.mongodb.scala.MongoCollection

import scala.util.{ Failure, Success }
import org.mongodb.scala.model.Filters._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object DebateSupervisorActor {

  final case class GetOpinion(productId: String)
  final case class NewArgument(from: String, argument: Argument)
  final case class OutOfArguments(from: String)

}

class DebateSupervisorActor(solver: Solver, collection: MongoCollection[Review]) extends Actor with ActorLogging {

  import DebateSupervisorActor._

  val debaterNames: List[String] = List("Bob", "Alice")
  val debaters: mutable.Map[ActorRef, String] = mutable.Map.empty
  var opinionRequester: Option[ActorRef] = Option.empty

  override def preStart(): Unit = {
    log.info("Debate supervisor actor started")
  }

  override def receive: Receive = {
    case GetOpinion(productId) =>
      opinionRequester = Option(sender())
      collection.count(equal("productId", productId)).toFuture().onComplete {
        case Success(count) =>
          var skip = 0
          var limit = (count / debaterNames.size).floor.toInt
          debaterNames.foreach {
            name =>
              val debater = context.actorOf(
                DebaterActor.props(name, collection, productId, skip, limit)
              )
              context.watch(debater)
              debaters.+=(debater -> name)
              skip += limit
              log.info(s"$name added to debate")
          }

          debaters.head._1 ! StartDebate(debaters.last._1)
        case Failure(ex) => log.error(ex.toString)
      }
    case NewArgument(from, argument) =>
      log.info(s"[$from] ${argument.description}")
      solver.addArgument(argument)
    case OutOfArguments(from) => log.info(s"$from run out of arguments.")
    case Terminated(debater) =>
      val debaterName = debaters.remove(debater).orNull
      log.info(s"$debaterName left.")
      if (debaters.isEmpty) {
        val result = solver.solve()
        opinionRequester.get ! result
      }
  }
}
