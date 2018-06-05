package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import org.mongodb.scala.MongoCollection

import scala.util.{ Failure, Success }
import org.mongodb.scala.model.Filters._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object DebateProtocol {
  final case class NewArgument(from: String, argument: Argument)
  final case class OutOfArguments(from: String)
  final case object StartDebate
}

final case class GetOpinion(productId: String)

class DebateSupervisorActor(solver: Solver, collection: MongoCollection[Review]) extends Actor with ActorLogging {

  import DebateProtocol._

  val debaterNames: List[String] = List("Bob", "Alice")
  val activeDebaters: mutable.Map[ActorRef, String] = mutable.Map.empty
  var opinionRequester: Option[ActorRef] = Option.empty

  override def preStart(): Unit = {
    log.info("Debate supervisor actor started")
  }

  override def receive: Receive = {
    case GetOpinion(productId) =>
      opinionRequester = Option(sender())
      collection.count(equal("product_id", productId)).toFuture().onComplete {
        case Success(count) =>
          var skip = 0
          var limit = (count / debaterNames.size).floor.toInt
          debaterNames.foreach {
            name =>
              val debater = context.actorOf(
                DebaterActor.props(name, collection, productId, skip, limit)
              )
              context.watch(debater)
              activeDebaters.+=(debater -> name)
              skip += limit
              log.info(s"$name added to debate")
          }

          activeDebaters.head._1 ! StartDebate
        case Failure(ex) => log.error(ex.toString)
      }
    case arg: NewArgument =>
      log.info(s"[${arg.from}] ${arg.argument.description}")
      val others = activeDebaters.filterNot { case (debater, _) => debater == sender() }
      others.foreach { case (debater, _) => debater ! arg }
      solver.addArgument(arg.argument, arg.from)
    case ooA: OutOfArguments =>
      log.info(s"${ooA.from} run out of arguments.")
      val others = activeDebaters.filterNot { case (debater, _) => debater == sender() }
      others.foreach { case (debater, _) => debater ! ooA }
    case Terminated(killedDebater) =>
      val debaterName = activeDebaters.remove(killedDebater).orNull
      log.info(s"$debaterName left.")
      activeDebaters.foreach {
        case (debater, _) => debater ! OutOfArguments(debaterName)
      }
      if (activeDebaters.isEmpty) {
        val result = solver.solve()
        opinionRequester.get ! result
      }
  }
}
