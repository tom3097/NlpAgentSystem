package com.nlpagentsystem

import org.mongodb.scala.bson.ObjectId

case class Feature(
  name: String,
  description: String,
  polarity_score: Double
)

case class Review(
  _id: ObjectId,
  product_id: String,
  review_id: String,
  features: List[Feature]
)