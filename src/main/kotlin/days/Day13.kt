package days

import readWholeFile

object Day13 {

    operator fun invoke() {
        println("🎄Day 13 🎄")
        print("Part 1: ")
        part1(readWholeFile("13"))
        println("")
        print("Part 2: ")
        part2(readWholeFile("13"))
    }

    private fun part1(input: String) {
        val result = input.split("\n\n").sumOf { pattern ->
            val linesOfPattern = pattern.split("\n")
            val xValues = linesOfPattern.first().indices.associateWith { emptyList<Int>() }.toMutableMap()
            val yValues = linesOfPattern.indices.associateWith { emptyList<Int>() }.toMutableMap()

            linesOfPattern.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    if (c == '#') {
                        val cxv = xValues.getOrDefault(x, emptyList())
                        xValues[x] = cxv.plus(y)
                        val cyv = yValues.getOrDefault(y, emptyList())
                        yValues[y] = cyv.plus(x)
                    }
                }
            }

            val xIndex = findReflection(xValues.toSortedMap().values.toList())
            val yIndex = findReflection(yValues.toSortedMap().values.toList())

            when {
                yIndex != -1 -> yIndex * 100
                xIndex != -1 ->xIndex
                else -> error("There is something wrong")
            }
        }

        print(result)
    }


    private fun part2(input: String) {
        val result = input.split("\n\n").sumOf { pattern ->
            val linesOfPattern = pattern.split("\n")
            val inputArray: Array<Array<Char>> = Array(linesOfPattern.size) { Array(linesOfPattern.first().length) { '.' } }
            linesOfPattern.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    inputArray[y][x] = c
                }
            }

           val (xReflectionIndex, yReflectionIndex) = inputArray.findSmudge(inputArray.findPerfectLine())

            when {
                yReflectionIndex != -1 -> yReflectionIndex * 100
                xReflectionIndex != -1 -> xReflectionIndex
                else -> error("Something is wrong")
            }
        }

        print(result)
    }

    private fun Array<Array<Char>>.findPerfectLine(
        modifiedX: Int = -1, modifiedY: Int = -1,
        previousX: Int = -1, previousY: Int = -1
    ): Pair<Int, Int> {
        val xValues: MutableMap<Int, List<Int>> = first().indices.associateWith { emptyList<Int>() }.toMutableMap()
        val yValues: MutableMap<Int, List<Int>> = indices.associateWith { emptyList<Int>() }.toMutableMap()

        forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                val currentChar = if(x == modifiedX && y == modifiedY) c.opposite()
                else c

                if (currentChar == '#') {
                    val cxv = xValues.getOrDefault(x, emptyList())
                    xValues[x] = cxv.plus(y)
                    val cyv = yValues.getOrDefault(y, emptyList())
                    yValues[y] = cyv.plus(x)
                }
            }
        }
        val xIndex = findReflection(xValues.toSortedMap().values.toList(), previousX)
        val yIndex = findReflection(yValues.toSortedMap().values.toList(), previousY)
        return xIndex to yIndex
    }

    private fun Array<Array<Char>>.findSmudge(currentReflection: Pair<Int, Int>): Pair<Int, Int> {
        val (xIndex, yIndex) = currentReflection
        var new = xIndex to yIndex

        outer@ for(y in indices) {
            for((x, _) in this[y].withIndex()) {
                val (newXIndex, newYIndex) = findPerfectLine(modifiedX = x, modifiedY = y, xIndex, yIndex)
                if(newXIndex != -1 && newXIndex != xIndex) {
                    new = newXIndex to -1
                    break@outer
                }

                if(newYIndex != -1 && newYIndex != yIndex) {
                    new = -1 to newYIndex
                    break@outer
                }
            }
        }
        return new
    }

    private fun findReflection(
        input: List<List<Int>>,
        previousReflection: Int = -1
    ): Int {
        for (i in 0..<input.size - 1) {
            val l = input[i]
            val r = input[i + 1]
            if (l == r) {
                var isPerfect = true
                var startIndex = i
                var endIndex = i + 1
                while (true) {
                    val tmpL = input.getOrNull(startIndex - 1)
                    val tmpR = input.getOrNull(endIndex + 1)

                    if (tmpL == null || tmpR == null) break

                    if (tmpL != tmpR) {
                        isPerfect = false
                        break
                    }
                    startIndex--
                    endIndex++
                }

                if (isPerfect && previousReflection != i+1) return i + 1
            }
        }
        return -1
    }

    private fun Char.opposite(): Char {
        return if(this == '.') '#'
        else '.'
    }
}