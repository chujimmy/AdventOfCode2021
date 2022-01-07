package advent.day.day23.domain

import advent.day.day23.Hallway
import advent.day.day23.Rooms

class Step(roomsInput: Rooms, hallwayInput: Hallway, val energy: Int) {
    val rooms: Rooms = roomsInput.map { it.copyOf() }.toTypedArray()
    val hallway: Hallway = hallwayInput.copyOf()
}
