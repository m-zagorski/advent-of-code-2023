package days

import numbersRegex
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
        var startWorkflow: List<Instruction> = emptyList()
        val allWorkflows: MutableMap<String, List<Instruction>> = mutableMapOf()
        val (workflows, ratings) = input.split("\n\n")

        workflows.split("\n").forEach { workflow ->
            val sIdx = workflow.indexOfFirst { it == '{' }
            val name = workflow.substring(0, sIdx)
            val others = workflow.substring(sIdx + 1, workflow.length - 1)

            val instr: List<Instruction> = others.split(",").map {
                if (it.contains(":")) {
                    val rating = it.first().toString()
                    val condition = it[1].toString()
                    val number = numbersRegex.findAll(it).first().value.toLong()
                    val nextInstruction = ":(.*)".toRegex().findAll(it).first().value.drop(1)
                    Instruction.Condition(rating, condition, number, nextInstruction)
                } else {
                    Instruction.Raw(it)
                }
            }

            if (name == "in") {
                startWorkflow = instr
            } else {
                allWorkflows[name] = instr
            }
        }

        val mappedRatings: List<Map<String, Long>> = ratings.split("\n").map { rating ->
            rating.drop(1).dropLast(1).split(",").associate {
                val name = it.first().toString()
                val number = numbersRegex.findAll(it).first().value.toLong()
                name to number
            }
        }

        var currentInstructions: List<Instruction> = startWorkflow
        val final = mutableListOf<Map<String, Long>>()
        mappedRatings.forEach { rating ->
            while (true) {
                val result = travelInstructions(rating, currentInstructions)
                if (result == "A") {
                    final.add(rating)
                    break
                }
                if (result == "R") {
                    break
                }
                currentInstructions = allWorkflows[result]!!
            }
            currentInstructions = startWorkflow
        }

        val result = final.sumOf {
            it.values.sum()
        }

        print(result)
    }

    private fun travelInstructions(rating: Map<String, Long>, instructions: List<Instruction>): String {
        for (instr in instructions) {
            val result = when (instr) {
                is Instruction.Raw -> instr.instr
                is Instruction.Condition -> {
                    val number = rating[instr.rating]!!
                    instr.shouldJump(number)
                }
            }
            if (result != null) {
                return result
            }
        }
        error("Wrong state")
    }

    private fun part2(input: String) {
        var startWorkflow: List<Instruction> = emptyList()
        val allWorkflows: MutableMap<String, List<Instruction>> = mutableMapOf()
        val (workflows, _) = input.split("\n\n")

        workflows.split("\n").forEach { workflow ->
            val sIdx = workflow.indexOfFirst { it == '{' }
            val name = workflow.substring(0, sIdx)
            val others = workflow.substring(sIdx + 1, workflow.length - 1)

            val instr: List<Instruction> = others.split(",").map {
                if (it.contains(":")) {
                    val rating = it.first().toString()
                    val condition = it[1].toString()
                    val number = numbersRegex.findAll(it).first().value.toLong()
                    val nextInstruction = ":(.*)".toRegex().findAll(it).first().value.drop(1)
                    Instruction.Condition(rating, condition, number, nextInstruction)
                } else {
                    Instruction.Raw(it)
                }
            }

            if (name == "in") {
                startWorkflow = instr
            } else {
                allWorkflows[name] = instr
            }
        }

        val paths = startWorkflow.flatMapIndexed { index, instruction ->
            val prev: List<Instruction> = startWorkflow.take(index).map { it.opposite() }
            findAcceptedPaths(instruction, allWorkflows, prev)
        }

        val result = paths.sumOf { path ->
            var numbers = Numbers()
            path.filterIsInstance<Instruction.Condition>().forEach { instruction ->
                numbers = numbers.modify(
                    rating = instruction.rating,
                    condition = instruction.condition,
                    value = instruction.number
                )
            }
            numbers.calculateProbabilities()
        }

        println(result)
    }

    sealed class Instruction {
        abstract fun opposite(): Instruction
        abstract fun nextInstr(): String

        fun isAccepted(): Boolean {
            return when (this) {
                is Raw -> instr == "A"
                is Condition -> nextInstruction == "A"
            }
        }

        fun isRejected(): Boolean {
            return when (this) {
                is Raw -> instr == "R"
                is Condition -> nextInstruction == "R"
            }
        }

        data class Raw(val instr: String) : Instruction() {
            override fun opposite(): Instruction = this
            override fun nextInstr(): String = instr
        }

        data class Condition(val rating: String, val condition: String, val number: Long, val nextInstruction: String) :
            Instruction() {
            fun shouldJump(n: Long): String? {
                return when (condition) {
                    "<" -> if (n < number) nextInstruction else null
                    ">" -> if (n > number) nextInstruction else null
                    else -> error("Unknown")
                }
            }

            override fun opposite(): Condition = copy(condition = condition.opposite())

            override fun nextInstr(): String = nextInstruction

            private fun String.opposite(): String {
                return when (this) {
                    "<" -> ">="
                    ">" -> "<="
                    else -> error("Wrong")
                }
            }
        }
    }

    private fun findAcceptedPaths(
        instruction: Instruction,
        workflows: Map<String, List<Instruction>>,
        result: List<Instruction>
    ): List<List<Instruction>> {
        if (instruction.isAccepted()) return listOf(result.plus(instruction))
        if (instruction.isRejected()) return emptyList()

        val nextInstructions = workflows[instruction.nextInstr()]!!

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
        fun modify(rating: String, condition: String, value: Long): Numbers {
            return when (rating) {
                "x" -> copy(x = x.update(condition, value))
                "m" -> copy(m = m.update(condition, value))
                "a" -> copy(a = a.update(condition, value))
                "s" -> copy(s = s.update(condition, value))
                else -> error("Wrong $rating")
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