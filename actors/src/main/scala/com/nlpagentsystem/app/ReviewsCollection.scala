package com.nlpagentsystem.app

import com.nlpagentsystem.{ Feature, Review }
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.{ IndexModel, IndexOptions, Indexes }
import org.mongodb.scala.{ MongoClient, MongoCollection }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ReviewsCollection {
  def create(connectionString: String): MongoCollection[Review] = {
    val client = MongoClient(connectionString)
    val codecRegistry = fromRegistries(
      fromProviders(classOf[Review]),
      fromProviders(classOf[Feature]),
      DEFAULT_CODEC_REGISTRY
    )
    val db = client.getDatabase("argumentation-agents").withCodecRegistry(codecRegistry)
    implicit val collection: MongoCollection[Review] = db.getCollection("reviews")
    //    addFakeData()
    addIndex()
    collection
  }

  private def addFakeData()(implicit collection: MongoCollection[Review]) = {
    val fakeReviews = Seq()
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

  private def addIndex()(implicit collection: MongoCollection[Review]) = {
    val future = collection.createIndexes(
      Seq(
        IndexModel(
          Indexes.ascending("product_id", "review_id"),
          IndexOptions().background(false).unique(true)
        )
      )
    ).head()
    Await.result(future, Duration.Inf)
  }
}
