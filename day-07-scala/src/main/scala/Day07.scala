import scala.io.Source
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

// TODO: Remove mutation? :)
// - Look into getters and setters and things
object Day07 {
  var nodeMap: mutable.HashMap[String, Node] = mutable.HashMap[String, Node]()

  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList
    parseInput(input)
    star1()
  }

  def star1(): Unit = {
    val queue = mutable.ListBuffer() ++ nodeMap.values.filter(_.require.isEmpty).toList
    val done: ListBuffer[Node] = mutable.ListBuffer()
    while (queue.nonEmpty) {
      val head = queue.minBy(_.id)
      queue -= head
      head.process()
      queue ++= head.next.filter(_.require.isEmpty)
      println(head.id)
      done += head
    }
    println(done.map(_.id).mkString(""))

  }

  def parseInput(input: List[String]): mutable.HashMap[String, Node] = {
    input
      .map(parseLine)
      .flatMap { case (a:String, b: String) => List(a, b) }
      .distinct
      .foreach(x => nodeMap += (x -> Node(x)))

    input
      .map(parseLine)
      .foreach(in => {
        val (from, to) = in
        nodeMap(from).addNext(nodeMap(to))
        nodeMap(to).addRequisites(nodeMap(from))
      })

    nodeMap
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
  }
}

