import java.io.File

fun main(args: Array<String>) {
    val input = getInput()
    star1(input)
    star2(input)
}

fun star1(input: List<String>) {
    val frequencies = input.map { countLetters(it) }
    val have2 = frequencies.filter { it.contains(2) }.count()
    val have3 = frequencies.filter { it.contains(3) }.count()
    println(have2 * have3)
}

fun getInput(): List<String> {
    return File("input.txt").readLines()
}

fun countLetters(word: String): Collection<Int> {
    val map = HashMap<Char, Int>()

    for (char in word) {
        val current = map.getOrElse(char) { 0 }
        map.put(char, current + 1)
    }

    return map.values
}

fun star2(input: List<String>) {
    val pair = getPair(input)
    val sameLetters = getSameLetters(pair.first, pair.second)
    println(sameLetters)
}

fun compareWords(first: String, second: String): Boolean {
    return first.zip(second)
            .filter { it.first != it.second }
            .count() == 1
}

fun getPair(input: List<String>): Pair<String, String> {
    for ((idx, line) in input.withIndex()) {
        input.subList(idx + 1, input.lastIndex)
                .filter { compareWords(line, it) }
                .forEach { return Pair(line, it) }
    }
    throw Exception("No pair found")
}

fun getSameLetters(first: String, second: String): String {
    return first.zip(second)
            .filter { it.first == it.second }
            .map { it.first }
            .joinToString("")
}

