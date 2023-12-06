package days

import readLines

object Day1 {

    operator fun invoke() {
        println("🎄Day 1 🎄")
        print("Part 1: ")
        part1(readLines("1"))
        println()
        print("Part 2: ")
        part2(readLines("1"))
    }

    private fun part1(input: List<String>) {
        val r = "[0-9]".toRegex()
        val result = input.sumOf { l ->
            val results = r.findAll(l)
            "${results.first().value}${results.last().value}".toInt()
        }
        print(result)
    }

    private fun part2(input: List<String>) {
        val textDigits = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9"
        )
        val reg = "(?=([0-9]|one|two|three|four|five|six|seven|eight|nine))".toRegex()
        val result = input.sumOf { l ->
            val results = reg.findAll(l).map { it.groupValues[1] }.toList()
            val f = results.first().let { textDigits.getOrDefault(it, it) }
            val s = results.lastOrNull()?.let { textDigits.getOrDefault(it, it) } ?: f
            "$f$s".toInt()
        }
        println(result)
    }
}