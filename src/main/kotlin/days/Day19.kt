package days

import readWholeFile
import kotlin.math.max
import kotlin.math.min

object Day19 {

    operator fun invoke() {
        println("🎄Day 19 🎄")
        print("Part 1: ")
        part1(readWholeFile("19"))
        println()
        print("Part 2: ")
        part2(readWholeFile("19"))
    }

    private fun part1(input: String) {
        val (workflows, ratings) = input.split("\n\n")

        val allWorkflows = workflows.split("\n").associate { workflow ->
            val (name, instruction) = "(\\w+)\\{(\\S+)}".toRegex().find(workflow)?.groupValues.orEmpty().drop(1)
            val instr = instruction.split(",").map(InstructionTest::createFromInput)
            name to instr
        }

        val mappedRatings: List<Map<String, Long>> = ratings.split("\n").map { rating ->
            rating.drop(1).dropLast(1).split(",").associate {
                val (name, number) = "([a-z])=(\\d+)".toRegex().find(it)?.groupValues.orEmpty().drop(1)
                name to number.toLong()
            }
        }

        var sum = 0L
        mappedRatings.forEach { rating ->
            var currentInstructions = allWorkflows.getValue("in")
            while (true) {
                val result = travelInstructions(rating, currentInstructions)
                if (result == "A") {
                    sum += rating.values.sum()
                    break
                }
                if (result == "R") break
                currentInstructions = allWorkflows.getValue(result)
            }
        }

        print(sum)
    }

    private fun travelInstructions(rating: Map<String, Long>, instructions: List<InstructionTest>): String {
        for (instr in instructions) {
            instr.jump(rating)?.let {
                return it
            }
        }
        error("Wrong state")
    }

    private fun part2(input: String) {
        val (workflows, _) = input.split("\n\n")

        val allWorkflows = workflows.split("\n").associate { workflow ->
            val (name, instruction) = "(\\w+)\\{(\\S+)}".toRegex().find(workflow)?.groupValues.orEmpty().drop(1)
            val instr = instruction.split(",").map(InstructionTest::createFromInput)
            name to instr
        }

        val startWorkflow = allWorkflows.getValue("in")
        val paths: List<List<InstructionTest>> = startWorkflow.flatMapIndexed { index, instruction ->
            val prev = startWorkflow.take(index).map { it.opposite() }
            findAcceptedPaths(instruction, allWorkflows, prev)
        }

        val result = paths.sumOf { path ->
            path.filter { it.operation != null }
                .fold(Numbers()) { acc, instruction ->
                    instruction.operation?.let { acc.modify(it) } ?: error("Wrong state")
                }.calculateProbabilities()
        }

        println(result)
    }

    data class Operation(val rating: String, val condition: String, val number: Long) {
        fun opposite(): Operation {
            return copy(condition = condition.opposite())
        }

        private fun String.opposite(): String {
            return when (this) {
                "<" -> ">="
                ">" -> "<="
                else -> error("Wrong")
            }
        }
    }

    private val operationRegex = "^([a-z])([<>])(\\d+):([A-Za-z]+)\$".toRegex()

    data class InstructionTest(val operation: Operation?, val nextInstruction: String) {
        fun jump(rating: Map<String, Long>): String? {
            if (operation == null) return nextInstruction

            val number = rating.getValue(operation.rating)

            return when (operation.condition) {
                "<" -> if (number < operation.number) nextInstruction else null
                ">" -> if (number > operation.number) nextInstruction else null
                else -> error("Unknown")
            }
        }

        fun isAccepted(): Boolean = nextInstruction == "A"

        fun isRejected(): Boolean = nextInstruction == "R"

        fun opposite(): InstructionTest {
            return copy(operation = operation?.opposite())
        }

        companion object {
            fun createFromInput(input: String): InstructionTest {
                return if (input.contains(":")) {
                    val (rating, condition, number, nextInstruction) = operationRegex.find(input)?.groupValues.orEmpty().drop(1)
                    InstructionTest(Operation(rating, condition, number.toLong()), nextInstruction)
                } else {
                    InstructionTest(null, input)
                }
            }
        }
    }

    private fun findAcceptedPaths(
        instruction: InstructionTest,
        workflows: Map<String, List<InstructionTest>>,
        result: List<InstructionTest>
    ): List<List<InstructionTest>> {
        if (instruction.isAccepted()) return listOf(result.plus(instruction))
        if (instruction.isRejected()) return emptyList()

        val nextInstructions = workflows.getValue(instruction.nextInstruction)

        return nextInstructions.flatMapIndexed { index, ii ->
            val prev = nextInstructions.take(index).map { it.opposite() }
            findAcceptedPaths(ii, workflows, result.plus(instruction).plus(prev))
        }
    }

    data class Numbers(
        val x: LongRange = 1L..4000L,
        val m: LongRange = 1L..4000L,
        val a: LongRange = 1L..4000L,
        val s: LongRange = 1L..4000L
    ) {
        fun modify(op: Operation): Numbers {
            return when (op.rating) {
                "x" -> copy(x = x.update(op.condition, op.number))
                "m" -> copy(m = m.update(op.condition, op.number))
                "a" -> copy(a = a.update(op.condition, op.number))
                "s" -> copy(s = s.update(op.condition, op.number))
                else -> error("Wrong ${op.rating}")
            }
        }

        fun calculateProbabilities(): Long {
            return (x.last - x.first + 1) * (m.last - m.first + 1) * (a.last - a.first + 1) * (s.last - s.first + 1)
        }
    }

    private fun LongRange.update(condition: String, value: Long): LongRange {
        return when (condition) {
            "<" -> first..min(value - 1, last)
            "<=" -> first..min(value, last)
            ">" -> max(value + 1, first)..last
            ">=" -> max(value, first)..last
            else -> error("Wrong $condition")
        }
    }
}