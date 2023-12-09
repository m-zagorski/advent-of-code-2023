package days

import readWholeFile

object Day8 {

    operator fun invoke() {
        println("🎄Day 8 🎄")
        print("Part 1: ")
        part1(readWholeFile("8"))
        println()
        print("Part 2: ")
        part2(readWholeFile("8"))
    }

    data class Instructions(val left: String, val right: String)

    private fun part1(input: String) {
        val (ins, rawMap) = input.split("\n\n")

        val map = rawMap.split("\n").associate { line ->
            val (i, l, r) = "[A-Z]+".toRegex().findAll(line).map { it.value }.toList()

            i to Instructions(left = l, right = r)
        }

        val result = findNearestZ("AAA", ins, map) {
            it == "ZZZ"
        }
        print(result)
    }

    private fun part2(input: String) {
        val startingPoints = mutableListOf<String>()
        val (ins, rawMap) = input.split("\n\n")

        val map = rawMap.split("\n").associate { line ->
            val (i, l, r) = "[A-Z]+".toRegex().findAll(line).map { it.value }.toList()
            if (i.endsWith('A')) startingPoints.add(i)
            i to Instructions(left = l, right = r)
        }

        val result = startingPoints
            .map { startingPoint ->
                findNearestZ(startingPoint, ins, map) {
                    it.endsWith('Z')
                }
            }
            .reduce { acc, l -> lcm(acc, l) }

        println(result)

    }

    private fun gdc(a: Long, b: Long): Long {
        return if (b == 0L) a
        else gdc(b, a % b)
    }

    private fun lcm(a: Long, b: Long): Long {
        return (a / gdc(a, b)) * b
    }

    private fun findNearestZ(
        sp: String,
        ins: String,
        map: Map<String, Instructions>,
        endCondition: (String) -> Boolean
    ): Long {
        var idx = 0L
        var currentStep = sp
        while (true) {
            val instr = ins[(idx % ins.length).toInt()]
            val value = map[currentStep]!!

            currentStep = when (instr) {
                'L' -> value.left
                'R' -> value.right
                else -> error("Unknown instr $instr")
            }
            idx++
            if (endCondition(currentStep)) break
        }
        return idx
    }
}