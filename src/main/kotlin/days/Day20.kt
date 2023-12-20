package days

import readLines
import java.util.*

object Day20 {

    operator fun invoke() {
        println("🎄Day 20 🎄")
        print("Part 1: ")
        part1(readLines("20"))
        println()
        print("Part 2: ")
        part2(readLines("20"))
    }

    private fun part1(input: List<String>) {
        val connectionMap = mutableMapOf<String, List<String>>()
        val modules: MutableMap<String, Module> = input.associate { line ->
            val (name, sendToRaw) = line.split(" -> ")
            val sendTo = sendToRaw.split(",").map { it.trim() }.toSet()
            sendTo.forEach {
                connectionMap[it] = connectionMap.getOrDefault(it, emptyList()).plus(name.clear())
            }
            val module: Module = when {
                name == "broadcaster" -> Module.Broadcaster(sendTo = sendTo)
                name.contains("%") -> Module.FlipFlop(name.clear(), sendTo)
                name.contains("&") -> Module.Conjunction(name.clear(), sendTo)
                else -> error("Wrong $name")
            }
            name.clear() to module
        }.toMutableMap()

        modules.filter { (_, value) ->
            value is Module.Conjunction
        }.forEach { (key, _) ->
            (modules[key] as Module.Conjunction).updateConnectedModules(connectionMap.getValue(key))
        }


        var low = 0L
        var high = 0L
        (0..<1000).forEach {
            val (lowCount, highCount) = doTheCycle(modules)
            low += lowCount
            high += highCount
        }
        println(low * high)
    }

    private fun doTheCycle(modules: MutableMap<String, Module>): Pair<Int, Int> {
        var lowCount = 1
        var highCount = 0

        val queue = LinkedList<ModuleWIthPulseToProcess>()
        modules.getValue("broadcaster").processPulse("", Pulse.Low).forEach(queue::offer)

        var c = 0
        while (true) {
            val (mName, pulse, from) = queue.poll() ?: break
            if (pulse == Pulse.Low) lowCount++
            if (pulse == Pulse.High) highCount++
            if (modules.contains(mName)) {
                modules.getValue(mName).processPulse(from, pulse).forEach(queue::offer)
            }
            c++
        }

        return lowCount to highCount
    }

    private fun String.clear(): String {
        return replace("%", "").replace("&", "")
    }

    private fun part2(input: List<String>) {
        val connectionMap = mutableMapOf<String, List<String>>()
        val modules: MutableMap<String, Module> = input.associate { line ->
            val (name, sendToRaw) = line.split(" -> ")
            val sendTo = sendToRaw.split(",").map { it.trim() }.toSet()
            sendTo.forEach {
                connectionMap[it] = connectionMap.getOrDefault(it, emptyList()).plus(name.clear())
            }
            val module: Module = when {
                name == "broadcaster" -> Module.Broadcaster(sendTo = sendTo)
                name.contains("%") -> Module.FlipFlop(name.clear(), sendTo)
                name.contains("&") -> Module.Conjunction(name.clear(), sendTo)
                else -> error("Wrong $name")
            }
            name.clear() to module
        }.toMutableMap()

        modules.filter { (_, value) ->
            value is Module.Conjunction
        }.forEach { (key, _) ->
            (modules[key] as Module.Conjunction).updateConnectedModules(connectionMap.getValue(key))
        }

        var buttonPushed = 1
        while (true) {
            val wasLow = doTheCycleTwo(modules)
            if (wasLow) break
            buttonPushed++
        }

        println(buttonPushed)
    }

    enum class Pulse {
        Low, High
    }

    sealed class Module {
        abstract fun processPulse(from: String, pulse: Pulse): List<ModuleWIthPulseToProcess>

        data class Broadcaster(val sendTo: Set<String>) : Module() {
            override fun processPulse(from: String, pulse: Pulse): List<ModuleWIthPulseToProcess> {
                return sendTo.map { ModuleWIthPulseToProcess(it, pulse, "broadcaster") }
            }
        }

        data class FlipFlop(val name: String, val sendTo: Set<String>, var isOn: Boolean = false) : Module() {
            override fun processPulse(from: String, pulse: Pulse): List<ModuleWIthPulseToProcess> {
                if (pulse == Pulse.High) return emptyList()
                return if (isOn) {
                    isOn = false
                    sendTo.map { ModuleWIthPulseToProcess(it, Pulse.Low, name) }
                } else {
                    isOn = true
                    sendTo.map { ModuleWIthPulseToProcess(it, Pulse.High, name) }
                }
            }
        }

        data class Conjunction(
            val name: String,
            val sendTo: Set<String>,
            var connectedModules: MutableMap<String, Pulse> = mutableMapOf()
        ) : Module() {

            fun updateConnectedModules(cm: List<String>) {
                connectedModules = cm.associateWith { Pulse.Low }.toMutableMap()
            }

            override fun processPulse(from: String, pulse: Pulse): List<ModuleWIthPulseToProcess> {
                connectedModules[from] = pulse
                return if (connectedModules.values.all { it == Pulse.High }) {
                    sendTo.map { ModuleWIthPulseToProcess(it, Pulse.Low, name) }
                } else {
                    sendTo.map { ModuleWIthPulseToProcess(it, Pulse.High, name) }
                }
            }
        }
    }

    data class ModuleWIthPulseToProcess(val moduleName: String, val pulse: Pulse, val from: String)

    private fun doTheCycleTwo(modules: MutableMap<String, Module>): Boolean {
        var isLow = false

        val queue = LinkedList<ModuleWIthPulseToProcess>()
        modules.getValue("broadcaster").processPulse("", Pulse.Low).forEach(queue::offer)

        var c = 0
        while (true) {
            val (mName, pulse, from) = queue.poll() ?: break
            if (modules.contains(mName)) {
                modules.getValue(mName).processPulse(from, pulse).forEach(queue::offer)
            } else {
                if (mName == "rx") {
                    isLow = pulse == Pulse.Low
                }
            }
            c++
        }

        return isLow
    }
}