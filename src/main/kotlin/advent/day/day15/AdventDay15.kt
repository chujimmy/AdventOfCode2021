package advent.day.day15

import advent.AdventDay
import advent.day.day15.domain.Node
import java.util.PriorityQueue

class AdventDay15 : AdventDay() {
    private val fileText = getFileAsText("day15")

    private val riskMap = fileText
        .split("\n")
        .map { it.toCharArray().map { d -> d.digitToInt() } }

    private val adjacentPointsOffsets = listOf(
        Pair(-1, 0),
        Pair(1, 0),
        Pair(0, -1),
        Pair(0, 1),
    )

    override fun run() {
        val resultPart01 = findPathDijkstra(riskMap)
        println("Part 01 - Least risky path risk: $resultPart01")

        val resultPart02 = findPathDijkstra(duplicateMap(riskMap, 5))
        println("Part 02 - Least risky path risk : $resultPart02")
    }

    private fun findPathDijkstra(riskMap: List<List<Int>>): Int {
        val startTime = System.currentTimeMillis()
        val startNodeWithRisk = Node(0, 0, 0)
        val endNode = Node(riskMap[0].lastIndex, riskMap.lastIndex)

        val nodesDistances = riskMap
            .flatMapIndexed { y, hLine -> hLine.mapIndexed { x, _ -> Node(x, y) } }
            .associateWith { Node(-1, -1) }
            .toMutableMap()
        nodesDistances[Node(0, 0)] = startNodeWithRisk

        val settledNodes: MutableSet<Node> = mutableSetOf()
        val unsettledNodes = PriorityQueue(compareBy<Node> { it.risk })
        unsettledNodes.add(startNodeWithRisk)

        while (unsettledNodes.isNotEmpty()) {
            val currentNode = unsettledNodes.remove()

            adjacentPointsOffsets
                .map { Node(currentNode.x + it.first, currentNode.y + it.second) }
                .filter { isNodeValid(riskMap, it) }
                .filter { !settledNodes.contains(it) }
                .forEach { adjacentNode ->
                    val riskToAdjacentFromCurrent = nodesDistances[currentNode]!!.risk + riskMap[adjacentNode.y][adjacentNode.x]
                    val previousRiskToAdjacent = nodesDistances[adjacentNode]!!.risk
                    if (riskToAdjacentFromCurrent < previousRiskToAdjacent) {
                        nodesDistances[adjacentNode] = Node(currentNode.x, currentNode.y, riskToAdjacentFromCurrent)
                        unsettledNodes.add(Node(adjacentNode.x, adjacentNode.y, riskToAdjacentFromCurrent))
                    }
                }
            settledNodes.add(currentNode)
        }

        val endTime = System.currentTimeMillis()
        println("Execution time ${(endTime - startTime)}ms")
        return nodesDistances[endNode]!!.risk
    }

    private fun isNodeValid(riskMap: List<List<Int>>, node: Node): Boolean {
        return node.x >= 0 && node.x < riskMap[0].size && node.y >= 0 && node.y < riskMap.size
    }

    private fun duplicateMap(riskMap: List<List<Int>>, replicationFactor: Int): List<List<Int>> {
        val subMapsToPlace: Map<Int, List<List<Int>>> = (0..8).associateWith { i ->
            riskMap.map { hLine ->
                hLine.map { risk ->
                    if (risk + i > 9) (risk + i - 9) else risk + i
                }
            }
        }

        val duplicatedMap = List(replicationFactor * riskMap[0].size) { y ->
            List(replicationFactor * riskMap.size) { x ->
                val yRepetitionOffset = y / riskMap[0].size
                val xRepetitionOffset = x / riskMap.size

                val repetitionOffset = (yRepetitionOffset + xRepetitionOffset) % 9

                val subY = y % riskMap[0].size
                val subX = x % riskMap.size

                subMapsToPlace[repetitionOffset]!![subY][subX]
            }
        }

        return duplicatedMap
    }
}
