package advent.day.day15

import advent.AdventDay
import advent.day.day15.domain.Node
import java.util.*

class AdventDay15 : AdventDay() {
    private val fileText = getFileAsText("day15")

    private val riskMapList = fileText
        .split("\n")
        .map { it.toCharArray().map { d -> d.digitToInt() } }

    private val adjacentPointsOffsets = listOf(
        Pair(-1, 0),
        Pair(1, 0),
        Pair(0, -1),
        Pair(0, 1),
    )

    override fun run() {
        val resultPart01 = findPathDijkstra(duplicateRiskMap(riskMapList, 1))
        println("Part 01 - Least risky path risk: $resultPart01")

        val resultPart02 = findPathDijkstra(duplicateRiskMap(riskMapList, 5))
        println("Part 02 - Least risky path risk: $resultPart02")
    }

    private fun findPathDijkstra(riskMap: Map<Node, Int>): Int {
        val dijkstraMap = riskMap
            .mapValues { if (it.key == Node(0, 0)) 0 else Int.MAX_VALUE }
            .toMutableMap()
        val settledNodes = mutableSetOf<Node>()
        val unsettledNodes = PriorityQueue(compareBy<Pair<Node, Int>> { it.second })

        unsettledNodes.add(Pair(Node(0, 0), 0))

        while (unsettledNodes.isNotEmpty()) {
            val currentNode = unsettledNodes.remove().first

            adjacentPointsOffsets
                .map { Node(currentNode.x + it.first, currentNode.y + it.second) }
                .filter { dijkstraMap.containsKey(it) }
                .filterNot { settledNodes.contains(it) }
                .forEach { adjacentNode ->
                    val riskToAdjacentFromCurrent = dijkstraMap.getValue(currentNode) + riskMap.getValue(adjacentNode)
                    val previousRiskToAdjacent = dijkstraMap.getValue(adjacentNode)
                    if (riskToAdjacentFromCurrent < previousRiskToAdjacent) {
                        dijkstraMap[adjacentNode] = riskToAdjacentFromCurrent
                        unsettledNodes.add(Pair(adjacentNode, riskToAdjacentFromCurrent))
                    }
                }
            settledNodes.add(currentNode)
        }

        return dijkstraMap.getValue(Node(riskMap.maxOf { it.key.x }, riskMap.maxOf { it.key.y }))
    }

    private fun duplicateRiskMap(baseRiskMap: List<List<Int>>, replicationFactor: Int): Map<Node, Int> {
        val subMapsToPlace = (0..8).associateWith { i ->
            baseRiskMap.map { hLine ->
                hLine.map { risk ->
                    if (risk + i > 9) (risk + i - 9) else risk + i
                }
            }
        }

        return (0 until replicationFactor * baseRiskMap[0].size).flatMap { y ->
            (0 until replicationFactor * baseRiskMap.size).map { x ->
                val yRepetitionOffset = y / baseRiskMap[0].size
                val xRepetitionOffset = x / baseRiskMap.size

                val repetitionOffset = (yRepetitionOffset + xRepetitionOffset) % 9

                val subY = y % baseRiskMap[0].size
                val subX = x % baseRiskMap.size

                Pair(Node(x, y), subMapsToPlace.getValue(repetitionOffset)[subY][subX])
            }
        }.associate { it.first to it.second }
    }
}
