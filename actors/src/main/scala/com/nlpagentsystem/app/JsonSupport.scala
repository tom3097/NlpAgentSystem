package com.nlpagentsystem.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nlpagentsystem.Opinion
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val opinionJsonFormat = jsonFormat2(Opinion)

}
