package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, ActorRef, PoisonPill, Props }
import com.nlpagentsystem.DebateSupervisorActor.{ NewArgument => SupervisorNewArgument }
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

final case class Argument(componentName: String, description: String, polarityScore: Double)

object DebaterActor {
  def props(collection: MongoCollection[Review], productId: String, skip: Int, limit: Int): Props =
    Props(new DebaterActor(collection, productId, skip, limit))

  final case class OutOfArguments()
  final case class StartDebate(otherDebater: ActorRef)
  final case class NewArgument(argument: Argument)

}

class DebaterActor(collection: MongoCollection[Review], productId: String, skip: Int, limit: Int)
    extends Actor with ActorLogging {

  import DebaterActor._
  import context._

  val arguments: mutable.ListBuffer[Argument] = mutable.ListBuffer()

  override def preStart() {
    log.info("Starting debating actor")
    val future = collection.find(equal("productId", productId)).skip(skip).limit(limit).toFuture()
    val fetchedReviews = Await.result(future, Duration(5, "sec"))
    fetchedReviews.foreach(
      review => review.features.foreach(f => arguments.+=(Argument(f.name, f.description, f.polarityScore)))
    )
  }

  def exhausted: Receive = {
    case NewArgument(argument) => log.info(s"New argument from partner, but I cannot respond to it: $argument")
    case OutOfArguments =>
      log.info("Both partner and I ran out of arguments. Sepuku")
      self ! PoisonPill
    case default => log.debug(default.toString)
  }
  override def receive: Receive = {
    case StartDebate(otherDebater) =>
      log.info("Starting debate...")
      if (arguments.isEmpty) {
        log.info("I have ran out of arguments. Waiting for partner to finish.")
        become(exhausted)
        otherDebater ! OutOfArguments
      } else {
        val newArgument = arguments.remove(0)
        otherDebater ! NewArgument(newArgument)
        context.parent ! SupervisorNewArgument(newArgument)
      }
    case NewArgument(argument) =>
      log.info(s"New argument from partner: $argument")
      if (arguments.isEmpty) {
        log.info("I have ran out of arguments. Waiting for partner to finish.")
        become(exhausted)
        sender() ! OutOfArguments
      } else {
        val similarArgument = arguments.find(a => a.componentName == argument.componentName).orNull
        val newArgument = if (similarArgument != null) {
          arguments.-=(similarArgument)
          similarArgument
        } else arguments.remove(0)
        sender() ! NewArgument(newArgument)
        context.parent ! SupervisorNewArgument(newArgument)
      }
    case OutOfArguments =>
      log.info("My partner ran out of arguments")
      arguments.foreach(
        arg => {
          sender() ! NewArgument(arg)
          context.parent ! SupervisorNewArgument(arg)
        }
      )
      sender() ! OutOfArguments
      log.info("I have ran out of arguments. Sepuku")
      self ! PoisonPill
  }
}
