package com.nlpagentsystem

object BasicSolver {
  def create(): BasicSolver = {
    new BasicSolver()
  }
}
class BasicSolver extends Solver {
  var arguments: Map[String, List[Node]] = Map.empty

  override def addArgument(argument: Argument): Unit = {
    if (arguments.isEmpty || !arguments.isDefinedAt(argument.componentName)) {
      arguments = arguments.+(
        argument.componentName -> List(
          Node(
            kind = getKind(null, argument),
            argument = argument.description,
            score = argument.polarityScore
          )
        )
      )
    } else {
      val currentList = arguments.get(argument.componentName).orNull
      val newList = Node(
        kind = getKind(currentList.head, argument),
        argument = argument.description,
        score = argument.polarityScore
      ) :: currentList
      arguments = arguments.+(argument.componentName -> newList)
    }
  }

  def getKind(prevNode: Node, newArgument: Argument): String = {
    if (prevNode == null) {
      if (newArgument.polarityScore.signum == 1) "support" else "counter"
    } else {
      if (prevNode.score.signum == newArgument.polarityScore.signum) "support" else "counter"
    }
  }

  override def solve(): Result = {
    arguments.foreach {
      case (componentName, nodes) => arguments = arguments.+(componentName -> nodes.reverse)
    }
    var score = 0.0
    arguments.foreach {
      case (_, nodes) => nodes.foreach(
        node => score += node.score
      )
    }
    Result(
      score = score,
      arguments = arguments,
      conclusion = "Laptop is ok"
    )
  }
}