package days

import readLines

object Day22 {

    operator fun invoke() {
        println("🎄Day 22 🎄")
        print("Part 1: ")
        part1(readLines("22"))
        println()
        print("Part 2: ")
        part2(readLines("22"))
    }

    private fun part1(input: List<String>) {
        val bricks = input.map { inn ->
            val (s, e) = inn.split("~")
            val (sx, sy, sz) = s.split(",").map { it.toInt() }
            val (ex, ey, ez) = e.split(",").map { it.toInt() }
            Brick(
                x = sx..ex,
                y = sy..ey,
                z = sz..ez
            )
        }.sortedBy { it.z.first }.toSet()

        val (bricksWithHeights, _) = moveBricksDown(bricks)

        val result = bricksWithHeights.count { brick ->
            canBeRemoved(brick, bricksWithHeights)
        }

        print(result)
    }

    private fun part2(input: List<String>) {
        val bricks = input.map { inn ->
            val (s, e) = inn.split("~")
            val (sx, sy, sz) = s.split(",").map { it.toInt() }
            val (ex, ey, ez) = e.split(",").map { it.toInt() }
            Brick(
                x = sx..ex,
                y = sy..ey,
                z = sz..ez
            )
        }.sortedBy { it.z.first }.toSet()

        val (bricksWithHeights, _) = moveBricksDown(bricks)

        val result = bricksWithHeights.sumOf { brick ->
            if (canBeRemoved(brick, bricksWithHeights)) {
                val (_, count) = moveBricksDown(bricksWithHeights.minus(brick))
                count
            } else {
                0L
            }
        }

        println(result)
    }

    data class Brick(
        val x: IntRange,
        val y: IntRange,
        val z: IntRange
    ) {
        fun moveBrickToZPosition(zOffset: Int): Brick {
            val height = z.last - z.first
            return copy(z = zOffset..zOffset + height)
        }
    }

    private fun canBeRemoved(brick: Brick, bricks: Set<Brick>): Boolean {
        val sameHeightBricks = bricks.filter { it.z.last == brick.z.last }.minus(brick)
        val bricksAbove = bricks.filter { it.z.first == brick.z.last + 1 }

        return bricksAbove.none { aboveBrick ->
            sameHeightBricks.none { below ->
                below.x.intersect(aboveBrick.x).isNotEmpty() && below.y.intersect(aboveBrick.y).isNotEmpty()
            }
        }
    }

    private fun moveBricksDown(bricks: Set<Brick>): Pair<Set<Brick>, Long> {
        val bricksWithHeights = mutableSetOf<Brick>()
        val startingHeight = 1
        var count = 0
        for (brick in bricks) {
            val mov = brick.z.first - startingHeight
            if (mov == 0) {
                bricksWithHeights.add(brick)
                continue
            }
            var currentHeight = mov
            while (true) {
                if (currentHeight == 0) {
                    val element = brick.moveBrickToZPosition(1)
                    if (element != brick) count++
                    bricksWithHeights.add(element)
                    break
                }

                val downCondition = canGoDown(brick, currentHeight, bricksWithHeights)
                if (downCondition != null) {
                    val element = brick.moveBrickToZPosition(downCondition + 1)
                    if (element != brick) count++
                    bricksWithHeights.add(element)
                    break
                }
                currentHeight--
            }
        }
        return bricksWithHeights to count.toLong()
    }

    private fun canGoDown(brick: Brick, position: Int, bricks: Set<Brick>): Int? {
        val bricksBelow = bricks.filter { it.z.last == position }

        if (bricksBelow.isEmpty()) return null

        val brickToLayOn = bricksBelow.firstOrNull { below ->
            below.x.intersect(brick.x).isNotEmpty() && below.y.intersect(brick.y).isNotEmpty()
        }

        return brickToLayOn?.z?.last
    }
}