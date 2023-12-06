package days

import numbersRegex
import readLines

object Day6 {

    operator fun invoke() {
        println("🎄Day 6 🎄")
        print("Part 1: ")
        part1(readLines("6"))
        println()
        print("Part 2: ")
        part2(readLines("6"))
    }

    private fun part1(input: List<String>) {
        val pairs = input.map { numbersRegex.findAll(it).map { it.value.toInt() }.toList() }.let { (time, distance) ->
            time.zip(distance)
        }

        val result = pairs.map { (currentTime, totalDistance) ->
            (0..currentTime).count { holdMs ->
                holdMs * (currentTime - holdMs) > totalDistance
            }
        }.reduce(operation = Int::times)

        print(result)
    }

    private fun part2(input: List<String>) {
        val (time, distance) = input.map {
            numbersRegex.findAll(it).map { it.value }.toList().joinToString(separator = "").toLong()
        }

        val result = (0..time).count { holdMs ->
            holdMs * (time - holdMs) > distance
        }

        println(result)
    }
}