import scala.annotation.tailrec
import scala.io.Source

object Day07Immutable {
  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList

    star1(parseInput(input))
    star2(parseInput(input))
  }

  def star1(nodes: List[Node]): Unit = {
    @tailrec
    def processNodes(nodes: List[Node], done: List[Node]): List[Node] = nextTasks(nodes) match {
      case Nil => done.reverse
      case head :: _ =>
        val toProcess = nodes
          .filter(_ != head)
          .map(_.withoutRequired(List(head)))
        processNodes(toProcess, head :: done)
    }

    val done = processNodes(nodes.sortBy(_.id), Nil)
    println(done.map(_.id).mkString(""))
  }

  def star2(nodes: List[Node]): Unit = {
    @tailrec
    def processNodes(nodes: List[Node], workers: List[(Int, Option[Node])], timeSpent: Int): Int = {
      if (nextTasks(nodes) == Nil && workers.forall { case (t, _) => t == 0 }) {
        timeSpent
      } else {
        val tasksJustDone = workers
          .filter { case (time, _) => time == 1 }
          .map { case (_, task) => task.get }

        val newNodes = nodes.map(_.withoutRequired(tasksJustDone))

        val newWorkers = workers.map { case (time, task) =>
          if (time == 1) (0, None)
          else if (time > 1) (time - 1, task)
          else (time, task)
        }

        val (finalWorkers, finalNodes) = newWorkers.foldLeft(List.empty[(Int, Option[Node])], newNodes) {
          case ((workers, nodes), (time, task)) =>
            val freeTasks = nextTasks(nodes)
            val (thisW, newNs) = if (time == 0 && freeTasks != Nil) {
              val head = freeTasks.head
              val nodesLeft = nodes.filter(_ != head)
              ((head.getTime, Some(head)), nodesLeft)
            } else {
              ((time, task), nodes)
            }
            (thisW :: workers, newNs)
        }

        processNodes(finalNodes, finalWorkers, timeSpent + 1)
      }
    }

    val workers: List[(Int, Option[Node])] = (1 to 5).toList.map(_ => (0, None))
    val time: Int = processNodes(nodes.sortBy(_.id), workers, 0)
    println(time - 1)
  }

  def parseInput(input: List[String]): List[Node] = {
    val nodeMap = input
      .map(parseLine)
      .flatMap { case (a, b) => List(a, b) }
      .distinct
      .foldLeft(Map.empty[Char, Node])((map, x) => map + (x -> Node(x)))

    input
      .map(parseLine)
      .foreach { case (from, to) =>
        nodeMap(to).addRequisites(nodeMap(from))
      }

    nodeMap.values.toList
  }

  def parseLine(line: String): (Char, Char) = (line.charAt(5), line.charAt(36))

  // Assumes tasks are already alphabetically sorted
  def nextTasks(tasks: List[Node]): List[Node] = tasks.filter(x => x.require.isEmpty)

  case class Node(id: Char, var require: List[Node] = Nil) {
    // This is the only mutation here - it's hard to make a graph of nodes when
    // they are all immutable... Creating a new node makes other's reference obsolete...
    def addRequisites(node: Node): Unit = {
      require = node :: require
    }

    def withoutRequired(others: List[Node]): Node =
      Node(id, require.filterNot(node => others.contains(node)))

    def getTime: Int = id.toInt - 'A'.toInt + 1 + 60

    override def toString: String = "Node - " + id

    override def equals(that: Any): Boolean = that match {
      case Node(id, _) => id == this.id
      case _ => false
    }
  }

}
