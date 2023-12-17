package days

import Point
import readLines
import java.util.*

object Day17 {

    operator fun invoke() {
        println("🎄Day 17 🎄")
        print("Part 1: ")
        part1(readLines("17"))
        println()
        print("Part 2: ")
        part2(readLines("17"))
    }

    private fun part1(input: List<String>) {
        val matrix = Array(input.size) { IntArray(input.first().length) { 0 } }
        input.mapIndexed { y, s ->
            s.forEachIndexed { x, c ->
                matrix[y][x] = c.digitToInt()
            }
        }

        val answer = dijkstra(1, matrix) { state, direction ->
            when {
                state.steps < 3 -> true
                else -> state.direction != direction
            }
        }

        print(answer)
    }

    private fun part2(input: List<String>) {
        val matrix = Array(input.size) { IntArray(input.first().length) { 0 } }
        input.mapIndexed { y, s ->
            s.forEachIndexed { x, c ->
                matrix[y][x] = c.digitToInt()
            }
        }

        val answer = dijkstra(4, matrix) { state, direction ->
            when {
                state.steps < 4 ->  state.direction == direction
                state.steps > 9 -> state.direction != direction
                else -> true
            }
        }

        print(answer)
    }

    sealed class Direction {
        abstract val offset: Point
        abstract val possibleDirections: List<Direction>

        data object Right : Direction() {
            override val offset: Point = Point(1, 0)
            override val possibleDirections: List<Direction> = listOf(Right, Top, Bottom)
        }

        data object Left : Direction() {
            override val offset: Point = Point(-1, 0)
            override val possibleDirections: List<Direction> = listOf(Left, Top, Bottom)
        }

        data object Top : Direction() {
            override val offset: Point = Point(0, -1)
            override val possibleDirections: List<Direction> = listOf(Right, Top, Left)
        }

        data object Bottom : Direction() {
            override val offset: Point = Point(0, 1)
            override val possibleDirections: List<Direction> = listOf(Right, Left, Bottom)
        }
    }

    data class CellState(
        val x: Int,
        val y: Int,
        val direction: Direction,
        val distance: Int,
        val steps: Int
    ) {
        fun toSimple(): CellStateSimple {
            return CellStateSimple(
                x, y, direction, steps
            )
        }
    }

    data class CellStateSimple(
        val x: Int,
        val y: Int,
        val direction: Direction,
        val steps: Int
    )

    private fun dijkstra(minSteps: Int, graph: Array<IntArray>, isValid: (CellState, Direction) -> Boolean): Int {
        val numRows = graph[0].size
        val numCols = graph.size

        val visited = mutableSetOf<CellStateSimple>()
        val queue = PriorityQueue<CellState>(compareBy { it.distance })

        val initialState = CellState(0, 0, Direction.Right, 0, 0)
        queue.add(initialState)
        visited.add(initialState.toSimple())

        while(queue.isNotEmpty()) {
            val cell = queue.poll()

            if (cell.x == numRows - 1 && cell.y == numCols - 1 && cell.steps >= minSteps) {
                return cell.distance
            }

            cell.direction.possibleDirections.forEach { direction ->
                val (dx, dy) = direction.offset
                val newX = cell.x + dx
                val newY = cell.y + dy

                if (newX in 0..<numRows && newY >= 0 && newY < numCols && isValid(cell, direction)) {
                    val newSteps = if (cell.direction == direction) cell.steps + 1 else 1
                    val newState = CellState(newX, newY, direction, cell.distance+graph[newY][newX] ,newSteps )
                    if(!visited.contains(newState.toSimple())) {
                        queue.add(newState)
                        visited.add(newState.toSimple())
                    }
                }
            }
        }
        error("No answer something is wrong")
    }
}