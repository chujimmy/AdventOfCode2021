package advent.day.day19

import advent.AdventDay
import advent.day.day19.domain.*
import advent.day.day19.domain.Axis.*
import advent.day.day19.domain.Orientation.*
import extension.list.combination
import extension.list.combinationList


class AdventDay19 : AdventDay() {
    private val fileText = getFileAsText("day19")
    private val scannersList = fileText
        .split("\n\n")
        .map {  it.split("\n") }
        .map { Pair(it.subList(0, 1)[0], it.subList(1, it.size)) }
        .map { Pair(it.first.replace(Regex("[a-zA-z -]"), "").toInt(), it.second) }
        .map { Scanner(it.first, it.second.map { s -> Coordinates.fromString(s) } ) }

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
//        scannersList[0].position = ScannerPosition(Coordinates(0.0, 0.0, 0.0), rotations[0], 0)
        val scannersAbsCoordInfo = mutableMapOf(
            0 to ScannerPosition(Coordinates(0.0, 0.0, 0.0), rotations[0], 0)
        )

//        while (scannersList.any { it.position == null } ) {
        while (scannersAbsCoordInfo.size != scannersList.size) {
            for (scannerPair in scannersList.combinationList()) {
                val scannerPairIds = scannerPair.map { it.id }
                val knownScannerIds = scannerPairIds.intersect(scannersAbsCoordInfo.keys)
                if (knownScannerIds.size != 1) {
//                if (scannerPair.map { it.id }.intersect(scannersAbsCoordInfo.keys).size != 1) {
                    continue
                }

                val knownScannerId = knownScannerIds.first()
//                val knownScannerId = scannerPair.filter { it.position != null }.map { it.id }.intersect(scannerPair.map { it.id }).first()
                val knownScanner = scannersList[knownScannerId]

                val newScannerId = scannerPairIds.minus(knownScannerId).first()
                val newScanner = scannersList[newScannerId]

                println("knownScannerIds $knownScannerIds scannerPairIds $scannerPairIds knownScannerId $knownScannerId newScannerId $newScannerId ")

                val beaconCombinationPairsWithSameDistance = knownScanner.beacons.combination()
                    .plus(newScanner.beacons.combination())
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

                scannersAbsCoordInfo[newScannerId] = findScannerInfo(knownScannerId, beaconsMap)
//                newScanner.position = findScannerInfo(knownScannerId, beaconsMap)
            }
        }

        println("scannerPositions \n${scannersList.joinToString("\n")}\n---------\n")

        val scannersAbsoluteCoordinates = scannersAbsCoordInfo
            .values
            .map {
                getAbsoluteCoordinate(it.coordinatesFromMiddleScanner, it.middleScannerId, scannersAbsCoordInfo)
        }

        val beaconsCoordinates = scannersList
            .flatMapIndexed { i, it ->
                it.beacons.map { beacon -> getAbsoluteCoordinate(beacon, i, scannersAbsCoordInfo) }
            }
            .toSet()

        println("Number of unique beacons ${beaconsCoordinates.size}")
    }

    private fun getAbsoluteCoordinate(
        newScannerCoordinatesFromKnownScannerPOV: Coordinates,
        knownScannerId: Int,
        scannersList: Map<Int, ScannerPosition>,
    ): Coordinates {
        if (knownScannerId == 0) {
            return newScannerCoordinatesFromKnownScannerPOV
        }

        val nextScannerInfo = scannersList.getValue(knownScannerId)

        val rotationToApply = nextScannerInfo.rotationFromMiddleScanner

        val tmp = newScannerCoordinatesFromKnownScannerPOV.translateCoordinates(rotationToApply) + nextScannerInfo.coordinatesFromMiddleScanner
        return getAbsoluteCoordinate(tmp, nextScannerInfo.middleScannerId, scannersList)
    }

    private fun findScannerInfo(knownScannerId: Int, beaconsMap: Map<Coordinates, Coordinates>): ScannerPosition {
        val newScannerInfo = beaconsMap
            .entries
            .map { rotations.map { r -> Pair(it.key - it.value.translateCoordinates(r), r) } }
            .reduce { acc, list -> acc.intersect(list).toList() }
            .first()

        return ScannerPosition(newScannerInfo.first, newScannerInfo.second, knownScannerId)
    }
}
