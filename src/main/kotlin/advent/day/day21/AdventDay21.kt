package advent.day.day21

import advent.AdventDay

class AdventDay21 : AdventDay() {
    private val fileText = getFileAsText("day21")
    private val startingPositions = fileText.split("\n")
        .map { str -> Integer.parseInt(str.substring(28)) }

    override fun run() {
        val dice = (1..100).toList()
        val positionList = (1..10).toList()
        val scores = hashMapOf(
            1 to Pair(startingPositions[0] - 1, 0),
            2 to Pair(startingPositions[1] - 1, 0),
        )

        var rollCount = 0
        while (scores.values.count { it.second >= 1000 } == 0) {
            for (player in 1..2) {
                val rollResult = (rollCount until rollCount + 3).sumOf { dice[(it % dice.size)] }
                val playerInfo = scores.getOrDefault(player, Pair(0, 0))
                val newPosition = (playerInfo.first + rollResult) % 10
                val newScore = (playerInfo.second + positionList[newPosition])

                rollCount += 3
                scores[player] = Pair(newPosition, newScore)

                if (newScore >= 1000) {
                    break
                }
            }
        }

        println("score $scores. Dice rolls $rollCount")
    }
}
