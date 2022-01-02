package advent.day.day18.domain

class ValueNode(
    parent: LeftRightNode?,
    var value: Int
) : Node(parent) {
    override fun getNodesToSplit(): List<Node> {
        return if (value > 9) listOf(this) else emptyList()
    }

    override fun splitNode(): Node? {
        if (value < 9) {
            return null
        }

        val parent = this.parent!!
        val newLeftNode = ValueNode(null, this.value / 2)
        val newRightNode = ValueNode(null, (this.value + 1) / 2)
        val newNode = LeftRightNode(parent, newLeftNode, newRightNode)

        if (parent.left == this) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }

        newLeftNode.parent = newNode
        newRightNode.parent = newNode

        return newNode
    }

    override fun magnitude(): Int {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}
