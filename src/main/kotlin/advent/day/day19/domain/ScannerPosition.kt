package advent.day.day19.domain

class ScannerPosition(
    val coordinatesFromMiddleScanner: Coordinates,
    val rotationFromMiddleScanner: Rotation,
    val middleScannerId: Int,
) {
    override fun toString(): String {
        return "(($coordinatesFromMiddleScanner), ($rotationFromMiddleScanner), $middleScannerId)"
    }
}


//class ScannerPosition(
//    val coordinatesToBaseScanner: Coordinates,
//    val rotationFromMiddleScanner: Rotation,
//    val middleScannerId: Int,
//) {
//    override fun toString(): String {
//        return "(($coordinatesToBaseScanner), ($rotationFromMiddleScanner), $middleScannerId)"
//    }
//}
