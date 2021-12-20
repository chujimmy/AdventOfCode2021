package advent.day.day13

import advent.AdventDay

class AdventDay13 : AdventDay() {
    override fun run() {
        val fileContent = getFileAsText("day13").split("\n\n")

        if (fileContent.size < 2) {
            return
        }

        val xMax = fileContent[0]
            .split("\n")
            .map { it.split(",")[0].toInt() }
            .maxOrNull() ?: 0

        val yMax = fileContent[0]
            .split("\n")
            .map { it.split(",")[1].toInt() }
            .maxOrNull() ?: 0

        val points = fileContent[0]
            .split("\n")
            .map {
                val point = it.split(",")
                Pair(point[0].toInt(), point[1].toInt())
            }
            .toSet()

        val foldingInstructions = fileContent[1]
            .split("\n")
            .map { folding ->
                val fold = folding
                    .substring(11)
                    .split("=")

                Pair(fold[0].first(), fold[1].toInt())
            }

        var paper = Array(yMax + 1) { y ->
            Array(xMax + 1) { x ->
                if (points.contains(Pair(x, y))) '#' else '.'
            }
        }

        foldingInstructions.forEachIndexed { i, instruction ->
            paper = fold(paper, instruction)
            printPaper(i + 1, paper)
        }
    }

    private fun fold(paper: Array<Array<Char>>, foldingInstruction: Pair<Char, Int>): Array<Array<Char>> {
        val foldingPlace = foldingInstruction.second
        when (foldingInstruction.first) {
            'x' -> {
                val left = paper.map { it.copyOfRange(0, foldingPlace) }.toTypedArray()
                val right = paper.map { it.copyOfRange(foldingPlace + 1, it.size) }.toTypedArray()

                return applyFolding(left, right, true)
            }
            'y' -> {
                val top = (0 until foldingPlace).map { i -> paper[i] }.toTypedArray()
                val bottom = (foldingPlace + 1 until paper.size).map { i -> paper[i] }.toTypedArray()

                return applyFolding(top, bottom, false)
            }
            else -> {}
        }
        return emptyArray()
    }

    private fun applyFolding(a: Array<Array<Char>>, b: Array<Array<Char>>, xFolding: Boolean): Array<Array<Char>> {
        if (a.size != b.size || a[0].size != b[0].size) {
            return emptyArray()
        }

        b.flatMapIndexed { y, hLine ->
            hLine.mapIndexed { x, char ->
                if (char == '#') Pair(x, y) else null
            }
        }
            .filterNotNull()
            .forEach { coordinates ->
                val newX = if (xFolding) a[0].size - 1 - coordinates.first else coordinates.first
                val newY = if (xFolding) coordinates.second else a.size - 1 - coordinates.second

                a[newY][newX] = '#'
            }

        return a
    }

    private fun printPaper(i: Int, paper: Array<Array<Char>>) {
        val totalHash = paper.sumOf { hLine -> hLine.count { it == '#' } }
        val paperStr = paper.joinToString("") {
            it.joinToString("") + "\n"
        }
        println("Count '#' (Step $i): $totalHash")
        if (i == 12) {
            println("\nPaper After Step $i \n$paperStr")
        }
    }
}
