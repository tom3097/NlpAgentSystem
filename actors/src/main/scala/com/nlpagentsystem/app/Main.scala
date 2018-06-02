package com.nlpagentsystem.app

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nlpagentsystem.{ BasicSolver, DebateSupervisorActor }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with Routing {

  val reviewsCollection = ReviewsCollection.create("mongodb://localhost")
  val solver = new BasicSolver()
  implicit val system: ActorSystem = ActorSystem("argumentation-agents")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  Http().bindAndHandle(routes, "localhost", 8080)
  log.info(s"Server online, visit eg. http://localhost:8080/opinions/B004GAR91S")
  Await.result(system.whenTerminated, Duration.Inf)

  override def supervisor() = system.actorOf(Props(new DebateSupervisorActor(solver, reviewsCollection)))
}
