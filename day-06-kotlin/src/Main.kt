import java.io.File

typealias Coordinates = List<Pair<Int, Int>>;

fun main() {
    val input = getInput()
    val coords = parseInput(input)
    star1(coords)
    star2(coords)
}

fun star1(coords: Coordinates) {
    val (xBound, yBound) = getSmallestArea(coords)
    val amounts = getAmountClosestInArea(coords, (2..xBound), (2..yBound))
    val infiniteAreas = findInfinite(coords, xBound, yBound)

    val max = amounts
            .withIndex()
            .filterNot { infiniteAreas.contains(it.index) }
            .maxBy { it.value }

    println("Size of largest area, ${max?.index} is ${max?.value}")
}

fun star2(coords: Coordinates) {
    val (xBound, yBound) = getSmallestArea(coords)

    val count = twoD(0..xBound, 0..yBound).count { loc ->
        coords.sumBy { coord ->
            Math.abs(coord.first - loc.first) + Math.abs(coord.second - loc.second)
        } < 10000
    }

    println("Size of the region with distance < 10000 is $count")
}


fun getInput(): List<String> {
    return File("input.txt").readLines()
}

fun parseInput(input: List<String>): Coordinates {
    return input.map {
        val split = it.split(", ")
        Pair(split[0].toInt(), split[1].toInt())
    }
}

fun getSmallestArea(coords: Coordinates): Pair<Int, Int> {
    return coords
            .unzip()
            .toList()
            .map { it.max() }
            .mapNotNull { it }
            .zipWithNext()
            .first()
}

fun getAmountClosestInArea(coords: Coordinates, rangeX: Iterable<Int>, rangeY: Iterable<Int>): IntArray {
    val amount = IntArray(coords.size)

    twoD(rangeX, rangeY)
            .map { getClosest(coords, it.first, it.second) }
            .filter { it > -1 }
            .forEach { amount[it]++ }

    return amount
}

fun getClosest(coords: Coordinates, x: Int, y: Int): Int {
    return coords
            .map { Math.abs(it.first - x) + Math.abs(it.second - y) }
            .foldIndexed(Pair(-1, Int.MAX_VALUE)) { idx, acc, d ->
                when {
                    d < acc.second -> Pair(idx, d)
                    d == acc.second -> Pair(-1, d)
                    else -> acc
                }
            }.first
}

fun findInfinite(coords: Coordinates, xBound: Int, yBound: Int): List<Int> {
    val amountInner = getAmountClosestInArea(coords, 1..xBound + 1, listOf(1, yBound + 1)) // vertical
            .zip(getAmountClosestInArea(coords, listOf(1, xBound + 1), 2..yBound), Int::plus) // horizontal

    val amountOuter = getAmountClosestInArea(coords, 0..xBound + 2, listOf(0, yBound + 2)) // vertical
            .zip(getAmountClosestInArea(coords, listOf(0, xBound + 2), 1..yBound + 1), Int::plus) // horizontal

    return coords.indices.filter {
        amountOuter[it] > amountInner[it] || amountOuter[it] == amountInner[it] && amountInner[it] != 0
    }
}

fun twoD(rangeX: Iterable<Int>, rangeY: Iterable<Int>): Iterable<Pair<Int, Int>> {
    return rangeX.flatMap { x -> rangeY.map { y -> Pair(x, y) } }
}