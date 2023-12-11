package days

import Point
import readLines
import kotlin.math.max
import kotlin.math.min

object Day11 {

    operator fun invoke() {
        println("🎄Day 11 🎄")
        print("Part 1: ")
        part1(readLines("11"))
        println()
        print("Part 2: ")
        part2(readLines("11"))
    }

    private fun part1(input: List<String>) {
        val ans = input.findAnswerForExpandedGalaxy(1)
        print(ans)
    }

    private fun part2(input: List<String>) {
       val ans = input.findAnswerForExpandedGalaxy(999_999L)
        println(ans)
    }

    private fun List<String>.findAnswerForExpandedGalaxy(expandFactor: Long): Long {
        val rows = indices.associateWith { emptyList<Int>() }.toMutableMap()
        val columns = first().indices.associateWith { emptyList<Int>() }.toMutableMap()
        val pointsLocations = mutableListOf<Point>()
        forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') {
                    pointsLocations.add(Point(x, y))

                    val currentRow = rows.getOrDefault(y, emptyList())
                    rows[y] = currentRow.plus(x)
                    val currentColumn = columns.getOrDefault(x, emptyList())
                    columns[x] = currentColumn.plus(y)
                }
            }
        }
        val emptyRows = rows.filter { (_, values) -> values.isEmpty() }.map { it.key }
        val emptyColumns = columns.filter { (_, values) -> values.isEmpty() }.map { it.key }

        val pairs = mutableListOf<Pair<Point, Point>>()
        for (i in pointsLocations.indices) {
            for (j in i + 1..<pointsLocations.size) {
                val pair = Pair(pointsLocations[i], pointsLocations[j])
                pairs.add(pair)
            }
        }

        return pairs.sumOf { (s, e) ->
            val xRange = (min(s.x, e.x)..max(s.x, e.x))
            val yRange = (min(s.y, e.y)..max(s.y, e.y))
            val crossingColumns = emptyColumns.count { it in xRange } * expandFactor
            val crossingRows = emptyRows.count { it in yRange } * expandFactor
            val totalCrossing = crossingColumns + crossingRows
            val distance = distanceBetweenPoints(s, e)
            distance + totalCrossing
        }
    }

    private fun distanceBetweenPoints(start: Point, end: Point): Int {
        return (max(start.x, end.x) - min(start.x, end.x)) + (max(start.y, end.y) - min(start.y, end.y))
    }
}