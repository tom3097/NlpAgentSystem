package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, ActorRef, Terminated }
import com.nlpagentsystem.DebaterActor.StartDebate
import org.mongodb.scala.MongoCollection

import scala.util.{ Failure, Success }
import org.mongodb.scala.model.Filters._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

final case class Opinion(verdict: String, description: String)

object DebateSupervisorActor {
  final case class GetOpinion(productId: String)
  final case class NewArgument(argument: Argument)
}

class DebateSupervisorActor(collection: MongoCollection[Review]) extends Actor with ActorLogging {
  import DebateSupervisorActor._

  val debaters: mutable.ListBuffer[ActorRef] = mutable.ListBuffer()
  val numberOfDebaters: Int = 2
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
          var limit = (count / numberOfDebaters).floor.toInt
          for (i <- 0 until numberOfDebaters) {
            val debater = context.actorOf(
              DebaterActor.props(collection, productId, skip, limit)
            )
            context.watch(debater)
            debaters.+=(debater)
            skip += limit
          }

          debaters.head ! StartDebate(debaters.last)
        case Failure(ex) => log.error(ex.toString)
      }
    case NewArgument(argument) => log.debug(s"Received from debaters: $argument")
    case Terminated(debater) =>
      debaters.-=(debater)
      if (debaters.isEmpty) {
        opinionRequester.get ! Opinion("cool", "very cool")
      }
  }
}
