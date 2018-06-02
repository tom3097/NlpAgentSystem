package com.nlpagentsystem

case class Result(score: Double, tree: Map[String, Any])

trait Solver {
  def addArgument(argument: Argument)
  def solve(): Result
}
