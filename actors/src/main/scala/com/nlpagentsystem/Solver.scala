package com.nlpagentsystem

case class Node(kind: String, argument: String, score: Double)
case class Result(score: Double, conclusion: String, arguments: Map[String, List[Node]])

trait Solver {
  def addArgument(argument: Argument)
  def solve(): Result
}
