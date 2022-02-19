package advent.day.day13

import advent.AdventDay

class AdventDay13 : AdventDay() {
    private val fileContent = getFileAsText("day13").split("\n\n")

    private val points = fileContent[0]
        .split("\n")
        .map { it.split(",") }
        .map { point -> Pair(point[0].toInt(), point[1].toInt()) }
        .toSet()

    private val foldingInstructions = fileContent[1]
        .split("\n")
        .map { it.substring(11).split("=") }
        .map { fold -> Pair(fold[0].first(), fold[1].toInt()) }

    override fun run() {
        val paper = Array(points.maxOf { it.second } + 1) { y ->
            Array(points.maxOf { it.first } + 1) { x ->
                if (points.contains(Pair(x, y))) '#' else '.'
            }
        }

        foldingInstructions
            .fold(paper) { acc, instruction -> fold(acc, instruction) }
    }

    private fun fold(paper: Array<Array<Char>>, foldingInstruction: Pair<Char, Int>): Array<Array<Char>> {
        val foldingPosition = foldingInstruction.second
        val triple = if (foldingInstruction.first == 'x') {
            val left = paper.map { it.copyOfRange(0, foldingPosition) }.toTypedArray()
            val right = paper.map { it.copyOfRange(foldingPosition + 1, it.size) }.toTypedArray()
            Triple(left, right, true)
        } else {
            val top = (0 until foldingPosition).map { i -> paper[i] }.toTypedArray()
            val bottom = (foldingPosition + 1 until paper.size).map { i -> paper[i] }.toTypedArray()
            Triple(top, bottom, false)
        }

        return applyFolding(triple.first, triple.second, triple.third)
    }

    private fun applyFolding(first: Array<Array<Char>>, second: Array<Array<Char>>, xFolding: Boolean): Array<Array<Char>> {
        if (first.size != second.size || first[0].size != second[0].size) {
            return emptyArray()
        }

        second
            .flatMapIndexed { y, hLine ->
                hLine.mapIndexed { x, char -> if (char == '#') Pair(x, y) else null }
            }
            .filterNotNull()
            .forEach { coordinates ->
                val newX = if (xFolding) first[0].size - 1 - coordinates.first else coordinates.first
                val newY = if (xFolding) coordinates.second else first.size - 1 - coordinates.second

                first[newY][newX] = '#'
            }

        printPaper(first)
        return first
    }

    private fun printPaper(paper: Array<Array<Char>>) {
        println("Count '#': ${paper.sumOf { hLine -> hLine.count { it == '#' } }}")
        println("\nPaper After Step \n${paper.joinToString("\n") { it.joinToString("") }}\n")
    }
}
