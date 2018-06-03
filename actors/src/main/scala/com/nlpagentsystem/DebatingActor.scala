package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, PoisonPill, Props }
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

final case class Argument(componentName: String, description: String, score: Double)

object DebaterActor {
  def props(
    name: String,
    collection: MongoCollection[Review],
    productId: String,
    skip: Int,
    limit: Int
  ): Props =
    Props(new DebaterActor(name, collection, productId, skip, limit))
}

class DebaterActor(
  name: String,
  collection: MongoCollection[Review],
  productId: String,
  skip: Int,
  limit: Int
)
    extends Actor with ActorLogging {

  import DebateProtocol._
  import context._

  var arguments: ListBuffer[Argument] = ListBuffer.empty

  override def preStart() {
    val future = collection.find(equal("product_id", productId)).skip(skip).limit(limit).toFuture()
    val fetchedReviews = Await.result(future, Duration(5, "sec"))
    arguments = fetchedReviews.flatMap(
      review => review.features
        .filter(feature => feature.polarity_score.abs >= 0.15)
        .map(feature => Argument(feature.name, feature.description, feature.polarity_score))
    ).to[ListBuffer]
  }

  def exhausted: Receive = {
    case OutOfArguments(from) => self ! PoisonPill
    case default => log.debug(default.toString)
  }
  override def receive: Receive = {
    case StartDebate =>
      if (arguments.isEmpty) {
        become(exhausted)
        parent ! OutOfArguments(name)
      } else {
        val newArgument = arguments.remove(0)
        parent ! NewArgument(name, newArgument)
      }
    case NewArgument(_, argument) =>
      if (arguments.isEmpty) {
        become(exhausted)
        parent ! OutOfArguments(name)
      } else {
        val similarArgument = arguments.find(a => a.componentName == argument.componentName).orNull
        val newArgument = if (similarArgument != null) {
          arguments.-=(similarArgument)
          similarArgument
        } else arguments.remove(0)
        parent ! NewArgument(name, newArgument)
      }
    case ooA: OutOfArguments =>
      arguments.foreach(
        arg => {
          parent ! NewArgument(name, arg)
        }
      )
      parent ! OutOfArguments(name)
      self ! PoisonPill
  }
}
