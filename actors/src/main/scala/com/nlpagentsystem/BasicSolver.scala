package com.nlpagentsystem

object BasicSolver {
  def create(): BasicSolver = {
    new BasicSolver()
  }
}
class BasicSolver extends Solver {
  var currentIdentifier = 0
  var arguments: Map[String, List[Node]] = Map.empty

  override def addArgument(argument: Argument, who: String): Unit = {
    if (arguments.isEmpty || !arguments.isDefinedAt(argument.componentName)) {
      arguments = arguments.+(
        argument.componentName -> List(
          Node(
            identifier = getNextIdentifier(),
            who = who,
            kind = getKind(null, argument),
            argument = argument.description,
            score = argument.score
          )
        )
      )
    } else {
      val currentList = arguments.get(argument.componentName).orNull
      val newList = Node(
        identifier = getNextIdentifier(),
        who = who,
        kind = getKind(currentList.head, argument),
        argument = argument.description,
        score = argument.score
      ) :: currentList
      arguments = arguments.+(argument.componentName -> newList)
    }
  }

  def getKind(prevNode: Node, newArgument: Argument): String = {
    if (prevNode == null) {
      if (newArgument.score.signum == 1) "support" else "counter"
    } else {
      if (prevNode.score.signum == newArgument.score.signum) "support" else "counter"
    }
  }

  def getNextIdentifier(): Int = {
    currentIdentifier += 1
    currentIdentifier
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