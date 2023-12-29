package days

import Point
import readLines

object Day16 {

    operator fun invoke() {
        println("🎄Day 16 🎄")
        print("Part 1: ")
        part1(readLines("16"))
        println()
        print("Part 2: ")
        part2(readLines("16"))
    }

    private fun part1(input: List<String>) {
        val matrix = Array(input.size) { Array(input.first().length) { '.' } }
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                matrix[y][x] = c
            }
        }

        val startingPoint = Point(0, 0)
        val direction = Direction.Right
        val startingDirection = direction.nextDirections(matrix[startingPoint.y][startingPoint.x]).first()

        val result = travel(0, startingPoint, startingDirection, matrix, mutableMapOf()).size

        print(result)
    }

    private fun part2(input: List<String>) {
        val matrix = Array(input.size) { Array(input.first().length) { '.' } }
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                matrix[y][x] = c
            }
        }

        val top = (0..<matrix.first().size).map { Point(it, 0) }.maxOf { startingPoint ->
            val direction = Direction.Down
            val startingDirection = direction.nextDirections(matrix[startingPoint.y][startingPoint.x]).first()
            travel(0, startingPoint, startingDirection, matrix, mutableMapOf()).size
        }
        val bottom = (0..<matrix.first().size).map { Point(it, matrix.size - 1) }.maxOf { startingPoint ->
            val direction = Direction.Up
            val startingDirection = direction.nextDirections(matrix[startingPoint.y][startingPoint.x]).first()
            travel(0, startingPoint, startingDirection, matrix, mutableMapOf()).size
        }
        val left = matrix.indices.map { Point(0, it) }.maxOf { startingPoint ->
            val direction = Direction.Right
            val startingDirection = direction.nextDirections(matrix[startingPoint.y][startingPoint.x]).first()
            travel(0, startingPoint, startingDirection, matrix, mutableMapOf()).size
        }
        val right = matrix.indices.map { Point(matrix.first().size - 1, it) }.maxOf { startingPoint ->
            val direction = Direction.Left
            val startingDirection = direction.nextDirections(matrix[startingPoint.y][startingPoint.x]).first()
            travel(0, startingPoint, startingDirection, matrix, mutableMapOf()).size
        }

        val result = listOf(top, bottom, left, right).max()

        println(result)
    }

    private fun travel(
        iteration: Int,
        currentPosition: Point,
        currentDirection: Direction,
        array: Array<Array<Char>>,
        visited: MutableMap<Point, List<Direction>>
    ): Map<Point, List<Direction>> {
        val backToStart = visited.getOrDefault(currentPosition, listOf()).contains(currentDirection)
        if (backToStart) return visited

        val newPoint = currentPosition.applyOffset(currentDirection.offset)
        visited[currentPosition] = visited.getOrDefault(currentPosition, emptyList()).plus(currentDirection)
        return if (newPoint.inBoard(array)) {
            val nextDirections = currentDirection.nextDirections(array[newPoint.y][newPoint.x])
            nextDirections
                .map { dir -> travel(iteration + 1, newPoint, dir, array, visited) }
                .reduce { acc, map -> acc + map }
        } else {
            visited
        }
    }

    sealed class Direction {
        abstract val offset: Point
        abstract fun nextDirections(mirror: Char): List<Direction>

        data object Right : Direction() {
            override val offset: Point = Point(1, 0)

            override fun nextDirections(mirror: Char): List<Direction> {
                return when (mirror) {
                    '.' -> listOf(Right)
                    '|' -> listOf(Up, Down)
                    '-' -> listOf(Right)
                    '\\' -> listOf(Down)
                    '/' -> listOf(Up)
                    else -> error("Unknown $mirror")
                }
            }
        }

        data object Left : Direction() {
            override val offset: Point = Point(-1, 0)
            override fun nextDirections(mirror: Char): List<Direction> {
                return when (mirror) {
                    '.' -> listOf(Left)
                    '|' -> listOf(Up, Down)
                    '-' -> listOf(Left)
                    '\\' -> listOf(Up)
                    '/' -> listOf(Down)
                    else -> error("Unknown $mirror")
                }
            }
        }

        data object Up : Direction() {
            override val offset: Point = Point(0, -1)

            override fun nextDirections(mirror: Char): List<Direction> {
                return when (mirror) {
                    '.' -> listOf(Up)
                    '|' -> listOf(Up)
                    '-' -> listOf(Right, Left)
                    '\\' -> listOf(Left)
                    '/' -> listOf(Right)
                    else -> error("Unknown $mirror")
                }
            }
        }

        data object Down : Direction() {
            override val offset: Point = Point(0, 1)
            override fun nextDirections(mirror: Char): List<Direction> {
                return when (mirror) {
                    '.' -> listOf(Down)
                    '|' -> listOf(Down)
                    '-' -> listOf(Right, Left)
                    '\\' -> listOf(Right)
                    '/' -> listOf(Left)
                    else -> error("Unknown $mirror")
                }
            }
        }
    }
}