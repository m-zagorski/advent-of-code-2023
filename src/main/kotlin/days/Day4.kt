package days

import numbersRegex
import readLines

object Day4 {

    operator fun invoke() {
        println("🎄Day 4 🎄")
        print("Part 1: ")
        part1(readLines("4"))
        println()
        print("Part 2: ")
        part2(readLines("4"))
    }

    private fun part1(input: List<String>) {
        val result = input.sumOf { line ->
            val common = line.split(":").last().split(" | ")
                .map { numbersRegex.findAll(it.trim()).map { it.value.toInt() }.toSet() }
                .reduce { acc, ints -> acc.intersect(ints) }

            common.drop(1).fold(if (common.isEmpty()) 0L else 1L) { acc, _ -> acc * 2 }
        }

        print(result)
    }

    private fun part2(input: List<String>) {
        val cardsCount = mutableMapOf<Int, Int>()
        input.forEach { line ->
            val (game, numbers) = line.split(":")
            val gameNumber = numbersRegex.findAll(game).first().value.toInt()
            val common = numbers.split(" | ")
                .map { numbersRegex.findAll(it.trim()).map { it.value.toInt() }.toSet() }
                .reduce { acc, ints -> acc.intersect(ints) }

            val currentGame = cardsCount.getOrDefault(gameNumber, 0)
            val numberOfCurrentGameCards = currentGame + 1
            cardsCount[gameNumber] = numberOfCurrentGameCards

            (gameNumber + 1..common.size + gameNumber).forEach { cardNumber ->
                val currentCount = cardsCount.getOrDefault(cardNumber, 0)
                cardsCount[cardNumber] = currentCount + numberOfCurrentGameCards
            }
        }

        val result = cardsCount.values.sumOf { it }
        println(result)
    }
}