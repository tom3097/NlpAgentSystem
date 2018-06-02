package com.nlpagentsystem

import scala.collection.mutable

class BasicSolver extends Solver {
  val argumentTree = new mutable.HashSet[Node]()

  override def addArgument(newArgument: Argument): Unit = {
    val newNode = Node(newArgument, new mutable.HashSet[Node](), new mutable.HashSet[Node]())

    val existingNode = argumentTree.find(node => node.argument.componentName == newArgument.componentName)
    if (existingNode.isEmpty) {
      argumentTree.add(newNode)
    } else {
      existingNode.get.add(newNode)
    }
  }

  override def solve(): Result = {
    val score = argumentTree.view.map(_.getScore).sum

    def nodeToMap(node: Node): Map[String, Any] = {
      val nodeMap = Map("argument" -> node.argument, "children" -> Array.ofDim[Map[String, Any]](node.counterArguments.size + node.supportingArguments.size))

      var arrayIndex = 0
      node.supportingArguments.foreach(supportingArgument => {
        nodeMap("children").asInstanceOf[Array[Map[String, Any]]](arrayIndex) = nodeToMap(supportingArgument)
        arrayIndex += 1
      })
      node.counterArguments.foreach(counterArgument => {
        nodeMap("children").asInstanceOf[Array[Map[String, Any]]](arrayIndex) = nodeToMap(counterArgument)
        arrayIndex += 1
      })
      nodeMap
    }

    val resultTree = mutable.Map[String, Any]()
    argumentTree.foreach(node => {
      resultTree.update(node.argument.componentName, nodeToMap(node))
    })

    Result(score, resultTree.toMap)
  }
}

final case class Node(argument: Argument, counterArguments: mutable.HashSet[Node], supportingArguments: mutable.HashSet[Node]) {
  var hasCounterargument: Boolean = false

  def add(newNode: Node) {
    if (newNode.argument.polarityScore.signum != argument.polarityScore.signum) {
      if (supportingArguments.exists(a => !a.hasCounterargument)) {
        supportingArguments.find(a => !a.hasCounterargument).get.add(newNode)
      } else if (counterArguments.exists(a => a.hasCounterargument)) {
        counterArguments.find(a => a.hasCounterargument).get.add(newNode)
      } else {
        counterArguments.add(newNode)
      }
    } else {
      if (counterArguments.exists(a => !a.hasCounterargument)) {
        counterArguments.find(a => !a.hasCounterargument).get.add(newNode)
      } else if (supportingArguments.exists(a => a.hasCounterargument)) {
        supportingArguments.find(a => a.hasCounterargument).get.add(newNode)
      } else {
        supportingArguments.add(newNode)
      }

      if (counterArguments.forall(a => a.hasCounterargument) && !supportingArguments.exists(a => a.hasCounterargument)) {
        hasCounterargument = false
      } else {
        hasCounterargument = true
      }
    }
  }
  def getScore: Double = {
    counterArguments.view.map(_.getScore).sum + supportingArguments.view.map(_.getScore).sum + argument.polarityScore
  }
}
