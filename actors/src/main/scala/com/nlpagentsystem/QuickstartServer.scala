package com.nlpagentsystem

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object QuickstartServer extends App with Routing {
  implicit val system: ActorSystem = ActorSystem("argumentation-actors")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val arbiterActor: ActorRef = system.actorOf(Props[OpinionArbiterActor], "arbiterActor")
  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)
}
