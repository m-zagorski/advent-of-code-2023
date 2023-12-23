package days

import Point
import readLines
import java.util.*

object Day23 {

    operator fun invoke() {
        println("🎄Day 23 🎄")
        print("Part 1: ")
        part1(readLines("23"))
        println()
        print("Part 2: ")
        part2(readLines("23"))
    }

    private fun part1(input: List<String>) {
        val matrix = Array(input.size) { Array(input.first().length) { '.' } }
        input.mapIndexed { y, s ->
            s.forEachIndexed { x, c ->
                matrix[y][x] = c
            }
        }
        val startPoint = matrix.first().indexOfFirst { it == '.' }
        val endPoint = matrix.last().indexOfLast { it == '.' }

        val sp = Point(startPoint, 0)
        val ep = Point(endPoint, matrix.size - 1)

        val result = bfs(sp, ep, matrix) { atPoint ->
            when (atPoint) {
                '#' -> emptyList()
                '.' -> Direction.all()
                '>' -> listOf(Direction.Right)
                '<' -> listOf(Direction.Left)
                '^' -> listOf(Direction.Top)
                'v' -> listOf(Direction.Bottom)
                else -> error("Unknown field $atPoint")
            }
        }

        print(result)
    }

    sealed class Direction {
        abstract val offset: Point

        data object Right : Direction() {
            override val offset: Point = Point(1, 0)
        }

        data object Left : Direction() {
            override val offset: Point = Point(-1, 0)
        }

        data object Top : Direction() {
            override val offset: Point = Point(0, -1)
        }

        data object Bottom : Direction() {
            override val offset: Point = Point(0, 1)
        }

        companion object {
            fun all(): List<Direction> = listOf(Right, Left, Top, Bottom)
        }
    }

    data class Cell(
        val point: Point,
        val distance: Int,
        val supportedDirections: List<Direction>,
        val visited: Set<Point>
    )

    private fun bfs(
        startPoint: Point,
        endPoint: Point,
        matrix: Array<Array<Char>>,
        directionMapper: (Char) -> List<Direction>
    ): Int {
        val distance = Array(matrix.size) { IntArray(matrix.first().size) { -1 } }
        val queue = LinkedList<Cell>()

        val startCell = Cell(startPoint, 0, Direction.all(), setOf(startPoint))
        queue.offer(startCell)
        distance[startPoint.y][startPoint.x] = 0

        val endDistances = mutableListOf<Int>()

        while (queue.isNotEmpty()) {
            val cell = queue.poll()

            for (direction in cell.supportedDirections) {
                val np = cell.point.applyOffset(direction.offset)

                if (!np.inBoard(matrix) || cell.visited.contains(np)) continue
                val atPoint = matrix[np.y][np.x]
                val nextDirections = directionMapper(atPoint)
                if (nextDirections.isEmpty()) continue

                val nc = Cell(np, cell.distance + 1, nextDirections, cell.visited.plus(np))

                if (np == endPoint) {
                    endDistances.add(nc.distance)
                } else {
                    queue.offer(nc)
                }
            }
        }

        return endDistances.max()
    }

    private fun Point.inBoard(array: Array<Array<Char>>): Boolean {
        return x >= 0 && x <= array.first().size - 1 && y >= 0 && y <= array.size - 1
    }

    private fun Point.applyOffset(offset: Point): Point {
        return Point(
            x = this.x + offset.x,
            y = this.y + offset.y
        )
    }

    private fun part2(input: List<String>) {
        val matrix = Array(input.size) { Array(input.first().length) { '.' } }
        input.mapIndexed { y, s ->
            s.forEachIndexed { x, c ->
                matrix[y][x] = c
            }
        }
        val startPoint = matrix.first().indexOfFirst { it == '.' }
        val endPoint = matrix.last().indexOfLast { it == '.' }

        val sp = Point(startPoint, 0)
        val ep = Point(endPoint, matrix.size - 1)

        println("Christmas \uD83C\uDF84\uD83C\uDF84\uD83C\uDF84")
    }
}