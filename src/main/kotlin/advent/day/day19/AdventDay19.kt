package advent.day.day19

import advent.AdventDay
import advent.day.day18.domain.LeftRightNode
import advent.day.day18.domain.Node
import advent.day.day19.domain.Coordinates
import advent.day.day19.domain.Scanner
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.util.Stack
import kotlin.math.pow

class AdventDay19 : AdventDay() {
    private val fileText = getFileAsText("day19")
    private val scannersText = fileText
        .split("\n\n")
        .toList()

    private val scannersList = scannersText
        .map {  it.split("\n") }
        .map {
            Pair(
                it.subList(0, 1)[0].replace("-", "").trim().split(" ")[1].toInt(),
                it.subList(1, it.size),
            )
        }
        .mapIndexed { i, pair ->
            val scannerReadings = pair.second
                .map { it.split(",") }
                .map { Coordinates(it[0].toDouble(), it[1].toDouble(), it[2].toDouble() ) }
            Scanner(pair.first, scannerReadings)
        }

    override fun run() {
        val scannerPositions = mutableMapOf<Int, Pair<Coordinates, Triple<Int, Int, Int>>>()
        scannerPositions[0] = Pair(Coordinates(0.0, 0.0, 0.0), Triple(1, 1, 1))

        val beaconCombinationDistancesPerScanner = scannersList
            .map { currentScanner ->
                val combinations = currentScanner.readings.flatMapIndexed { i, firstBeacon ->
                    currentScanner.readings.subList(i + 1, currentScanner.readings.size).mapIndexed { _, secondBeacon ->
                        val sum = (
                            (firstBeacon.x - secondBeacon.x).pow(2) +
                            (firstBeacon.y - secondBeacon.y).pow(2) +
                            (firstBeacon.z - secondBeacon.z).pow(2)
                        )
                        val distance = kotlin.math.sqrt(sum)
                        val beaconPair = Pair(firstBeacon, secondBeacon)

                        Pair(beaconPair, distance)
                    }
                }
                Pair(currentScanner.id, combinations)
            }


        val hhhh = beaconCombinationDistancesPerScanner.flatMapIndexed { i, firstScanner ->
            beaconCombinationDistancesPerScanner.subList(i + 1, beaconCombinationDistancesPerScanner.size).mapIndexed { j, secondScanner ->
                listOf(firstScanner, secondScanner)
            }
        }

        for (scannerPair in hhhh) {
            val firstScannerNumber = scannerPair[0].first
            val secondScannerNumber = scannerPair[1].first
            val firstScanner = scannerPair[0].second
            val secondScanner = scannerPair[1].second

            if (firstScannerNumber !in scannerPositions) {
                continue
            }

            if (secondScannerNumber in scannerPositions) {
                continue
            }


            val BeaconPairWithSameDistance = firstScanner.plus(secondScanner)
                .groupBy { it.second }
                .mapValues { (_, values) -> values.map { it.first } }
                .filter { it.value.size > 1 }

            val beaconsInBothScanners = BeaconPairWithSameDistance.values
                .flatMap { setOf(it[0].first, it[0].second) }
                .toSet()

            // 2 scanners are not overlapping, therefore no need to continue
            if (beaconsInBothScanners.isEmpty()) {
                continue
            }

            val aa = mutableMapOf<Coordinates, Coordinates>()

            beaconsInBothScanners.forEach { beacon ->
                val aaa = BeaconPairWithSameDistance
                    .filter { it.value.size > 1 }
                    .values
                    .filter {
                        it.any { beaconPair ->
                            beaconPair.first == beacon || beaconPair.second == beacon
                        }
                    }.take(2)
                    .flatMap { listOf(it[1].first, it[1].second) }
                    .groupingBy { it }
                    .eachCount()
                    .filter { it.value == 2 }
                    .keys
                    .first()

                aa[beacon] = aaa

            }


            val coordinates = calculateScannerCoordinates(aa, scannerPositions[firstScannerNumber]!!)

            if (coordinates != null) {
                scannerPositions[secondScannerNumber] = coordinates
            }
            println("scannerPositions $scannerPositions")
        }

//        beaconCombinationDistancesPerScanner.windowed(2, 2).forEach { scannerPair ->
//            val firstScanner = scannerPair[0]
//            val secondScanner = scannerPair[1]
//
//            val BeaconPairWithSameDistance = firstScanner.plus(secondScanner)
//                .groupBy { it.second }
//                .mapValues { (_, values) -> values.map { it.first } }
//                .filter { it.value.size > 1 }
//
//            val beaconsInBothScanners = BeaconPairWithSameDistance.values
//                .flatMap { setOf(it[0].first, it[0].second) }
//                .toSet()
//
//            val aa = mutableMapOf<Coordinates, Coordinates>()
//
//            beaconsInBothScanners.forEach { beacon ->
//                val aaa = BeaconPairWithSameDistance
//                    .filter { it.value.size > 1 }
//                    .values
//                    .filter {
//                        it.any { beaconPair ->
//                            beaconPair.first == beacon || beaconPair.second == beacon
//                        }
//                    }.take(2)
//                    .flatMap { listOf(it[1].first, it[1].second) }
//                    .groupingBy { it }
//                    .eachCount()
//                    .filter { it.value == 2 }
//                    .keys
//                    .first()
//
//                aa[beacon] = aaa
//
//
//
//            }
//            val coordinates = calculateScannerCoordinates(aa, scannerPositions[0]!!)
//            println("qddd")
//        }
    }

    private fun calculateScannerCoordinates(
        map: Map<Coordinates, Coordinates>, scannerCoordinates: Pair<Coordinates, Triple<Int, Int, Int>>
    ): Pair<Coordinates, Triple<Int, Int, Int>>? {
        val offsets = setOf(
            Triple(1,1,1),
            Triple(1,1,-1),
            Triple(1,-1,1),
            Triple(1,-1,-1),
            Triple(-1,-1,-1),
            Triple(-1,1,1),
        )

        var possibleCoordinates = mutableListOf<Pair<Coordinates, Triple<Int, Int, Int>>>()

        val keys = map.keys.toMutableList()
        val key = keys.removeFirst()
        val value = map[key]!!

        possibleCoordinates.addAll(offsets.map { triple ->
            Pair(
                Coordinates(
                key.x + (value.x * triple.first),
                key.y + (value.y * triple.second),
                key.z + (value.z * triple.third),
                ),
                triple
            )
        })

        while(possibleCoordinates.size > 1) {
            val k = keys.removeFirst()
            val v = map[k]!!
            val aaa = offsets.map { triple ->

                Pair(
                    Coordinates(
                        k.x + (v.x * triple.first),
                        k.y + (v.y * triple.second),
                        k.z + (v.z * triple.third),
                    ),
                    triple
                )
            }.toList()

            possibleCoordinates = (possibleCoordinates.intersect(aaa)).toMutableList()
        }

        if (possibleCoordinates.isNotEmpty()) {
            println("Found coordinates of a scanner ${possibleCoordinates[0]}")
            return possibleCoordinates[0]
        }

        return null
    }
}
