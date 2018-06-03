package com.nlpagentsystem

case class Node(identifier: Int, who: String, kind: String, argument: String, score: Double)
case class Result(score: Double, conclusion: String, arguments: Map[String, List[Node]])

trait Solver {
  def addArgument(argument: Argument, who: String)
  def solve(): Result
}
