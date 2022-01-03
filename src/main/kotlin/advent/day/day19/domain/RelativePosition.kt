package advent.day.day19.domain

data class RelativePosition(
    val coordFromKnownScanner: Coordinates,
    val rotationFromKnownScanner: Rotation,
    val knownScannerId: Int,
)
