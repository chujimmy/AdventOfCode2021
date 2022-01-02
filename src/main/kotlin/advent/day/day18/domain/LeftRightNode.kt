package advent.day.day18.domain

class LeftRightNode(
    parent: LeftRightNode?,
    var left: Node,
    var right: Node,
) : Node(parent) {
    init {
        left.parent = this
        right.parent = this
    }

    override fun getNodesToExplode(): List<Node> {
        if (getHeight() == 4) {
            return listOf(this)
        }

        return left.getNodesToExplode().plus(right.getNodesToExplode())
    }

    override fun explodeNode(): Node? {
        val parentNode = parent!!

        if (this.left !is ValueNode && this.right !is ValueNode) {
            return null
        }

        mergeUp((left as ValueNode).value, parentNode, this, true)
        mergeUp((right as ValueNode).value, parentNode, this, false)

        val replacementNode = ValueNode(parentNode, 0)
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

        val childrenToCheck = if (useLeft && currentNode is LeftRightNode && currentNode.left != previousNode) {
            currentNode.left
        } else if (!useLeft && currentNode is LeftRightNode && currentNode.right != previousNode) {
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

        if (currentNode is ValueNode) {
            currentNode.value = currentNode.value + valueToMerge
            return currentNode
        }

        if (currentNode is LeftRightNode) {
            val firstChildrenToCheck = if (useLeft) currentNode.right else currentNode.left
            val secondChildrenToCheck = if (useLeft) currentNode.left else currentNode.right
            return mergeDown(valueToMerge, firstChildrenToCheck, useLeft) ?: mergeDown(valueToMerge, secondChildrenToCheck, useLeft)
        }

        return null
    }

    override fun getNodesToSplit(): List<Node> {
        return left.getNodesToSplit().plus(right.getNodesToSplit())
    }

    override fun magnitude(): Int {
        return 3 * left.magnitude() + 2 * right.magnitude()
    }

    override fun toString(): String {
        return "[$left,$right]"
    }

    companion object {
        fun buildLeftRightNode(parent: LeftRightNode?, leftContent: String, rightContent: String): Node {
            return LeftRightNode(parent, fromString(parent, leftContent), fromString(parent, rightContent))
        }
    }
}
