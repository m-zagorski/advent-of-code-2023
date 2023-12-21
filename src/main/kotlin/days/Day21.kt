package days

import Point
import readLines

object Day21 {

    operator fun invoke() {
        println("🎄Day 21 🎄")
        print("Part 1: ")
        part1(readLines("21"))
        println()
        print("Part 2: ")
        part2(readLines("21"))
    }

    private fun part1(input: List<String>) {
        var startPosition = Point(0, 0)
        val matrix = Array(input.size) { Array(input.first().length) { '.' } }

        input.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == 'S') {
                    matrix[y][x] = '.'
                    startPosition = Point(x, y)
                } else {
                    matrix[y][x] = c
                }
            }
        }

        val result = solve(startPosition, 64, matrix)

        print(result)
    }

    private fun solve(startPosition: Point, maxSteps: Int, matrix: Array<Array<Char>>): Int {
        val directions = listOf(
            Point(1, 0),
            Point(-1, 0),
            Point(0, -1),
            Point(0, 1)
        )

        var currentPoints = setOf(startPosition)
        repeat(maxSteps) {
            val tmp = mutableSetOf<Point>()
            currentPoints.forEach { current ->
                val newPoints = directions
                    .map { direction -> current.applyOffset(direction) }
                    .filter { np -> np.inBoard(matrix) && matrix[np.y][np.x] == '.' }
                tmp.addAll(newPoints)
            }
            currentPoints = tmp
        }
        return currentPoints.size
    }

    private fun Point.applyOffset(offset: Point): Point {
        return Point(
            x = this.x + offset.x,
            y = this.y + offset.y
        )
    }

    private fun Point.inBoard(array: Array<Array<Char>>): Boolean {
        return x >= 0 && x <= array.first().size - 1 && y >= 0 && y <= array.size - 1
    }

    private fun part2(input: List<String>) {
    }
}