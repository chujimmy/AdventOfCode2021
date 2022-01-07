package advent.day.day23

import advent.AdventDay
import advent.day.day23.domain.Amphipod
import advent.day.day23.domain.Step
import java.lang.Integer.max
import java.lang.Integer.min
import java.lang.StrictMath.abs
import java.lang.StringBuilder

typealias Rooms = Array<Array<Amphipod?>>
typealias Hallway = Array<Amphipod?>

class AdventDay23 : AdventDay() {
    private val inputRooms = getFileAsText("day23")
        .split("\n")
        .subList(2, 4)
        .map { it.replace("#", "").trim() }
        .toTypedArray()

    private val roomIndexToAmphipodMap = mapOf(0 to Amphipod.A, 1 to Amphipod.B, 2 to Amphipod.C, 3 to Amphipod.D)
    private val amphipodToRoomIndexMap = roomIndexToAmphipodMap.entries.associate { e -> e.value to e.key }

    private fun initRooms(inputRooms: Array<String>): Array<Array<Amphipod?>> {
        return Array(4) { r -> Array(inputRooms.size) { p -> Amphipod.fromType(inputRooms[p][r]) } }
    }

    override fun run() {
        runPart01()
        runPart02()
    }

    private fun runPart01() {
        solveAndPrint(initRooms(inputRooms), Array(11) { null })
    }

    private fun runPart02() {
        solveAndPrint(initRooms(arrayOf(inputRooms[0], "DCBA", "DBAC", inputRooms[1])), Array(11) { null })
    }

    private fun solveAndPrint(rooms: Rooms, hallway: Hallway) {
        val initialStep = stepStr(rooms, hallway, 0)
        val shortestEnergySteps = solve(rooms, hallway)
            .filterNot { it.any { step -> step.energy == Int.MAX_VALUE } }
            .minByOrNull { it.sumOf { s -> s.energy } }!!

        val stepsStr = shortestEnergySteps
            .joinToString(separator = "", prefix = initialStep) { stepStr(it.rooms, it.hallway, it.energy) }

        println("Total energy: ${shortestEnergySteps.sumOf { it.energy }}")
        println("${shortestEnergySteps.size} Steps:\n$stepsStr")
    }

    private fun solve(rooms: Rooms, hallway: Hallway): List<List<Step>> {
        val commonSteps = mutableListOf<Step>()

        var hallwayAmphipodsMovableToRoom = getHallwayAmphipodsMovableToRoom(rooms, hallway)
        var roomAmphipodsMovableToRoom = getRoomAmphipodsMovableToRoom(rooms, hallway)

        while (hallwayAmphipodsMovableToRoom.isNotEmpty() || roomAmphipodsMovableToRoom.isNotEmpty()) {
            hallwayAmphipodsMovableToRoom
                .map { moveHallwayAmphipodToRoom(it, rooms, hallway) }
                .forEach { res -> commonSteps.add(res) }

            roomAmphipodsMovableToRoom
                .map { moveRoomAmphipodToRoom(rooms, hallway, it) }
                .forEach { res -> commonSteps.add(res) }

            hallwayAmphipodsMovableToRoom = getHallwayAmphipodsMovableToRoom(rooms, hallway)
            roomAmphipodsMovableToRoom = getRoomAmphipodsMovableToRoom(rooms, hallway)
        }

        if (isCompleted(rooms, hallway)) {
            return listOf(commonSteps)
        }

        val possibleMovesToHallway = getRoomAmphipodsMovableToHallway(rooms)
            .flatMap { rIndex ->
                getHallwayFreeSpaces(hallway)
                    .filter { hIndex -> canAmphipodMoveInHallway(hallway, getRoomEntranceHallwayIndex(rIndex), hIndex) }
                    .map { hIndex -> Pair(rIndex, hIndex) }
            }

        if (possibleMovesToHallway.isEmpty()) {
            commonSteps.add(Step(rooms, hallway, Int.MAX_VALUE))
            return listOf(commonSteps)
        }

        return possibleMovesToHallway
            .map { Triple(rooms.map { r -> r.copyOf() }.toTypedArray(), hallway.copyOf(), it) }
            .map { moveRoomAmphipodsToHallway(it.first, it.second, it.third.first, it.third.second) }
            .map { Pair(Step(it.rooms, it.hallway, it.energy), solve(it.rooms, it.hallway)) }
            .flatMap { it.second.map { s -> commonSteps.plus(it.first).plus(s) } }
    }

    private fun getHallwayAmphipodsMovableToRoom(rooms: Rooms, hallway: Hallway): List<Int> {
        return hallway
            .mapIndexedNotNull { i, a -> if (a != null) Pair(a, i) else null }
            .filter { canAmphipodEnterItFinalRoom(it.first, rooms) }
            .map { Triple(it.first, it.second, getRoomEntranceHallwayIndex(amphipodToRoomIndexMap.getValue(it.first))) }
            .filter { canAmphipodMoveInHallway(hallway, it.second, it.third) }
            .map { it.second }
    }

    private fun moveHallwayAmphipodToRoom(hallwayAmphipodIndex: Int, rooms: Rooms, hallway: Hallway): Step {
        val amphipodToMove = hallway[hallwayAmphipodIndex] ?: throw Exception()
        val roomIndex = amphipodToRoomIndexMap.getValue(amphipodToMove)
        val roomEntranceHallwayIndex = getRoomEntranceHallwayIndex(roomIndex)
        val positionInRoomIndex = rooms[roomIndex].indexOfLast { it == null }
        val stepsCount = abs(hallwayAmphipodIndex - roomEntranceHallwayIndex) + positionInRoomIndex + 1

        rooms[roomIndex][positionInRoomIndex] = amphipodToMove
        hallway[hallwayAmphipodIndex] = null

        return Step(rooms, hallway, amphipodToMove.energy * stepsCount)
    }

    private fun getRoomAmphipodsMovableToRoom(rooms: Rooms, hallway: Hallway): List<Pair<Int, Int>> {
        return rooms
            .map { r -> r.firstOrNull { a -> a != null } }
            .mapIndexedNotNull { i, a -> if (a != null) Pair(a, i) else null }
            .filter { it.first != roomIndexToAmphipodMap.getValue(it.second) }
            .filter { canAmphipodEnterItFinalRoom(it.first, rooms) }
            .map { Pair(it.second, amphipodToRoomIndexMap.getValue(it.first)) }
            .filter {
                val startRoomEntranceHallwayIndex = getRoomEntranceHallwayIndex(it.first)
                val endRoomEntranceHallwayIndex = getRoomEntranceHallwayIndex(it.second)
                canAmphipodMoveInHallway(hallway, startRoomEntranceHallwayIndex, endRoomEntranceHallwayIndex)
            }
    }

    private fun moveRoomAmphipodToRoom(rooms: Rooms, hallway: Hallway, roomStartEndIndexes: Pair<Int, Int>): Step {
        val startRoomIndex = roomStartEndIndexes.first
        val endRoomIndex = roomStartEndIndexes.second
        val amphipodToMove = rooms[startRoomIndex].first { it != null } ?: throw Exception()

        val positionInStartRoom = rooms[startRoomIndex].indexOfFirst { it != null }
        val startRoomEntranceHallwayIndex = getRoomEntranceHallwayIndex(startRoomIndex)
        val positionInEndRoom = rooms[endRoomIndex].indexOfLast { it == null }
        val endRoomEntranceHallwayIndex = getRoomEntranceHallwayIndex(endRoomIndex)

        val distanceInHallway = abs(startRoomEntranceHallwayIndex - endRoomEntranceHallwayIndex)
        val energy = amphipodToMove.energy * (distanceInHallway + positionInStartRoom + 1 + positionInEndRoom + 1)
        rooms[startRoomIndex][positionInStartRoom] = null
        rooms[endRoomIndex][positionInEndRoom] = amphipodToMove

        return Step(rooms, hallway, energy)
    }

    private fun getRoomAmphipodsMovableToHallway(rooms: Rooms): List<Int> {
        return rooms
            .mapIndexed { i, r -> Pair(r, i) }
            .filter { pair -> pair.first.any { it != null } }
            .filterNot { pair -> pair.first.filterNotNull().all { it == roomIndexToAmphipodMap.getValue(pair.second) } }
            .map { it.second }
    }

    private fun moveRoomAmphipodsToHallway(rooms: Rooms, hallway: Hallway, roomIndex: Int, hallwayIndex: Int): Step {
        val amphipodIndexInRoom = rooms[roomIndex].indexOfFirst { it != null }
        val amphipodToMove = rooms[roomIndex][amphipodIndexInRoom] ?: throw Exception("Invalid Room Index")

        val stepsCount = abs(hallwayIndex - getRoomEntranceHallwayIndex(roomIndex)) + amphipodIndexInRoom + 1
        val energy = amphipodToMove.energy * stepsCount

        rooms[roomIndex][amphipodIndexInRoom] = null
        hallway[hallwayIndex] = amphipodToMove

        return Step(rooms, hallway, energy)
    }

    private fun getRoomEntranceHallwayIndex(roomIndex: Int): Int {
        return if (roomIndex in 0..4) ((roomIndex + 1) * 2) else throw Exception("Invalid Room Index")
    }

    private fun canAmphipodEnterItFinalRoom(amphipod: Amphipod, rooms: Rooms): Boolean {
        val amphipodRoomIndex = amphipodToRoomIndexMap.getValue(amphipod)
        val amphipodRoom = rooms[amphipodRoomIndex]

        return amphipodRoom.all { it == amphipod || it == null }
    }

    private fun canAmphipodMoveInHallway(hallway: Array<Amphipod?>, start: Int, end: Int): Boolean {
        val min = min(start, end)
        val max = max(start, end)

        return hallway[end] == null && hallway.copyOfRange(min + 1, max).all { it == null }
    }

    private fun isCompleted(rooms: Array<Array<Amphipod?>>, hallway: Array<Amphipod?>): Boolean {
        val isHallwayEmpty = hallway.all { it == null }
        val areRoomsAllFilledWithCorrectAmphipods = rooms
            .mapIndexed { i, r -> r.all { it == roomIndexToAmphipodMap.getValue(i) } }
            .reduce { acc, bool -> acc && bool }

        return isHallwayEmpty && areRoomsAllFilledWithCorrectAmphipods
    }

    private fun getHallwayFreeSpaces(hallway: Array<Amphipod?>): List<Int> {
        return hallway
            .mapIndexedNotNull { i, a -> if (a == null) i else null }
            .filter { it !in listOf(2, 4, 6, 8) }
    }

    private fun stepStr(rooms: Rooms, hallway: Hallway, energy: Int): String {
        val builder = StringBuilder("\nEnergy: ${energy}\n#############\n")
            .append(hallway.map { it?.type ?: '.' }.joinToString(prefix = "#", postfix = "#", separator = ""))
            .append("\n")

        (0 until rooms[0].size).forEach { i ->
            val prefix = if (i == 0) "###" else "  #"
            val postfix = if (i == 0) "###" else "#  "

            val line = rooms
                .map { it[i] ?: "." }
                .joinToString(prefix = prefix, postfix = postfix, separator = "#")

            builder.append(line).append("\n")
        }

        return builder.append("  #########  \n").toString()
    }
}
