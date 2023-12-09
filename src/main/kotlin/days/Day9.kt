package days

import readLines

object Day9 {

    operator fun invoke() {
        println("🎄Day 9 🎄")
        print("Part 1: ")
        part1(readLines("9"))
        println()
        print("Part 2: ")
        part2(readLines("9"))
    }

    private fun part1(input: List<String>) {
        val result = input.sumOf { line ->
            "-?\\d+".toRegex()
                .findAll(line)
                .map { it.value.toInt() }
                .toList()
                .createValue()
        }

        print(result)
    }

    private fun part2(input: List<String>) {
        val result = input.sumOf { line ->
            "-?\\d+".toRegex()
                .findAll(line)
                .map { it.value.toInt() }
                .toList()
                .reversed()
                .createValue()
        }

        print(result)
    }

    private tailrec fun List<Int>.createValue(lastIndex: Int = 0): Int {
        if (all { it == 0 }) return lastIndex

        return windowed(2, 1).map { (first, second) -> second - first }.createValue(
            lastIndex = lastIndex + last()
        )
    }
}