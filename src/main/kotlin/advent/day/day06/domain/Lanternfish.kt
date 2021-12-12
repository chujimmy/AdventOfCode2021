package advent.day.day06.domain

class Lanternfish(var timer: Int) {

    fun updateTimer(): Lanternfish? {
        if (this.timer == 0) {
            this.timer = 6
            return Lanternfish(8)
        }

        this.timer--

        return null
    }

    override fun toString(): String {
        return this.timer.toString()
    }
}
