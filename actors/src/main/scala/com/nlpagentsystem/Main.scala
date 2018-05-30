package com.nlpagentsystem

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

object Main extends App with Routing {
  val client = MongoClient("mongodb://localhost")
  prepareDatabase(client)
  implicit val system: ActorSystem = ActorSystem("argumentation-agents")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val arbiterActor: ActorRef = system.actorOf(Props[OpinionArbiterActor], "arbiterActor")
  Http().bindAndHandle(routes, "localhost", 8080)
  println(s"Server online at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)

  private def prepareDatabase(client: MongoClient): Unit = {
    val codecRegistry = fromRegistries(fromProviders(classOf[Review]), DEFAULT_CODEC_REGISTRY)
    val db = client.getDatabase("argumentation-agents").withCodecRegistry(codecRegistry)
    val collection: MongoCollection[Review] = db.getCollection("reviews")
    addIndex(collection)
    addFakeData(collection)
  }

  private def addFakeData(collection: MongoCollection[Review]) = {
    val fakeReviews = Seq(
      Review(productId = "P1", reviewId = "R1"),
      Review(productId = "P1", reviewId = "R2"),
      Review(productId = "P1", reviewId = "R3"),
      Review(productId = "P1", reviewId )
    )
    fakeReviews.foreach(
      review => {
        val future = collection.insertOne(review).toFuture()
        try {
          Await.result(future, Duration.Inf)
        } catch {
          case e: Exception => println(e)
        }
      }
    )
  }

  private def addIndex(collection: MongoCollection[Review]) = {
    val createIndex = collection.createIndexes(
      Seq(
        IndexModel(
          Indexes.ascending("productId", "reviewId"),
          IndexOptions().background(false).unique(true)
        )
      )
    ).head()
    Await.result(createIndex, Duration.Inf)
  }
}
