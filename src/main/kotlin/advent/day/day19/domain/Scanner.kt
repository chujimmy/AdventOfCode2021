package advent.day.day19.domain

class Scanner(
    val id: Int,
    val beacons: List<Coordinates>,
    var position: ScannerPosition? = null,
) {
    override fun toString(): String {
        return "$id, position=$position"
    }
}

