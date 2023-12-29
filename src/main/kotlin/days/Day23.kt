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

        val result = bfs(sp, ep, matrix)

        print(result)
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

        val roads = traverseAllRoads(sp, matrix)
        val distinctNodes = roads.flatMap { listOf(it.start, it.end) }.toSet()
        val map = distinctNodes.associateWith { currentNode ->
            roads.mapNotNull {
                if (it.start == currentNode) EndPointWithDistance(it.end, it.length)
                else if (it.end == currentNode) EndPointWithDistance(it.start, it.length)
                else null
            }
        }

        val result = findLongestRoad(sp, ep, 0, emptySet(), map).max()
        println(result)
    }

    sealed class Direction {
        abstract val offset: Point

        fun opposite(): Direction {
            return when (this) {
                Bottom -> Top
                Left -> Right
                Right -> Left
                Top -> Bottom
            }
        }

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

    data class Crossroad(val start: Point, val direction: Direction)
    data class Road(val start: Point, val end: Point, val length: Int)
    data class RoadWithCrossroads(val road: Road, val reflectedCrossroad: Crossroad, val crossroads: List<Crossroad>)

    data class EndPointWithDistance(val p: Point, val distance: Int)

    data class Cell(
        val point: Point,
        val distance: Int,
        val supportedDirections: List<Direction>,
        val visited: Set<Point>
    )

    private fun bfs(
        startPoint: Point,
        endPoint: Point,
        matrix: Array<Array<Char>>
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
                val nextDirections = when (val atPoint = matrix[np.y][np.x]) {
                    '#' -> continue
                    '.' -> Direction.all()
                    '>' -> listOf(Direction.Right)
                    '<' -> listOf(Direction.Left)
                    '^' -> listOf(Direction.Top)
                    'v' -> listOf(Direction.Bottom)
                    else -> error("Unknown field $atPoint")
                }

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

    private fun traverseAllRoads(sp: Point, matrix: Array<Array<Char>>): Set<Road> {
        val visitedCrossroads = mutableSetOf<Crossroad>()
        val roads = mutableSetOf<Road>()

        val startingCrossroad = Crossroad(sp, Direction.Bottom)
        val queue = LinkedList<Crossroad>()
        queue.offer(startingCrossroad)

        while (queue.isNotEmpty()) {
            val crossroad = queue.poll()

            if (visitedCrossroads.contains(crossroad)) continue

            visitedCrossroads.add(crossroad)

            val roadWithCrossroads = travelCurrentRoad(
                sp = crossroad.start.applyOffset(crossroad.direction.offset),
                road = Road(crossroad.start, crossroad.start, 1),
                comingFrom = crossroad.direction,
                matrix = matrix
            )

            roads.add(roadWithCrossroads.road)
            visitedCrossroads.add(roadWithCrossroads.reflectedCrossroad)
            roadWithCrossroads.crossroads.forEach { queue.offer(it) }
        }

        return roads

    }

    private fun findLongestRoad(
        sp: Point,
        ep: Point,
        currentDistance: Int,
        visited: Set<Point>,
        matrix: Map<Point, List<EndPointWithDistance>>
    ): Set<Int> {
        if (sp == ep) return setOf(currentDistance)

        return matrix.getValue(sp).flatMap { (n, length) ->
            if (visited.contains(n)) emptySet()
            else findLongestRoad(n, ep, currentDistance + length, visited.plus(sp), matrix)
        }.toSet()
    }

    private tailrec fun travelCurrentRoad(
        sp: Point,
        road: Road,
        comingFrom: Direction,
        matrix: Array<Array<Char>>
    ): RoadWithCrossroads {
        val nextDirections = getAvailableDirections(sp, matrix, comingFrom)

        return if (nextDirections.size == 1) {
            travelCurrentRoad(
                sp.applyOffset(nextDirections.first().offset),
                road.copy(length = road.length + 1),
                nextDirections.first(),
                matrix
            )
        } else {
            RoadWithCrossroads(
                road = road.copy(end = sp),
                reflectedCrossroad = Crossroad(sp, comingFrom.opposite()),
                crossroads = if (nextDirections.isEmpty()) emptyList() else nextDirections.map { Crossroad(sp, it) }
            )
        }
    }

    private fun getAvailableDirections(
        p: Point,
        matrix: Array<Array<Char>>,
        comingFrom: Direction
    ): List<Direction> {
        val dir = mutableListOf<Direction>()
        val right = p.applyOffset(Direction.Right.offset)
        val left = p.applyOffset(Direction.Left.offset)
        val top = p.applyOffset(Direction.Top.offset)
        val bottom = p.applyOffset(Direction.Bottom.offset)
        if (right.inBoard(matrix) && right.canGo(matrix) && comingFrom != Direction.Left) dir.add(Direction.Right)
        if (left.inBoard(matrix) && left.canGo(matrix) && comingFrom != Direction.Right) dir.add(Direction.Left)
        if (top.inBoard(matrix) && top.canGo(matrix) && comingFrom != Direction.Bottom) dir.add(Direction.Top)
        if (bottom.inBoard(matrix) && bottom.canGo(matrix) && comingFrom != Direction.Top) dir.add(Direction.Bottom)

        return dir
    }

    private fun Point.canGo(matrix: Array<Array<Char>>): Boolean {
        return when (matrix[y][x]) {
            '.',
            '>',
            '<',
            '^',
            'v' -> true

            else -> false
        }
    }
}