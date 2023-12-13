package days

import readLines

object Day12 {

    operator fun invoke() {
        println("🎄Day 12 🎄")
        print("Part 1: ")
        part1(readLines("12"))
        println()
        print("Part 2: ")
        part2(readLines("12"))
    }

    private fun part1(input: List<String>) {
        val result = input.sumOf { line ->
            val (damaged, groups) = line.split(" ")
            val intGroups = groups.split(',').map { it.toInt() }
            val result = replaceQuestionMarks(damaged, setOf(damaged.replace("?", ".")))
            val withLength = result.count { combination ->
                val sizes = "[#]+".toRegex().findAll(combination).map { it.value.length }.toList()
                sizes == intGroups
            }
            withLength
        }
        println(result)
    }


    private fun part2(input: List<String>) {
        println("I will get back to you ;)")
    }

    private fun replaceQuestionMarks(input: String, currentCombinations: Set<String>): Set<String> {
        val qm = "[?]".toRegex().findAll(input).toList()
        if (qm.isEmpty()) return currentCombinations


        val allSets = qm.map { result ->
            val part1 = input.substring(0, result.range.first).replace("?", ".")
            val part2 = input.substring(result.range.first, input.length).replaceFirst("?", "#")
            val newInput = part1 + part2
            replaceQuestionMarks(newInput, currentCombinations.plus(newInput))
        }.flatten().toSet()

        return allSets
    }
}