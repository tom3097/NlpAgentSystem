package com.nlpagentsystem.app

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.nlpagentsystem.DebateSupervisorActor._
import com.nlpagentsystem.Opinion

import scala.concurrent.Future
import scala.concurrent.duration._

trait Routing extends JsonSupport {

  def supervisor(): ActorRef
  implicit def system: ActorSystem
  implicit lazy val timeout = Timeout(15.seconds)
  lazy val log = Logging(system, classOf[Routing])

  lazy val routes: Route =
    pathPrefix("opinions") {
      concat(
        path(Segment) { productId =>
          concat(
            get {
              val maybeOpinion: Future[Opinion] = (supervisor() ? GetOpinion(productId)).mapTo[Opinion]
              rejectEmptyResponse {
                complete(maybeOpinion)
              }
            }
          )
        }
      )
    }

}
