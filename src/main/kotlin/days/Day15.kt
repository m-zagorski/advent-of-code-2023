package days

import readWholeFile

object Day15 {

    operator fun invoke() {
        println("🎄Day 15 🎄")
        print("Part 1: ")
        part1(readWholeFile("15"))
        println()
        print("Part 2: ")
        part2(readWholeFile("15"))
    }

    private fun part1(input: String) {
        val sum = input.split(",").sumOf(::calculateAscii)
        print(sum)
    }

    private fun part2(input: String) {
        val boxes: MutableMap<Int, MutableMap<String, Int>> = mutableMapOf()
        input.split(",").forEach {instruction ->
            val word = "[a-z]+".toRegex().findAll(instruction).map { it.value }.first()
            val sign = instruction.elementAt(word.length)
            val boxNumber = calculateAscii(word)

            when (sign) {
                '=' -> {
                    val number = instruction.elementAt(word.length + 1).digitToInt()
                    val currentLenses = boxes.getOrDefault(boxNumber, mutableMapOf())
                    currentLenses[word] = number
                    boxes[boxNumber] = currentLenses
                }

                '-' -> {
                    val currentLenses = boxes.getOrDefault(boxNumber, mutableMapOf())
                    currentLenses.remove(word)
                    boxes[boxNumber] = currentLenses
                }

                else -> error("Unknown $word $sign")
            }
        }

        val sum = boxes.entries.sumOf { (k, v) ->
            val f: Long = k + 1L
            v.values.withIndex().sumOf { (index, i) ->
                f * (index + 1) * i
            }
        }
        println(sum)
    }

    private fun calculateAscii(word: String): Int {
        return word.fold(0) { acc, c ->
            val value = acc + c.code
            val multiplied = value * 17
            multiplied % 256
        }
    }
}