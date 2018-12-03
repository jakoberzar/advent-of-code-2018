import scala.io.Source

object Day03 {
  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList
    val claims = parseInput(input)

    star1(claims)
    star2(claims)
  }

  def star1(claims: List[Claim]): Unit = {
    val takenInches = getFabricWithClaims(claims)
      .flatten
      .count(_ > 1)

    println(takenInches)
  }

  def star2(claims: List[Claim]): Unit = {
    val fabric = getFabricWithClaims(claims)

    val completeClaim = claims
      .find(_.coordinates.forall { case (x, y) => fabric(y)(x) == 1 })
      .get

    println(completeClaim.id)
  }

  case class Claim(id: Int, left: Int, top: Int, width: Int, height: Int) {
    val coordinates: IndexedSeq[(Int, Int)] = for {
      x <- left until left + width
      y <- top until top + height
    } yield (x, y)
  }

  def parseInput(input: List[String]): List[Claim] = {
    input.map(line => {
      val spaces = line.split(" ")
      val id = spaces(0).drop(1).toInt
      val (left, top) = spaces(2).split(",") match {
        case Array(x, y) => (x.toInt, y.dropRight(1).toInt)
      }
      val (width, height) = spaces(3).split("x") match {
        case Array(w, h) => (w.toInt, h.toInt)
      }

      Claim(id, left, top, width, height)
    })
  }

  def getFabricWithClaims(claims: List[Claim]): Array[Array[Int]] = {
    val fabric = Array.ofDim[Int](1000, 1000)

    for {
      claim <- claims
      (x, y) <- claim.coordinates
    } fabric(y)(x) += 1

    fabric
  }
}
