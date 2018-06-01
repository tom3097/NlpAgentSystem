package com.nlpagentsystem

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import com.nlpagentsystem.OpinionArbiterActor._

trait Routing extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routing])

  def arbiterActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val routes: Route =
    pathPrefix("opinions") {
      concat(
        path(Segment) { product_id =>
          concat(
            get {
              val maybeOpinion: Future[Opinion] = (arbiterActor ? GetOpinion(product_id)).mapTo[Opinion]
              rejectEmptyResponse {
                complete(maybeOpinion)
              }
            }
          )
        }
      )
    }
}
