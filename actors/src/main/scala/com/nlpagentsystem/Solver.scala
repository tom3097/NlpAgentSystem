package com.nlpagentsystem

case class Result(score: Double, tree: Map[String,AnyVal])

trait Solver {
  def addArgument(argument: Argument)
  def solve(): Result
}
