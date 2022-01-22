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
        .map { Scanner(it.first, it.second.map { str -> Coordinates.fromString(str)}) }

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
        val scannersInfo = mutableMapOf(
            0 to Pair(Coordinates(0.0, 0.0, 0.0), rotations[0]),
        )

        while (scannersInfo.size != scannersList.size) {
            for (scannerPair in scannersList.combinationList()) {
                if (scannersInfo.keys.intersect(scannerPair.map { it.id }).isEmpty() ) {
                    continue
                }

                val baseScannerIndex = scannerPair.indexOfFirst { scannersInfo.containsKey(it.id) }
                val baseScannerId = scannerPair[baseScannerIndex].id
                val baseScannerBeaconsCombinations = scannerPair[baseScannerIndex].beacons.combination()
                val challengerScannerId = scannerPair[(baseScannerIndex + 1) % 2].id
                val challengerScannerBeaconsCombinations = scannerPair[(baseScannerIndex + 1) % 2].beacons.combination()

                val beaconCombinationPairsWithSameDistance = baseScannerBeaconsCombinations
                    .plus(challengerScannerBeaconsCombinations)
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

                println("Current scanner: ${scannerPair.map { it.id }}")
                val baseScannerInfo = scannersInfo.getValue(baseScannerId)
                val challengerScannerInfo = findScannerInfo(baseScannerInfo, beaconsMap)

                scannersInfo[challengerScannerId] = challengerScannerInfo
                println("scannerPositions ${scannersInfo}\n---------\n")
            }

        }
    }

    private fun findScannerInfo(
        baseScannerInfo: Pair<Coordinates, Rotation>, beaconsMap: Map<Coordinates, Coordinates>
    ): Pair<Coordinates, Rotation> {
        val baseScannerCoordinates = baseScannerInfo.first
        val baseScannerRotation = baseScannerInfo.second
        val challengerScannerInfo  = beaconsMap
            .entries
            .map { rotations.map { r -> Pair(it.key - it.value.translateCoordinates(r), r) } }
            .reduce { acc, list -> acc.intersect(list).toList() }
            .first()

        val tmp = baseScannerInfo.first + challengerScannerInfo.first.translateCoordinates(baseScannerInfo.second)
        println("Info for challenger scanner $challengerScannerInfo")
        println("Base Scanner info: ${baseScannerInfo.first} ${baseScannerInfo.second}")
        println("Challenger Scanner Absolute Coordinates: $tmp ${challengerScannerInfo.second}")
        return Pair(tmp, challengerScannerInfo.second)
    }
}
