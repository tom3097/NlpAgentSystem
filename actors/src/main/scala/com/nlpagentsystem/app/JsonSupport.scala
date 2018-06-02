package com.nlpagentsystem.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nlpagentsystem.{ Argument, Node, Result }
import spray.json.{ DefaultJsonProtocol }

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val result = jsonFormat2(Result)

}
