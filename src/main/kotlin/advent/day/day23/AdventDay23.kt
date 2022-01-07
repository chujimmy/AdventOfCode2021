package advent.day.day23

import advent.AdventDay
import advent.day.day21.domain.PositionAndScore
import java.math.BigInteger
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class AdventDay23 : AdventDay() {
    private val fileText = getFileAsText("day23")
    private val initialRoom = fileText
        .split("\n")
        .subList(2, 4)
        .map { it.replace("#", "").trim() }

    private val hallway = CharArray(11) { if (it in intArrayOf(3, 5, 7)) '#' else '.' }
    private val roomsList = List(4) { Stack<Char>() }
    private val initialSize = 2

    override fun run() {
        runPart01()
    }

    private fun runPart01() {
        initialRoom
            .reversed()
            .forEach { it.forEachIndexed { i, c -> roomsList[i].add(c) } }

        println(areAmphipodsInTheirsRooms(roomsList))
    }

    private fun can(): Boolean {
        return false
    }

    private fun areAmphipodsInTheirsRooms(roomsList: List<Stack<Char>>): Boolean {
        return roomsList.foldIndexed(true) { i, acc, stack  ->
            acc && stack.size == initialSize && stack.all { it.code == 'A'.code + i }
        }
    }
}
