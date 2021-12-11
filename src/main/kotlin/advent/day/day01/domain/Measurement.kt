package advent.day.day01.domain

class Measurement {
    var measurements: List<Int> = ArrayList()

    fun isComplete(): Boolean {
        return this.measurements.size >= 3
    }

    fun sum(): Int {
        return measurements.sum()
    }

    fun add(measurement: Int) {
        this.measurements += measurement
    }
}
