package com.nlpagentsystem

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nlpagentsystem.OpinionArbiterActor._
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val opinionJsonFormat = jsonFormat2(Opinion)

}
