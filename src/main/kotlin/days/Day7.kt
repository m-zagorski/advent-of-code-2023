package days

import readLines
import kotlin.math.max

object Day7 {

    operator fun invoke() {
        println("🎄Day 7 🎄")
        print("Part 1: ")
        part1(readLines("7"))
        println()
        print("Part 2: ")
        part2(readLines("7"))
    }

    data class ResultHand(val hand: String, val bid: Long, val strength: Int)

    private fun part1(input: List<String>) {
        val result = input.map { line ->
            val (hand, bid) = line.split(" ")
            ResultHand(hand, bid.toLong(), determineStrength(hand))
        }
            .groupBy { it.strength }
            .values
            .asSequence()
            .map { samePairs -> samePairs.sortedWith(strCompare(false)) }
            .sortedBy { it.first().strength }
            .flatten()
            .mapIndexed { index, resultHand -> (index + 1) * resultHand.bid }
            .sum()

        print(result)
    }

    private fun part2(input: List<String>) {
        val result = input.map { line ->
            val (hand, bid) = line.split(" ")
            ResultHand(hand, bid.toLong(), handleJokers(hand))
        }.groupBy { it.strength }
            .values
            .asSequence()
            .map { samePairs -> samePairs.sortedWith(strCompare(true)) }
            .sortedBy { it.first().strength }
            .flatten()
            .mapIndexed { index, resultHand -> (index + 1) * resultHand.bid }
            .sum()


        println(result)
    }

    private const val cardsWithoutJokers = "AKQT98765432"

    private val winning = mapOf(
        listOf(5) to 7,
        listOf(4, 1) to 6,
        listOf(3, 2) to 5,
        listOf(3, 1, 1) to 4,
        listOf(2, 2, 1) to 3,
        listOf(2, 1, 1, 1) to 2,
        listOf(1, 1, 1, 1, 1) to 1
    )

    private fun strCompare(withJokers: Boolean) = Comparator<ResultHand> { h1, h2 ->
        var result = 0
        for (item in h1.hand.zip(h2.hand)) {
            val (f, s) = item
            if (f == s) continue
            if (f.toRange(withJokers) > s.toRange(withJokers)) {
                result = 1
                break
            } else {
                result = -1
                break
            }
        }
        result
    }

    private fun Char.toRange(withJokers: Boolean): Int {
        return if (isDigit()) return digitToInt() - 1
        else {
            when (this) {
                'A' -> 13
                'K' -> 12
                'Q' -> 11
                'J' -> if (withJokers) 0 else 10
                'T' -> 9
                else -> error("Unknown card $this")
            }
        }
    }

    private fun determineStrength(hand: String): Int {
        val groups = hand.asIterable().groupBy { it }.values.map { it.size }.sortedByDescending { it }
        return winning[groups] ?: error("Unknown combination $hand")
    }

    private fun handleJokers(hand: String): Int {
        val jokers: List<MatchResult> = "J".toRegex().findAll(hand).toList()

        return if (jokers.toList().size == 5) 7
        else generatePermutations(hand, jokers, -1)
    }

    private fun generatePermutations(hand: String, jokers: List<MatchResult>, winning: Int): Int {
        if (jokers.isEmpty()) {
            return max(winning, determineStrength(hand))
        }
        val firstJoker = jokers.first()

        return cardsWithoutJokers.maxOf {
            val range = firstJoker.range.first
            val replaced = hand.replaceRange(range, range + 1, it.toString())
            generatePermutations(replaced, jokers.minus(firstJoker), winning)
        }
    }
}