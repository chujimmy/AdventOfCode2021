package advent.day.day19

import advent.AdventDay
import advent.day.day19.domain.Axis
import advent.day.day19.domain.Axis.*
import advent.day.day19.domain.Coordinates
import advent.day.day19.domain.Rotation
import advent.day.day19.domain.Scanner

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
        .map { pair ->
            val scannerReadings = pair.second
                .map { it.split(",") }
                .map { Coordinates(it[0].toDouble(), it[1].toDouble(), it[2].toDouble() ) }
            Scanner(pair.first, scannerReadings)
        }

    override fun run() {
        val scannerPositions = mutableMapOf<Int, Pair<Coordinates, Triple<Int, Int, Int>>>()
        scannerPositions[0] = Pair(Coordinates(0.0, 0.0, 0.0), Triple(1, 1, 1))

        val beaconCombinationsPerScanner = scannersList
            .map { currentScanner ->
                val combinations = currentScanner.readings.flatMapIndexed { i, firstBeacon ->
                    currentScanner.readings.subList(i + 1, currentScanner.readings.size).mapIndexed { _, secondBeacon ->
                        Pair(firstBeacon, secondBeacon)
                    }
                }
                Pair(currentScanner.id, combinations)
            }


        val scannersCombinations = beaconCombinationsPerScanner.flatMapIndexed { i, firstScanner ->
            beaconCombinationsPerScanner.subList(i + 1, beaconCombinationsPerScanner.size).map { secondScanner ->
                listOf(firstScanner, secondScanner)
            }
        }

        for (scannerPair in scannersCombinations) {
            val scannerOneId = scannerPair[0].first
            val scannerOneBeaconCombinations = scannerPair[0].second
            val scannerTwoId = scannerPair[1].first
            val scannerTwoBeaconCombinations = scannerPair[1].second

            if (scannerOneId !in scannerPositions || scannerTwoId in scannerPositions) {
                continue
            }

            val beaconPairWithSameDistance: Map<Double, List<Pair<Coordinates, Coordinates>>> = scannerOneBeaconCombinations
                .plus(scannerTwoBeaconCombinations)
                .groupBy { it.first.getDistance(it.second) }
                .filter { it.value.size > 1 }

            val beaconsInScannerOnePresentInScannerTwo = beaconPairWithSameDistance.values
                .flatMap { setOf(it[0].first, it[0].second) }
                .toSet()

            if (beaconsInScannerOnePresentInScannerTwo.size < 12) {
                continue
            }

            val beaconsCoordinatesMap = mutableMapOf<Coordinates, Coordinates>()

            beaconsInScannerOnePresentInScannerTwo.forEach { beaconCoordinatesInScannerOne ->
                val beaconCoordinatesInScannerTwo = beaconPairWithSameDistance
                    .values
                    .filter { beaconCoordinatesInScannerOne in listOf(it[0].first, it[0].second) }
                    .take(2)
                    .flatMap { listOf(it[1].first, it[1].second) }
                    .groupingBy { it }
                    .eachCount()
                    .filter { it.value == 2 }
                    .keys
                    .first()

                beaconsCoordinatesMap[beaconCoordinatesInScannerOne] = beaconCoordinatesInScannerTwo
            }

            val scannerTwoCoordinates = findScannerCoordinates(beaconsCoordinatesMap)

            if (scannerTwoCoordinates != null) {
                scannerPositions[scannerTwoId] = scannerTwoCoordinates
            }
            println("scannerPositions $scannerPositions")
        }

    }

    private fun findScannerCoordinates(beaconsCoordinatesMap: Map<Coordinates, Coordinates>): Coordinates? {
        val rotations = setOf(
            listOf(Rotation(X, 1), Rotation(Y, 1), Rotation(Z, 1)),
            listOf(Rotation(X, 1), Rotation(Z, 1), Rotation(Y, -1)),
            listOf(Rotation(X, 1), Rotation(Y, -1), Rotation(Z, -1)),
            listOf(Rotation(X, 1), Rotation(Z, -1), Rotation(Y, 1)),
            listOf(Rotation(Z, 1), Rotation(Y, 1), Rotation(X, -1)),
            listOf(Rotation(Z, 1), Rotation(X, -1), Rotation(Y, -1)),
            listOf(Rotation(Z, 1), Rotation(Y, -1), Rotation(X, 1)),
            listOf(Rotation(Z, 1), Rotation(X, 1), Rotation(Y, 1)),
            listOf(Rotation(X, -1), Rotation(Y, 1), Rotation(Z, -1)),
            listOf(Rotation(X, -1), Rotation(Z, 1), Rotation(Y, 1)),
            listOf(Rotation(X, -1), Rotation(Y, -1), Rotation(Z, 1)),
            listOf(Rotation(X, -1), Rotation(Z, -1), Rotation(Y, -1)),
            listOf(Rotation(Z, -1), Rotation(Y, 1), Rotation(X, 1)),
            listOf(Rotation(Z, -1), Rotation(X, -1), Rotation(Y, 1)),
            listOf(Rotation(Z, -1), Rotation(Y, -1), Rotation(X, -1)),
            listOf(Rotation(Z, -1), Rotation(X, 1), Rotation(Y, -1)),
            listOf(Rotation(Y, 1), Rotation(X, -1), Rotation(Z, 1)),
            listOf(Rotation(Y, 1), Rotation(Z, 1), Rotation(X, 1)),
            listOf(Rotation(Y, 1), Rotation(X, 1), Rotation(Z, -1)),
            listOf(Rotation(Y, 1), Rotation(Z, -1), Rotation(X, -1)),
            listOf(Rotation(Y, -1), Rotation(X, -1), Rotation(Z, -1)),
            listOf(Rotation(Y, -1), Rotation(Z, 1), Rotation(X, -1)),
            listOf(Rotation(Y, -1), Rotation(X, 1), Rotation(Z, 1)),
            listOf(Rotation(Y, -1), Rotation(Z, -1), Rotation(X, 1)),
        )

        val coordinates = beaconsCoordinatesMap
            .entries
            .fold(emptySet<Pair<Coordinates, List<Rotation>>>()) { acc, entry ->
                val beaconCoordinatesScannerOne = entry.key
                val beaconCoordinatesScannerTwo = entry.value
                val scannerTwoPossibleCoordinates = rotations.map {
                    val c = Coordinates(
                        beaconCoordinatesScannerOne.x - getValue(X, it, beaconCoordinatesScannerTwo),
                        beaconCoordinatesScannerOne.y - getValue(Y, it, beaconCoordinatesScannerTwo),
                        beaconCoordinatesScannerOne.z - getValue(Z, it, beaconCoordinatesScannerTwo),
                    )
                    Pair(c, it)
                }.toSet()

                if (acc.isEmpty()) {
                    scannerTwoPossibleCoordinates
                } else {
                    acc.intersect(scannerTwoPossibleCoordinates)
                }
            }

        if (coordinates.isNotEmpty()) {
            println("Found coordinates of a scanner ${coordinates.first()}")
            return coordinates.first()
        }
//        val offsets = setOf(
//            Triple(1,1,1),
//            Triple(1,1,-1),
//            Triple(1,-1,1),
//            Triple(1,-1,-1),
//            Triple(-1,1,1),
//            Triple(-1,1,-1),
//            Triple(-1,-1,1),
//            Triple(-1,-1,-1),
//        )

//        var possibleCoordinates = mutableListOf<Pair<Coordinates, Triple<Int, Int, Int>>>()
//
//        val keys = beaconsCoordinatesMap.keys.toMutableList()
//        val key = keys.removeFirst()
//        val value = beaconsCoordinatesMap[key]!!
//
//        possibleCoordinates.addAll(offsets.map { triple ->
//            Pair(
//                Coordinates(
//                    key.x + (value.x * triple.first),
//                    key.y + (value.y * triple.second),
//                    key.z + (value.z * triple.third),
//                ),
//                triple
//            )
//        })

//        while(possibleCoordinates.size > 1) {
//            val k = keys.removeFirst()
//            val v = beaconsCoordinatesMap[k]!!
//            val aaa = offsets.map { triple ->
//
//                Pair(
//                    Coordinates(
//                        k.x + (v.x * triple.first),
//                        k.y + (v.y * triple.second),
//                        k.z + (v.z * triple.third),
//                    ),
//                    triple
//                )
//            }.toList()
//
//            possibleCoordinates = (possibleCoordinates.intersect(aaa)).toMutableList()
//        }

//        if (possibleCoordinates.isNotEmpty()) {
//            println("Found coordinates of a scanner ${possibleCoordinates[0]}")
//            return possibleCoordinates[0]
//        }

        return null
    }

    private fun getValue(axis: Axis, rotationList: List<Rotation>, coordinates: Coordinates): Double {
        val index = rotationList.indexOfFirst { r -> r.axis == axis }
        val facing = rotationList[index].rotation

        return when(index) {
            0 -> coordinates.x * facing
            1 -> coordinates.y * facing
            2 -> coordinates.z * facing
            else -> 0.0
        }
    }
}
