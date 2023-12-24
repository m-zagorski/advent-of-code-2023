package days

import readLines

object Day24 {

    operator fun invoke() {
        println("🎄Day 24 🎄")
        print("Part 1: ")
        part1(readLines("24"))
        println()
        print("Part 2: ")
        part2(readLines("24"))
    }

    private fun part1(input: List<String>) {
        val points = input.map { line ->
            val (c, o) = line.split(" @ ").map { it.replace(" ", "") }
            val (x, y) = c.split(",").map { it.toLong() }
            val (ox, oy) = o.split(",").map { it.toLong() }

            PointInTime(
                p = PointL(x, y),
                pOffset = PointL(ox, oy)
            )
        }

        var inCount = 0
        val testRange = 200_000_000_000_000.0..400_000_000_000_000.0
        points.forEachIndexed { index, pointA ->
            points.drop(index + 1).forEach { pointB ->
                val a = pointA.slopeAndYIntersection()
                val b = pointB.slopeAndYIntersection()

                if (a.first != b.first) {
                    val (x, y) = findIntersection(a, b)
                    if (x in testRange && y in testRange && pointA.arePointsInCorrectTime(x, y) && pointB.arePointsInCorrectTime(x, y)) {
                        inCount++
                    }
                }
            }
        }
        println(inCount)
    }

    private fun part2(input: List<String>) {
        val result = input.map {
        }

        println(result)
    }

    data class PointL(val x: Long, val y: Long)

    data class PointInTime(
        var p: PointL,
        val pOffset: PointL
    ) {
        fun arePointsInCorrectTime(p1: Double, p2: Double): Boolean {
            val isXCorrect =  if(pOffset.x >= 0) {
                p1 >= p.x
            } else {
                p1 <= p.x
            }
            val isYCorrect = if(pOffset.y >= 0) {
                p2 >= p.y
            } else {
                p2 <= p.y
            }

            return isXCorrect && isYCorrect
        }
    }

    private fun PointInTime.slopeAndYIntersection(): Pair<Double, Double> {
        return slope() to yIntersection(slope())
    }

    private fun PointInTime.slope(): Double {
        return pOffset.y / pOffset.x.toDouble()
    }

    private fun PointInTime.yIntersection(slope: Double): Double {
        return p.y - (slope * p.x)
    }

    private fun findIntersection(
        line1Data: Pair<Double, Double>,
        line2Data: Pair<Double, Double>
    ): Pair<Double, Double> {
        val (a1, b1) = line1Data
        val (a2, b2) = line2Data

        val x = (b2 - b1) / (a1 - a2)
        val y = a1 * x + b1

        return x to y
    }
}