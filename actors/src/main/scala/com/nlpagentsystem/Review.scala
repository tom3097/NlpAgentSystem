package com.nlpagentsystem

import org.mongodb.scala.bson.ObjectId

object Review {
  def apply(productId: String, reviewId: String): Review =
    Review(new ObjectId(), productId, reviewId)
}
case class Review(_id: ObjectId, productId: String, reviewId: String)