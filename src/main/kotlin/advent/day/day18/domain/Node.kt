package advent.day.day18.domain

abstract class Node(var parent: LeftRightNode?) {
    open fun getNodesToExplode(): List<Node> { return emptyList() }
    open fun explodeNode(): Node? { return null }
    open fun getNodesToSplit(): List<Node> { return emptyList() }
    open fun splitNode(): Node? { return null }
    abstract fun magnitude(): Int

    protected fun getHeight(): Int {
        return if (parent == null) 0 else 1 + parent!!.getHeight()
    }

    fun reduce(): Node {
        var nodesToExplode = getNodesToExplode().toMutableList()
        var notesToSplit = getNodesToSplit().toMutableList()

        while (nodesToExplode.isNotEmpty() || notesToSplit.isNotEmpty()) {
            while (nodesToExplode.isNotEmpty()) {
                nodesToExplode.removeFirst().explodeNode()
            }

            notesToSplit = getNodesToSplit().toMutableList()
            while (notesToSplit.isNotEmpty()) {
                val splitNode = notesToSplit.removeFirst().splitNode()
                if (splitNode != null && splitNode.getHeight() >= 4) {
                    break
                }
                notesToSplit = getNodesToSplit().toMutableList()
            }

            nodesToExplode = getNodesToExplode().toMutableList()
            notesToSplit = getNodesToSplit().toMutableList()
        }

        return this
    }

    companion object {
        fun fromString(parent: LeftRightNode?, nodeStr: String): Node {
            val nodeContent = nodeStr.removePrefix("[").removeSuffix("]").trim()
            val leftRightSeparatorIndex = nodeContent
                .mapIndexed { i, c -> if (c == ',') i else null }
                .filterNotNull()
                .find { commaIndex ->
                    val strBeforeComma = nodeContent.substring(0, commaIndex)
                    val strAfterComma = nodeContent.substring(commaIndex + 1)

                    strBeforeComma.count { it == '[' } - strBeforeComma.count { it == ']' } == 0 &&
                        strAfterComma.count { it == '[' } - strAfterComma.count { it == ']' } == 0
                }

            return if (leftRightSeparatorIndex != null) {
                LeftRightNode.buildLeftRightNode(
                    parent,
                    nodeContent.substring(0, leftRightSeparatorIndex),
                    nodeContent.substring(leftRightSeparatorIndex + 1)
                )
            } else {
                ValueNode(parent, Integer.parseInt(nodeContent.removePrefix(",")))
            }
        }
    }
}
