package days

import readLines
import kotlin.math.abs

object Day18 {

    operator fun invoke() {
        println("🎄Day 18 🎄")
        print("Part 1: ")
        part1(readLines("18"))
        println()
        print("Part 2: ")
        part2(readLines("18"))
    }


    private fun part1(input: List<String>) {
        val startingPoint = Point(0, 0)
        val shapePoints = mutableListOf(startingPoint)
        var perimeter = 0

        input.map { line ->
            val (direction, metersRaw, _) = line.split(" ")
            val distance = metersRaw.toInt()
            val lastPoint = shapePoints.last()
            val np = direction.applyOffset(lastPoint, distance)
            perimeter += distance
            shapePoints.add(np)
        }

        val result = shoelaceArea(shapePoints) + perimeter / 2 + 1
        println(result)
    }

    private fun part2(input: List<String>) {
        val startingPoint = Point(0, 0)
        val shapePoints = mutableListOf(startingPoint)
        var perimeter = 0L

        input.map { line ->
            val (_, _, hex) = line.split(" ")
            val distance = hex.drop(2).dropLast(2).toLong(16)
            val lastPoint = shapePoints.last()
            val np = hex.dropLast(1).last().applyOffset(lastPoint, distance)
            perimeter += distance
            shapePoints.add(np)

        }
        val result = shoelaceArea(shapePoints) + perimeter / 2 + 1
        println(result)
    }

    data class Point(val x: Long, val y: Long)

    private fun shoelaceArea(polygon: List<Point>): Long {
        val area = polygon.reversed()
            .windowed(2, 1)
            .sumOf { (first, second) ->
                (first.x * second.y) - (first.y * second.x)
            }
        return abs(area / 2)
    }

    private fun String.applyOffset(point: Point, offset: Int): Point {
        return when (this) {
            "R" -> point.copy(x = point.x + offset)
            "L" -> point.copy(x = point.x - offset)
            "U" -> point.copy(y = point.y - offset)
            "D" -> point.copy(y = point.y + offset)
            else -> error("wrong instr $this")
        }
    }

    private fun Char.applyOffset(point: Point, offset: Long): Point {
        return when (this) {
            '0' -> point.copy(x = point.x + offset)
            '2' -> point.copy(x = point.x - offset)
            '3' -> point.copy(y = point.y - offset)
            '1' -> point.copy(y = point.y + offset)
            else -> error("wrong instr $this")
        }
    }
}