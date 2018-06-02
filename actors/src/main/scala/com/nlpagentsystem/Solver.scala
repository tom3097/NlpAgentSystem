package com.nlpagentsystem

import scala.collection.mutable

case class Result(score: Double, tree: mutable.Map[String, Any])

trait Solver {
  def addArgument(argument: Argument)
  def solve(): Result
}
