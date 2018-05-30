package com.nlpagentsystem

import akka.actor.{ Actor, ActorLogging }

class OpinionExchangeActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case a => println(a)
  }
}
