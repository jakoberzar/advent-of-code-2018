import scala.io.Source

object Day03 {
  def main(args: Array[String]): Unit = {
    val input = Source.fromResource("input.txt").getLines().toList
    star1(input)
    star2(input)
  }

  def star1(input: List[String]): Unit = {
    val claims = parseInput(input)
    val taken = getFabricWithClaims(claims).map(_.count(_ > 1)).sum

    println(taken)
  }

  def star2(input: List[String]): Unit = {
    val claims: List[Claim] = parseInput(input)
    val fabric: Array[Array[Int]] = getFabricWithClaims(claims)

    val completeClaim: Claim = claims.find(
      _.coordinates.forall { case (x, y) => fabric(y)(x) == 1 }
    ) match {
      case Some(claim) => claim
      case None => throw new Exception("Could not find any such claim!")
    }

    println(completeClaim.id)
  }

  case class Claim(id: Int, left: Int, top: Int, width: Int, height: Int) {
    val verticalRange: Range = top until top + height
    val horizontalRange: Range = left until left + width
    def coordinates: IndexedSeq[(Int, Int)] = for {
      x <- horizontalRange
      y <- verticalRange
    } yield (x ,y)
  }

  def parseInput(input: List[String]): List[Claim] = {
    input.map((line: String) => {
      val spaces = line.split(" ")
      val id = spaces(0).substring(1).toInt;
      val (left, top) = spaces(2).split(",") match  {
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
      x <- claim.horizontalRange
      y <- claim.verticalRange
    } fabric(y)(x) += 1

    fabric
  }
}
