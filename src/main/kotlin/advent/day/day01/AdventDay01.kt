package advent.day.day01

import advent.AdventDay
import advent.day.day01.domain.Measurement
import java.util.*

class AdventDay01: AdventDay() {
    override fun run() {
        val fileText = getFileAsText("day01")
        val measurementsValues: Array<Int> = fileText.split("\n").map{ it.toInt()}.toTypedArray()

        val queue: Queue<Measurement> = LinkedList()
        var queueIterator: MutableIterator<Measurement>

        var countIncreasedMeasurements = 0
        var previousMeasurement: Measurement? = null

        for (measurementValue in measurementsValues) {
            val measurementToCheck = queue.peek()
            if (measurementToCheck != null && measurementToCheck.isComplete()) {
                queue.poll()
            }

            queue.add(Measurement())
            queueIterator = queue.iterator()
            queueIterator.forEachRemaining { it.add(measurementValue) }

            if (hasMeasurementValueIncreased(previousMeasurement, measurementToCheck)) {
                countIncreasedMeasurements++
            }

            previousMeasurement = measurementToCheck
        }

        queueIterator = queue.iterator()
        queueIterator.forEachRemaining {
            if (hasMeasurementValueIncreased(previousMeasurement, it)) {
                countIncreasedMeasurements++
            }
            previousMeasurement = it
        }

        println("Number of increase: $countIncreasedMeasurements")
    }

    private fun hasMeasurementValueIncreased(
        previousMeasurement: Measurement?,
        measurementToCheck: Measurement?,
    ): Boolean {
        if (previousMeasurement == null || measurementToCheck == null) {
            return false
        }

        return measurementToCheck.isComplete() && previousMeasurement.isComplete()
            && measurementToCheck.sum() > previousMeasurement.sum()
    }
}