package days

import numbersRegex
import readLines

object Day3 {

    operator fun invoke() {
        println("🎄Day 3 🎄")
        print("Part 1: ")
        part1(readLines("3"))
        println()
        print("Part 2: ")
        part2(readLines("3"))
    }

    private data class Position(val x: Int, val y: Int)
    private data class Input(val number: String, val pos: Position)
    private data class Input2(val number: String, val pos: List<Position>)

    private val aroundCoordinates = listOf(
        -1 to 0,
        1 to 0,
        -1 to -1,
        0 to -1,
        1 to -1,
        -1 to 1,
        0 to 1,
        1 to 1
    )

    private fun part1(input: List<String>) {
        val numbers = mutableListOf<Input>()
        val symbolsPositions = mutableListOf<Position>()
        input.forEachIndexed { row, line ->
            numbersRegex.findAll(line).forEach { match ->
                numbers.add(Input(match.value, Position(match.range.first, row)))
            }
            line.asIterable().forEachIndexed { index, c ->
                if (!c.isDigit() && c != '.') {
                    symbolsPositions.add(Position(index, row))
                }
            }
        }

        val result = numbers.sumOf { (number, pos) ->
            val contains = number.indices.any {
                val newPos = Position(pos.x + it, pos.y)
                checkIfSign(newPos, symbolsPositions)
            }
            if (!contains) 0
            else number.toLong()
        }
        println(result)
    }

    private fun part2(input: List<String>) {
        val numbers = mutableListOf<Input2>()
        val gearsPositions = mutableListOf<Position>()
        input.forEachIndexed { row, line ->
            val regex = "(\\d+|\\*)".toRegex()
            regex.findAll(line).forEach { match ->
                val value = match.value
                if (value == "*") {
                    gearsPositions.add(Position(match.range.first, row))
                } else {
                    val positions = (match.range.first..match.range.last).map { Position(it, row) }
                    numbers.add(Input2(value, positions))
                }
            }
        }

        val result = gearsPositions.sumOf {
            val result = getNumberOrNull(it, numbers)
            if (result.size == 2) {
                result.first().number.toLong() * result.last().number.toLong()
            } else {
                0L
            }
        }
        println(result)
    }

    private fun checkIfSign(pos: Position, signs: List<Position>): Boolean {
        return aroundCoordinates.any { (x, y) ->
            val newPos = Position(pos.x + x, pos.y + y)
            signs.contains(newPos)
        }
    }

    private fun getNumberOrNull(pos: Position, numbers: List<Input2>): Set<Input2> {
        return aroundCoordinates.mapNotNull { (x, y) ->
            val newPos = Position(pos.x + x, pos.y + y)
            numbers.find { (_, positions) ->
                positions.contains(newPos)
            }
        }.toSet()
    }
}