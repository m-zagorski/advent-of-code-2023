package days

import numbersRegex
import readWholeFile
import kotlin.math.min

object Day5 {

    operator fun invoke() {
        println("🎄Day 5 🎄")
        print("Part 1: ")
        part1(readWholeFile("5"))
        println()
        print("Part 2: ")
        part2(readWholeFile("5"))
    }

    private fun part1(input: String) {
        val splits = input.split("\n\n")
        var seeds = numbersRegex.findAll(splits.first()).map { it.value.toLong() }.toList()

        splits.drop(1).forEach { map ->
            val tmpSeeds = seeds.associateBy { it }.toMutableMap()
            map.split("\n").drop(1).forEach { instructions ->
                val (destination, source, range) = numbersRegex.findAll(instructions).map { it.value.toLong() }.toList()
                val diff = destination - source

                tmpSeeds.forEach { (seed, _) ->
                    if (seed in (source..<source + range)) {
                        val new = seed + diff
                        tmpSeeds[seed] = new
                    }
                }
            }
            seeds = tmpSeeds.values.toList()
        }
        val result = seeds.min()
        print(result)
    }

    data class Instruction(val range: LongRange, val diff: Long)

    private fun part2(input: String) {
        val splits = input.split("\n\n")
        val seeds: List<LongRange> =
            numbersRegex.findAll(splits.first()).map { it.value.toLong() }.toList().chunked(2).map { (start, range) ->
                start..<start + range
            }

        val allRanges = seeds.map { seedsRange ->
            var seedsMultipleRanges = setOf(seedsRange)
            splits.drop(1).forEach { map ->
                val instructions: List<Instruction> = map.split("\n").drop(1).map { instruction ->
                    val (destination, source, range) = numbersRegex.findAll(instruction).map { it.value.toLong() }
                        .toList()
                    Instruction(
                        range = source..<source + range,
                        diff = destination - source
                    )
                }

                seedsMultipleRanges = seedsMultipleRanges.map { sr ->
                    changeRanges(sr, instructions)
                }.flatten().toSet()
            }

            seedsMultipleRanges
        }.flatten()

        val result = allRanges.minByOrNull { it.first }?.first

        println(result)
    }

    private fun changeRanges(
        seedsRange: LongRange,
        instructions: List<Instruction>
    ): MutableSet<LongRange> {
        val newRanges = mutableSetOf<LongRange>()
        val s = seedsRange.first
        val e = seedsRange.last
        instructions.forEach { (range, diff) ->
            val insS = range.first
            val insE = range.last

            var start: Long = -1L
            var end: Long = -1L

            if (s < insS) {
//                if (e >= insS) {
//                    start = insS
//                    end = min(e, insE)
//                }
                if (e <= insS) {
                    start = -1L
                    end = -1L
                } else {
                    start = insS
                    end = min(e, insE)
                }
            } else {
//                if (s <= insE) {
//                    start = s
//                    end = min(e, insE)
//                }
                if (s > insE) {
                    start = -1L
                    end = -1L
                } else {
                    start = s
                    end = min(e, insE)
                }
            }

            if (start != -1L && end != -1L) {
                newRanges.add(start + diff..end + diff)
            }
        }
        if (newRanges.isEmpty()) {
            newRanges.add(s..e)
        }

        return mergeOverlappingRanges(newRanges)
    }

    private fun mergeOverlappingRanges(ranges: Set<LongRange>): MutableSet<LongRange> {
        if (ranges.isEmpty()) return mutableSetOf()
        val sortedRanges = ranges.sortedBy { it.first }
        val mergedRanges = mutableSetOf<LongRange>()

        var currentRange = sortedRanges.first()

        for (range in sortedRanges.drop(1)) {
            currentRange = if (currentRange.last >= range.first - 1) {
                currentRange.first..range.last
            } else {
                mergedRanges.add(currentRange)
                range
            }
        }
        mergedRanges.add(currentRange)

        return mergedRanges
    }
}