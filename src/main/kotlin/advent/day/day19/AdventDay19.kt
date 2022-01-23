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
        scannersList[0].position = ScannerPosition(Coordinates(0.0, 0.0, 0.0), rotations[0], 0)

        while (scannersList.any { it.position == null } ) {
            for (scannerPair in scannersList.combinationList()) {
                if (scannerPair.mapNotNull { it.position }.size != 1) {
                    continue
                }

                val knownScannerId = scannerPair.filter { it.position != null }.map { it.id }.intersect(scannerPair.map { it.id }).first()
                val knownScanner = scannersList[knownScannerId]

                val newScannerId = scannersList.filter { it.position == null }.map { it.id }.intersect(scannerPair.map { it.id }).first()
                val newScanner = scannersList[newScannerId]

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

                newScanner.position = findScannerInfo(knownScannerId, scannersList, beaconsMap)
            }
        }

        println("scannerPositions \n${scannersList.joinToString("\n")}\n---------\n")

        val scannersAbsoluteCoordinates = scannersList
            .filter {it.position != null }
            .map {
                val aa = it.position!!
                getScannerAbsoluteCoordinate(aa.coordinatesFromMiddleScanner, aa.middleScannerId, scannersList)
        }

        println("aaaa ${scannersAbsoluteCoordinates.joinToString("\n") }")
    }

    private fun getScannerAbsoluteCoordinate(
        newScannerCoordinatesFromKnownScannerPOV: Coordinates,
        knownScannerId: Int,
        scannersList: List<Scanner>,
    ): Coordinates {
        println("newScannerCoordinatesFromKnownScannerPOV $newScannerCoordinatesFromKnownScannerPOV")
        if (knownScannerId == 0) {
            return newScannerCoordinatesFromKnownScannerPOV
        }

        val nextScannerInfo = scannersList[knownScannerId].position ?: return Coordinates(0.0, 0.0, 0.0)

        val rotationToApply = nextScannerInfo.rotationFromMiddleScanner
//        val newMiddle = scannersList[middleScannerId]!!.position!!.middleScannerId
//        val newRotation = scannersList[newMiddle]!!.position!!.rotationFromMiddleScanner

        val tmp = newScannerCoordinatesFromKnownScannerPOV.translateCoordinates(rotationToApply) + nextScannerInfo.coordinatesFromMiddleScanner
        println("aaaa $tmp")
//        return nextScannerInfo.coordinatesFromMiddleScanner + aa(tmp, nextScannerInfo.middleScannerId, scannersList)
        return getScannerAbsoluteCoordinate(tmp, nextScannerInfo.middleScannerId, scannersList)

//        val tmp = baseScannerInfo.first + challengerScannerInfo.first.translateCoordinates(baseScannerInfo.second)
//        return coordinates + aa(coordinates, middleScannerId)

    }

    private fun findScannerInfo(
        knownScannerId: Int,
        scannersList: List<Scanner>,
        beaconsMap: Map<Coordinates, Coordinates>,
    ): ScannerPosition {
        val knownScanner = scannersList[knownScannerId]
        val newScannerInfo  = beaconsMap
            .entries
            .map { rotations.map { r -> Pair(it.key - it.value.translateCoordinates(r), r) } }
            .reduce { acc, list -> acc.intersect(list).toList() }
            .first()

        val newScannerCoordinatesFromKnownScannerPOV = newScannerInfo.first
        println("newScannerCoordinatesFromKnownScannerPOV $newScannerCoordinatesFromKnownScannerPOV")


//        val initialRotationFromKnownScanner = knownScanner.position!!
//        println("initialRotationFromKnownScanner,$initialRotationFromKnownScanner")

//        var bababa = newScannerInfo.first.translateCoordinates(knownScanner.position!!.rotationFromMiddleScanner)
//        var tmp: Coordinates = knownScanner.position!!.coordinatesToBaseScanner - bababa
//        if (initialRotationFromKnownScanner.middleScannerId != 0) {
//            tmp = knownScanner.position!!.coordinatesToBaseScanner - (newScannerInfo.first.translateCoordinates(initialRotationFromKnownScanner.rotationFromMiddleScanner))
//        } else {
//            tmp = knownScanner.position!!.coordinatesToBaseScanner + bababa
//
//        }



//        if (initialRotationFromKnownScanner.middleScannerId != 0) {
//            tmp = tmp.translateCoordinates(initialRotationFromKnownScanner.rotationFromMiddleScanner)
//        }


//        println("New Scanner coordinates from base scanner?? $tmp")
//        println("tmptmptmptmp $tmp")
//        println("Info for challenger scanner $newScannerInfo")
//        println("Base Scanner info: ${scannersList[middleScannerId].position!!.coordinatesToBaseScanner} ${scannersList[middleScannerId].position!!.rotationFromMiddleScanner}")
//        println("Challenger Scanner Absolute Coordinates: $tmp ${newScannerInfo.second}")
        return ScannerPosition(newScannerInfo.first, newScannerInfo.second, knownScannerId)
    }
}
