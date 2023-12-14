package days

import readLines

object Day14 {

    operator fun invoke() {
        println("🎄Day 14 🎄")
        print("Part 1: ")
        part1(readLines("14"))
        println()
        print("Part 2: ")
        part2(readLines("14"))
    }

    private fun part1(input: List<String>) {
        val cubeRocks: MutableMap<Int, List<Int>> = input.indices.associateWith { emptyList<Int>() }.toMutableMap()
        val roundRocks: MutableMap<Int, List<Int>> = input.indices.associateWith { emptyList<Int>() }.toMutableMap()
        input.forEachIndexed { y, l ->
            l.forEachIndexed { x, c ->
                if (c == '#') {
                    cubeRocks[y] = cubeRocks.getOrDefault(y, emptyList()).plus(x)
                }
                if (c == 'O') {
                    roundRocks[y] = roundRocks.getOrDefault(y, emptyList()).plus(x)
                }
            }
        }

        val output = rollRocksVertically(0, cubeRocks, roundRocks, false)
        val sum = output.values.withIndex().sumOf { (k, v) ->
            (roundRocks.size - k) * v.size
        }
        print(sum)
    }

    private fun part2(input: List<String>) {
        val cubeRocks: MutableMap<Int, List<Int>> = input.indices.associateWith { emptyList<Int>() }.toMutableMap()
        val roundRocks: MutableMap<Int, List<Int>> = input.indices.associateWith { emptyList<Int>() }.toMutableMap()
        input.forEachIndexed { y, l ->
            l.forEachIndexed { x, c ->
                if (c == '#') {
                    cubeRocks[y] = cubeRocks.getOrDefault(y, emptyList()).plus(x)
                }
                if (c == 'O') {
                    roundRocks[y] = roundRocks.getOrDefault(y, emptyList()).plus(x)
                }
            }
        }

        var output: MutableMap<Int, List<Int>> = roundRocks
        val cache = hashMapOf<Int, List<Int>>()

        var firstCacheHit = -1
        val diff: Int
        var i = 0
        while (true) {
            val key = output.hashCode()

            if (cache.containsKey(key) && firstCacheHit == -1) {
                firstCacheHit = i
            }
            output = rollCircle(cubeRocks, output)
            val newValue = cache.getOrDefault(key, emptyList()).plus(i)
            cache[key] = newValue
            if (newValue.size == 2) {
                diff = newValue[1] - newValue[0]
                break
            }
            i++
        }

        val divider = (1_000_000_000 - firstCacheHit) / diff
        val offset = 1_000_000_000 - firstCacheHit - (diff * divider)

        val o = (0..<firstCacheHit + offset).fold(roundRocks) { acc, _ -> rollCircle(cubeRocks, acc) }

        val sum = o.values.withIndex().sumOf { (k, v) ->
            (roundRocks.size - k) * v.size
        }
        println(sum)
    }

    private fun rollRocksVertically(
        currentRow: Int,
        cubes: MutableMap<Int, List<Int>>,
        round: MutableMap<Int, List<Int>>,
        south: Boolean
    ): MutableMap<Int, List<Int>> {
        val endCondition = if (south) currentRow == 0 else currentRow == cubes.size - 1
        if (endCondition) return round

        val cr = if (south) currentRow - 1 else currentRow + 1
        val offset = if (south) 1 else -1
        val range = if (south) currentRow..<cubes.size else currentRow downTo 0

        val itemsToMove = round[cr]!!
        round[cr] = emptyList()

        val movedItems = itemsToMove.map { item ->
            var itemIndex = cr
            for (i in range) {
                val c: List<Int> = cubes[i] ?: emptyList()
                val r: List<Int> = round[i] ?: emptyList()
                if (c.contains(item) || r.contains(item)) {
                    break
                } else {
                    itemIndex += offset
                }
            }
            itemIndex to item
        }
        movedItems.forEach { (y, x) ->
            round[y] = round.getOrDefault(y, emptyList()).plus(x)
        }
        return rollRocksVertically(cr, cubes, round, south)
    }

    private tailrec fun rollRocksHorizontally(
        items: List<Int>,
        cubes: List<Int>,
        result: List<Int>,
        maxSize: Int,
        west: Boolean
    ): List<Int> {
        if (items.isEmpty()) return result

        val item = if (west) items.first() else items.last()
        val offset = if (west) -1 else 1
        var currentValue = item
        val range = if (west) item + offset downTo 0 else currentValue + 1..<maxSize
        for (i in range) {
            if (result.contains(i) || cubes.contains(i)) {
                break
            } else {
                currentValue += offset
            }
        }
        return rollRocksHorizontally(items.minus(item), cubes, result.plus(currentValue), maxSize, west)
    }

    private fun rollRocksToWest(
        cubes: MutableMap<Int, List<Int>>,
        round: MutableMap<Int, List<Int>>
    ): MutableMap<Int, List<Int>> {
        return round.mapValues { (y, values) ->
            rollRocksHorizontally(values.sorted(), cubes[y]!!, emptyList(), round.size, true)
        }.toMutableMap()
    }

    private fun rollRocksToEast(
        cubes: MutableMap<Int, List<Int>>,
        round: MutableMap<Int, List<Int>>
    ): MutableMap<Int, List<Int>> {
        return round.mapValues { (y, values) ->
            rollRocksHorizontally(values.sorted(), cubes[y]!!, emptyList(), round.size, false)
        }.toMutableMap()
    }

    private fun rollCircle(
        cubes: MutableMap<Int, List<Int>>,
        round: MutableMap<Int, List<Int>>
    ): MutableMap<Int, List<Int>> {
        val north = rollRocksVertically(0, cubes, round, false)
        val west = rollRocksToWest(cubes, north)
        val south = rollRocksVertically(cubes.size - 1, cubes, west, true)
        return rollRocksToEast(cubes, south)
    }
}