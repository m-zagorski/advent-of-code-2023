package days

import numbersRegex
import readLines
import kotlin.math.max

object Day2 {

    operator fun invoke() {
        println("🎄Day 2 🎄")
        print("Part 1: ")
        part1(readLines("2"))
        println()
        print("Part 2: ")
        part2(readLines("2"))
    }

    private fun part1(input: List<String>) {
        val c = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14
        )
        val result = input.sumOf { entry ->
            val (game, data) = entry.split(":")
            var gameValid = true
            data.split(";").forEach { set ->
                set.split(",").forEach { record ->
                    val (number, color) = record.trim().split(" ")
                    if (c.getOrDefault(color, Int.MAX_VALUE) < number.toInt()) {
                        gameValid = false
                    }
                }
            }
            if (gameValid) numbersRegex.findAll(game).first().value.toInt()
            else 0
        }

        print(result)
    }

    private fun part2(input: List<String>) {
        val result = input.sumOf { game ->
            val data = game.split(":").last()
            val minValues = mutableMapOf<String, Int>()
            data.split(";").forEach { set ->
                set.split(",").forEach { record ->
                    val (number, color) = record.trim().split(" ")
                    val currentValue = minValues.getOrDefault(color, 0)
                    minValues[color] = max(currentValue, number.toInt())
                }
            }
            minValues.values.reduce { acc, i -> acc * i }
        }

        println(result)
    }
}