package advent.day.day19

import advent.AdventDay
import advent.day.day19.domain.Axis.X
import advent.day.day19.domain.Axis.Y
import advent.day.day19.domain.Axis.Z
import advent.day.day19.domain.Coordinates
import advent.day.day19.domain.Orientation.NEGATIVE
import advent.day.day19.domain.Orientation.POSITIVE
import advent.day.day19.domain.RelativePosition
import advent.day.day19.domain.Rotation
import advent.day.day19.domain.Scanner
import extension.list.combination
import extension.list.combinationList

class AdventDay19 : AdventDay() {
    private val fileText = getFileAsText("day19")
    private val scannersList = fileText
        .split("\n\n")
        .map { it.split("\n") }
        .map { Pair(it.subList(0, 1)[0], it.subList(1, it.size)) }
        .map { Pair(it.first.replace(Regex("[a-zA-z -]"), "").toInt(), it.second) }
        .map { Scanner(it.first, it.second.map { s -> Coordinates.fromString(s) }) }

    private val rotations = listOf(
        Rotation(POSITIVE, X, POSITIVE, Y, POSITIVE, Z),
        Rotation(POSITIVE, X, POSITIVE, Z, NEGATIVE, Y),
        Rotation(POSITIVE, X, NEGATIVE, Y, NEGATIVE, Z),
        Rotation(POSITIVE, X, NEGATIVE, Z, POSITIVE, Y),
        Rotation(POSITIVE, Z, POSITIVE, Y, NEGATIVE, X),
        Rotation(POSITIVE, Z, NEGATIVE, X, NEGATIVE, Y),
        Rotation(POSITIVE, Z, NEGATIVE, Y, POSITIVE, X),
        Rotation(POSITIVE, Z, POSITIVE, X, POSITIVE, Y),
        Rotation(NEGATIVE, X, POSITIVE, Y, NEGATIVE, Z),
        Rotation(NEGATIVE, X, POSITIVE, Z, POSITIVE, Y),
        Rotation(NEGATIVE, X, NEGATIVE, Y, POSITIVE, Z),
        Rotation(NEGATIVE, X, NEGATIVE, Z, NEGATIVE, Y),
        Rotation(NEGATIVE, Z, POSITIVE, Y, POSITIVE, X),
        Rotation(NEGATIVE, Z, NEGATIVE, X, POSITIVE, Y),
        Rotation(NEGATIVE, Z, NEGATIVE, Y, NEGATIVE, X),
        Rotation(NEGATIVE, Z, POSITIVE, X, NEGATIVE, Y),
        Rotation(POSITIVE, Y, NEGATIVE, X, POSITIVE, Z),
        Rotation(POSITIVE, Y, POSITIVE, Z, POSITIVE, X),
        Rotation(POSITIVE, Y, POSITIVE, X, NEGATIVE, Z),
        Rotation(POSITIVE, Y, NEGATIVE, Z, NEGATIVE, X),
        Rotation(NEGATIVE, Y, NEGATIVE, X, NEGATIVE, Z),
        Rotation(NEGATIVE, Y, POSITIVE, Z, NEGATIVE, X),
        Rotation(NEGATIVE, Y, POSITIVE, X, POSITIVE, Z),
        Rotation(NEGATIVE, Y, NEGATIVE, Z, POSITIVE, X),
    )

    override fun run() {
        val scannersCoordInfo = mutableMapOf(
            0 to RelativePosition(Coordinates(0.0, 0.0, 0.0), rotations[0], 0)
        )

        while (scannersCoordInfo.size != scannersList.size) {
            for (scannerPair in scannersList.combinationList()) {
                val scannerPairIds = scannerPair.map { it.id }
                val knownScannerIds = scannerPairIds.intersect(scannersCoordInfo.keys)
                if (knownScannerIds.size != 1) {
                    continue
                }

                val knownScannerId = knownScannerIds.first()
                val newScannerId = scannerPairIds.minus(knownScannerId).first()

                val beaconCombinationPairsWithSameDistance = scannersList[knownScannerId].beacons.combination()
                    .plus(scannersList[newScannerId].beacons.combination())
                    .groupBy { it.first.getDistance(it.second) }
                    .filter { it.value.size == 2 }

                val beaconsMap = beaconCombinationPairsWithSameDistance.values
                    .flatMap { setOf(it[0].first, it[0].second) }
                    .toSet()
                    .associateWith { beaconInBaseScanner ->
                        beaconCombinationPairsWithSameDistance
                            .filter { beaconInBaseScanner in listOf(it.value[0].first, it.value[0].second) }
                            .flatMap { listOf(it.value[1].first, it.value[1].second) }
                            .groupingBy { it }
                            .eachCount()
                            .entries
                            .reduce { a, c -> if (c.value > a.value) c else a }
                            .takeIf { it.value > 1 }?.key
                    }
                    .filter { it.value != null } as Map<Coordinates, Coordinates>

                if (beaconsMap.size < 12) {
                    continue
                }

                scannersCoordInfo[newScannerId] = beaconsMap
                    .entries
                    .map {
                        rotations.map { r ->
                            val potentialCoord = it.key - it.value.translateCoordinates(r)
                            RelativePosition(potentialCoord, r, knownScannerId)
                        }
                    }
                    .reduce { acc, list -> acc.intersect(list).toList() }
                    .first()
            }
        }

        val beaconsAbsCoordinates = scannersList
            .flatMapIndexed { i, it -> it.beacons.map { coord -> getAbsCoord(coord, i, scannersCoordInfo) } }
            .toSet()

        println("Number of unique beacons: ${beaconsAbsCoordinates.size}")

        val scannersMaxManhattanDistance = scannersCoordInfo
            .values
            .map { getAbsCoord(it.coordFromKnownScanner, it.knownScannerId, scannersCoordInfo) }
            .combination()
            .map { it.first.getManhattanDistance(it.second) }
            .maxOf { it }

        println("Maximum Manhattan distance between 2 scanners: $scannersMaxManhattanDistance")
    }

    private fun getAbsCoord(
        coordFromCurrentScannerPOV: Coordinates,
        currentScannerId: Int,
        scannersCoordInfo: Map<Int, RelativePosition>,
    ): Coordinates {
        if (currentScannerId == 0) {
            return coordFromCurrentScannerPOV
        }

        val nextScanner = scannersCoordInfo.getValue(currentScannerId)
        val rotationToApply = nextScanner.rotationFromKnownScanner
        val nextCurrentScannerId = nextScanner.knownScannerId

        val translatedCoordWithNextScannerRotation = coordFromCurrentScannerPOV.translateCoordinates(rotationToApply)
        val coordFromNextScannerPOV = translatedCoordWithNextScannerRotation + nextScanner.coordFromKnownScanner

        return getAbsCoord(coordFromNextScannerPOV, nextCurrentScannerId, scannersCoordInfo)
    }
}
