package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging, Props }

object OpinionArbiterActor {
  final case class GetOpinion(product_id: String)
  final case class Opinion(verdict: String, description: String)
}

class OpinionArbiterActor extends Actor with ActorLogging {
  import OpinionArbiterActor._

  override def receive: Receive = {
    case GetOpinion(product_id) => {
      val helloActor = context.actorOf(Props(new OpinionExchangeActor()), name = "helloactor")
      sender() ! Opinion(verdict = "cool", description = product_id)
    }

  }
}
