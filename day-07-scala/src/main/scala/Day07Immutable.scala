import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Source

object Day07Immutable {
  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList

    star1(parseInput(input))
    // ABDCJLFMNVQWHIRKTEUXOZSYPG
    // ABDCJLFMNVQWHIRKTEUXOZSYPG
    star2(parseInput(input)) // 896
  }

  def star1(nodes: List[Node]): Unit = {
    @tailrec
    def processNodes(nodes: List[Node], done: List[Node]): List[Node] = nextTasks(nodes) match {
      case Nil => done.reverse
      case head :: _ =>
        val toProcess = nodes
          .filter(_.id != head.id)
          .map(_.withoutRequire(List(head)))
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

        val newNodes = nodes
          .map(_.withoutRequire(tasksJustDone))

        val newWorkers = workers.map { case (time, task) =>
          if (time == 1) (0, None)
          else if (time > 1) (time - 1, task)
          else (time, task)
        }

        val (finalWorkers, finalNodes) = newWorkers.foldLeft(List.empty[(Int, Option[Node])], newNodes) {
          case ((ws, ns), (time, task)) =>
            val freeTasks = nextTasks(ns)
            val (thisW, newNs) = if (time == 0 && freeTasks != Nil) {
              val head = freeTasks.head
              val nodesLeft = ns.filter(_.id != head.id)
              ((head.getTime, Some(head)), nodesLeft)
            } else {
              ((time, task), ns)
            }
            (thisW :: ws, newNs)
        }

        processNodes(finalNodes, finalWorkers, timeSpent + 1)
      }
    }

    val workers: List[(Int, Option[Node])] = (1 to 5).toList.map(_ => (0, None))
    val time: Int = processNodes(nodes.sortBy(_.id), workers, 0)
    println(time - 1)
  }

  def parseInput(input: List[String]): List[Node] = {
    val nodeMap = mutable.HashMap[String, Node]()

    input
      .map(parseLine)
      .flatMap { case (a: String, b: String) => List(a, b) }
      .distinct
      .foreach(x => nodeMap += (x -> Node(x)))

    input
      .map(parseLine)
      .foreach { case (from, to) =>
        nodeMap(to).addRequisites(nodeMap(from))
      }

    nodeMap.values.toList
  }

  def parseLine(line: String): (String, String) = (line.charAt(5).toString, line.charAt(36).toString)

  // Assumes tasks are already alphabetically sorted
  def nextTasks(tasks: List[Node]): List[Node] = tasks.filter(x => x.require.isEmpty)

  case class Node(id: String, var require: List[Node] = Nil) {
    def addRequisites(node: Node): Unit = {
      require = node :: require
    }

    def getTime: Int = id.charAt(0).toInt - 'A'.toInt + 1 + 60

    def withoutRequire(others: List[Node]): Node =
      Node(id, require.filterNot(node => others.exists(_.id == node.id)))

    override def toString: String = "Node - " + id
  }
}
