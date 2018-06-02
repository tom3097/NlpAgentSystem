package com.nlpagentsystem

import org.mongodb.scala.bson.ObjectId

case class Feature(
  name: String,
  description: String,
  polarityScore: Double
)

case class Review(
  _id: ObjectId,
  productId: String,
  reviewId: String,
  features: List[Feature]
)