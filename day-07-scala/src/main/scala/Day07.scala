import scala.io.Source
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

// TODO: Remove mutation? :)
// - Look into getters and setters and things
object Day07 {
  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList

    star1(parseInput(input))
    star2(parseInput(input))
  }

  def star1(nodes: Iterable[Node]): Unit = {
    val queue = ListBuffer() ++ nodes.filter(_.require.isEmpty).toList
    val done: ListBuffer[Node] = mutable.ListBuffer()
    while (queue.nonEmpty) {
      val head = queue.minBy(_.id)
      queue -= head
      head.process()
      queue ++= head.next.filter(_.require.isEmpty)
      done += head
    }
    println(done.map(_.id).mkString(""))
  }

  def star2(nodes: Iterable[Node]): Unit = {
    val queue = mutable.ListBuffer() ++ nodes.filter(_.require.isEmpty).toList
    val done: ListBuffer[Node] = mutable.ListBuffer()
    var time = 0
    var workers: List[(Int, Option[Node])] = (1 to 5).toList.map(_ => (0, None))
    while (queue.nonEmpty || workers.exists { case (t, _) => t > 0 }) {
      workers = workers.map { case (time, task) =>
        if (time == 1) {
          val taskVal = task.get
          taskVal.process()
          queue ++= taskVal.next.filter(_.require.isEmpty)
          done += taskVal
          (0, None)
        } else if (time > 1) {
          (time - 1, task)
        } else {
          (time, task)
        }
      }.map { case (time, task) =>
        if (time == 0 && queue.nonEmpty) {
          val head = queue.minBy(_.id)
          queue -= head
          (head.getTime, Some(head))
        } else {
          (time, task)
        }
      }

      time += 1
    }
    println(done.map(_.id).mkString(""))
    println(time - 1)
  }

  def parseInput(input: List[String]): Iterable[Node] = {
    val nodeMap = mutable.HashMap[String, Node]()

    input
      .map(parseLine)
      .flatMap { case (a: String, b: String) => List(a, b) }
      .distinct
      .foreach(x => nodeMap += (x -> Node(x)))

    input
      .map(parseLine)
      .foreach { case (from, to) =>
        nodeMap(from).addNext(nodeMap(to))
        nodeMap(to).addRequisites(nodeMap(from))
      }

    nodeMap.values
  }

  def parseLine(line: String): (String, String) = (line.charAt(5).toString, line.charAt(36).toString)

  case class Node(
                   id: String,
                   var done: Boolean = false,
                   var next: List[Node] = Nil,
                   var require: List[Node] = Nil
                 ) {
    def addNext(node: Node): Unit = {
      next = node :: next
    }

    def removeNext(node: Node): Unit = {
      next = next.filter(_ != node)
    }

    def addRequisites(node: Node): Unit = {
      require = node :: require
    }

    def removeRequisites(node: Node): Unit = {
      require = require.filter(_ != node)
    }

    def process(): Unit = {
      done = true
      next.foreach(_.removeRequisites(this))
    }

    def getTime: Int = id.charAt(0).toInt - 'A'.toInt + 1 + 60

    override def toString: String = "Node - " + id
  }
}