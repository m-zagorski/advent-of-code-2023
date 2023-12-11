package days

import Point
import readLines
import kotlin.math.ceil

object Day10 {

    operator fun invoke() {
        println("🎄Day 10 🎄")
        print("Part 1: ")
        part1(readLines("10"))
        println()
        print("Part 2: ")
        part2(readLines("10"))
    }

    sealed class Direction {
        abstract fun offset(): Pair<Int, Int>

        data object South : Direction() {
            override fun offset(): Pair<Int, Int> = Pair(0, 1)
        }

        data object North : Direction() {
            override fun offset(): Pair<Int, Int> = Pair(0, -1)
        }

        data object East : Direction() {
            override fun offset(): Pair<Int, Int> = Pair(1, 0)
        }

        data object West : Direction() {
            override fun offset(): Pair<Int, Int> = Pair(-1, 0)
        }

    }

    sealed class Position {
        abstract fun nextDirection(comingFrom: Direction): Direction

        data object SouthEast : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.South, Direction.North -> Direction.East
                    Direction.East, Direction.West -> Direction.South
                }
            }
        }

        data object SouthWest : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.South, Direction.North -> Direction.West
                    Direction.West, Direction.East -> Direction.South
                }
            }
        }

        data object NorthEast : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.North, Direction.South -> Direction.East
                    Direction.East, Direction.West -> Direction.North
                }
            }
        }

        data object NorthWest : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.North, Direction.South -> Direction.West
                    Direction.West, Direction.East -> Direction.North
                }
            }
        }

        data object EastWest : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.East -> Direction.East
                    Direction.West -> Direction.West
                    else -> error("Wrong direction $this $comingFrom")
                }
            }
        }

        data object NorthSouth : Position() {
            override fun nextDirection(comingFrom: Direction): Direction {
                return when (comingFrom) {
                    Direction.North -> Direction.North
                    Direction.South -> Direction.South
                    else -> error("Wrong direction $this $comingFrom")
                }
            }
        }
    }

    private val instructionToPositionMap = mapOf(
        'F' to Position.SouthEast,
        '7' to Position.SouthWest,
        'L' to Position.NorthEast,
        'J' to Position.NorthWest,
        '-' to Position.EastWest,
        '|' to Position.NorthSouth
    )

    private val directionToPossiblePipesMap = mapOf(
        Direction.North to "F7|",
        Direction.South to "LJ|",
        Direction.West to "FL-",
        Direction.East to "7J-"
    )

    private const val positionChangingPipes = "SF7LJ"

    data class PolygonCoordinatesWithInstructions(
        val polygonCoordinates: List<Point>,
        val instructionCoordinates: List<Point>
    ) {
        companion object {
            val EMPTY = PolygonCoordinatesWithInstructions(emptyList(), emptyList())
        }
    }

    private tailrec fun countExplore(
        input: List<String>,
        nx: Int,
        ny: Int,
        direction: Direction,
        currentCount: Int = 0
    ): Int {
        if (nx < 0 || ny < 0 || nx > input.size - 1 || ny > input.first().length - 1) return currentCount

        val currentPipe: Char = input[ny][nx]
        val isNextSupported = directionToPossiblePipesMap[direction]?.any { it == currentPipe } ?: false

        return if (isNextSupported) {
            val pipePosition: Position? = instructionToPositionMap[currentPipe]
            val nextDirection = pipePosition?.nextDirection(direction)

            if (nextDirection == null) currentCount
            else {
                val (offsetX, offsetY) = nextDirection.offset()
                countExplore(input, nx + offsetX, ny + offsetY, nextDirection, currentCount + 1)
            }
        } else {
            currentCount
        }
    }

    private fun part1(input: List<String>) {
        val (x, y) = input.mapIndexed { index, l ->
            val position = "[S]".toRegex().findAll(l).firstOrNull()
            if (position != null) position.range.first to index
            else null
        }.filterNotNull().first()

        val loopLength = listOf(
            Triple(1, 0, Direction.East),
            Triple(-1, 0, Direction.West),
            Triple(0, -1, Direction.North),
            Triple(0, 1, Direction.South)
        ).maxOf { (xOffset, yOffset, direction) ->
            countExplore(input, x + xOffset, y + yOffset, direction, 0)
        }

        val result = ceil(loopLength.toDouble() / 2.0).toInt()
        println(result)
    }

    private fun part2(input: List<String>) {
        val (x, y) = input.mapIndexed { index, l ->
            val position = "[S]".toRegex().findAll(l).firstOrNull()
            if (position != null) position.range.first to index
            else null
        }.filterNotNull().first()

        val (coords, correct) = listOf(
            Triple(1, 0, Direction.East),
            Triple(-1, 0, Direction.West),
            Triple(0, -1, Direction.North),
            Triple(0, 1, Direction.South)
        )
            .map { (xOffset, yOffset, direction) ->
                polygonExplore(
                    input = input,
                    x = x + xOffset,
                    y = y + yOffset,
                    direction = direction,
                    coordinates = PolygonCoordinatesWithInstructions(
                        polygonCoordinates = listOf(Point(x, y)),
                        instructionCoordinates = listOf(Point(x, y))
                    )
                )
            }
            .first { (coords, _) -> coords.isNotEmpty() }

        var count = 0
        for ((y, line) in input.withIndex()) {
            for ((x, _) in line.withIndex()) {
                if (!correct.contains(Point(x, y))) {
                    val isInside = isInside(Point(x, y), coords)
                    if (isInside) {
                        count += 1
                    }
                }
            }
        }
        println(count)
    }

    private tailrec fun polygonExplore(
        input: List<String>,
        x: Int,
        y: Int,
        direction: Direction,
        coordinates: PolygonCoordinatesWithInstructions
    ): PolygonCoordinatesWithInstructions {
        if (x < 0 || y < 0 || x > input.first().length - 1 || y > input.size - 1) return PolygonCoordinatesWithInstructions.EMPTY

        val currentPipe = input[y][x]
        if (currentPipe == 'S') {
            return coordinates
        }

        val newCoordinates = if (positionChangingPipes.contains(currentPipe)) {
            coordinates.copy(
                polygonCoordinates = coordinates.polygonCoordinates.plus(Point(x, y)),
                instructionCoordinates = coordinates.instructionCoordinates.plus(Point(x, y))
            )
        } else {
            coordinates.copy(
                instructionCoordinates = coordinates.instructionCoordinates.plus(Point(x, y))
            )
        }

        val isNextSupported = directionToPossiblePipesMap[direction]?.any { it == currentPipe } ?: false

        if (isNextSupported) {
            val pipePosition: Position? = instructionToPositionMap[currentPipe]
            val nextDirection = pipePosition?.nextDirection(direction)
            if (nextDirection != null) {
                val (offsetX, offsetY) = nextDirection.offset()
                return polygonExplore(input, x + offsetX, y + offsetY, nextDirection, newCoordinates)
            }
        }

        return PolygonCoordinatesWithInstructions.EMPTY
    }

    private fun isInside(point: Point, polygonCoordinates: List<Point>): Boolean {
        var j = polygonCoordinates.size - 1
        val intersectionCount = polygonCoordinates.mapIndexed { i, currentPoint ->
            val previousPoint = polygonCoordinates[j]
            val intersect = (currentPoint.y > point.y) != (previousPoint.y > point.y) &&
                    point.x < (previousPoint.x - currentPoint.x) * (point.y - currentPoint.y) / (previousPoint.y - currentPoint.y) + currentPoint.x
            j = i
            if (intersect) 1 else 0
        }.sum()

        return intersectionCount % 2 != 0
    }
}