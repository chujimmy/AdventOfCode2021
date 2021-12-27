package advent.day.day18

import advent.AdventDay
import advent.day.day18.domain.Node

class AdventDay18 : AdventDay() {
    private val fileContent = getFileAsText("day18")
    private val numbers = fileContent.split("\n")

    override fun run() {
        val trees = numbers.map { Node.fromString(null, it) }

        val totalSum = trees.reduce { acc: Node, tree ->
            val node = Node(null, acc, tree)

            acc.parent = node
            tree.parent = node

            node.reduce()
        }

        println("Magnitude: ${totalSum.magnitude()}")

        val maxMagnitude = numbers
            .flatMapIndexed { i, node1 ->
                numbers.subList(i + 1, numbers.size).flatMapIndexed { j, node2 ->
                    mutableListOf(
                        Pair(Node.fromString(null, node1), Node.fromString(null, node2)),
                        Pair(Node.fromString(null, node2), Node.fromString(null, node1)),
                    )
                }
            }.map {
                val first = it.first
                val second = it.second
                val node = Node(null, first, second)

                first.parent = node
                second.parent = node

                node.reduce()
            }
            .map { it.magnitude() }
            .maxOf { it }

        println("Largest magnitude of any sum of two different number: $maxMagnitude")
    }
}
