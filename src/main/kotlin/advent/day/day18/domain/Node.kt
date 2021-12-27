package advent.day.day18.domain

import java.lang.StringBuilder

class Node(
    var parent: Node? = null,
    var left: Node? = null,
    var right: Node? = null,
    var value: Int? = null
) {

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
            }

            nodesToExplode = getNodesToExplode().toMutableList()
            notesToSplit = getNodesToSplit().toMutableList()
        }

        return this
    }

    private fun getNodesToExplode(): List<Node> {
        if (value != null) {
            return emptyList()
        }

        if (getHeight() == 4) {
            return listOf(this)
        }

        val nodesToReduce = mutableListOf<Node>()
        nodesToReduce.addAll(if (left?.value == null) left!!.getNodesToExplode() else emptyList())
        nodesToReduce.addAll(if (right?.value == null) right!!.getNodesToExplode() else emptyList())

        return nodesToReduce
    }

    private fun explodeNode(): Node {
        val parentNode = parent!!
        val replacementNode = Node(parent = parentNode, value = 0)

        mergeUp(this.left!!.value!!, parentNode, this, true)
        mergeUp(this.right!!.value!!, parentNode, this, false)

        if (parentNode.left == this) {
            parentNode.left = replacementNode
        } else {
            parentNode.right = replacementNode
        }

        return replacementNode
    }

    private fun mergeUp(valueToMerge: Int, currentNode: Node?, previousNode: Node, useLeft: Boolean): Node? {
        if (currentNode == null) {
            return null
        }

        val childrenToCheck = if (useLeft && currentNode.left != previousNode) {
            currentNode.left
        } else if (!useLeft && currentNode.right != previousNode) {
            currentNode.right
        } else {
            null
        }

        return mergeDown(valueToMerge, childrenToCheck, useLeft) ?: mergeUp(valueToMerge, currentNode.parent, currentNode, useLeft)
    }

    private fun mergeDown(valueToMerge: Int, currentNode: Node?, useLeft: Boolean): Node? {
        if (currentNode == null) {
            return null
        }

        if (currentNode.value != null) {
            currentNode.value = currentNode.value!! + valueToMerge
            return currentNode
        }

        val firstChildrenToCheck = if (useLeft) currentNode.right else currentNode.left
        val secondChildrenToCheck = if (useLeft) currentNode.left else currentNode.right

        return mergeDown(valueToMerge, firstChildrenToCheck, useLeft) ?: mergeDown(valueToMerge, secondChildrenToCheck, useLeft)
    }

    private fun getNodesToSplit(): List<Node> {
        if (value != null && value!! > 9) {
            return listOf(this)
        }

        val nodesToSplit = mutableListOf<Node>()
        nodesToSplit.addAll(if (left != null) left!!.getNodesToSplit() else emptyList())
        nodesToSplit.addAll(if (right != null) right!!.getNodesToSplit() else emptyList())

        return nodesToSplit
    }

    private fun splitNode(): Node? {
        if (value == null) {
            return null
        }

        this.left = Node(this, value = this.value!! / 2)
        this.right = Node(this, value = (this.value!! + 1) / 2)
        this.value = null

        return this
    }

    private fun getHeight(): Int {
        return if (parent == null) 0 else 1 + parent!!.getHeight()
    }

    fun magnitude(): Int {
        return value ?: 3 * left!!.magnitude() + 2 * right!!.magnitude()
    }

    override fun toString(): String {
        if (value != null) {
            return value.toString()
        }

        return StringBuilder("[${left!!},${right!!}]").toString()
    }

    companion object {
        fun fromString(parent: Node?, nodeStr: String): Node {
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

            val node = Node(parent)
            if (leftRightSeparatorIndex != null) {
                node.left = fromString(node, nodeContent.substring(0, leftRightSeparatorIndex))
                node.right = fromString(node, nodeContent.substring(leftRightSeparatorIndex + 1))
            } else {
                node.value = Integer.parseInt(nodeContent.removePrefix(","))
            }

            return node
        }
    }
}
