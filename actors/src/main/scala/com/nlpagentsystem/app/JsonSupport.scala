package com.nlpagentsystem.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nlpagentsystem.{ Node, Result }
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val node = jsonFormat5(Node)
  implicit val result = jsonFormat3(Result)

}
