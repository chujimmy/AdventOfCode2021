package advent.day.day20

import advent.AdventDay

class AdventDay20 : AdventDay() {
    private val fileText = getFileAsText("day20")
    private val imageEnhancementAlgorithm = fileText.split("\n\n")[0]
    private val initialImage = fileText.split("\n\n")[1]
        .split("\n")
        .map { it.toCharArray().toList() }

    override fun run() {

        val finalImage = (1..50).fold(initialImage) { acc, step ->
            val imageWithPaddedSpace = MutableList(acc[0].size + 2) { y ->
                MutableList(acc.size + 2) { x ->
                    if (x < 1 || x > acc.size || y < 1 || y > acc[0].size) {
                        '.'
                    } else {
                        acc[y - 1][x - 1]
                    }
                }
            }

            (0..imageWithPaddedSpace.lastIndex).forEach { middlePixelY ->
                (0..imageWithPaddedSpace[0].lastIndex).forEach { middlePixelX ->
                    val str = (middlePixelY - 1..middlePixelY + 1)
                        .joinToString("") { y ->
                            (middlePixelX - 1..middlePixelX + 1).joinToString("") { x ->
                                getImageChar(step, x - 1, y - 1, acc, imageEnhancementAlgorithm).toString()
                            }
                        }

                    val intValue = str
                        .replace("#", "1")
                        .replace(".", "0")
                        .toInt(2)

                    imageWithPaddedSpace[middlePixelY][middlePixelX] = imageEnhancementAlgorithm[intValue]
                }
            }
            val imageStr = imageWithPaddedSpace.joinToString("\n") { it.joinToString("") }
            println("Step $step:\n$imageStr\n ")

            imageWithPaddedSpace
        }

        println("Total lit pixels ${finalImage.sumOf { line -> line.count { c -> c == '#' } }}")
    }

    private fun getImageChar(step: Int, x: Int, y: Int, image: List<List<Char>>, imageEnhancementAlgorithm: String): Char {
        return if (x < 0 || x > image[0].lastIndex || y < 0 || y > image.lastIndex) {
            if (step % 2 == 0) imageEnhancementAlgorithm[0] else '.'
        } else {
            image[y][x]
        }
    }
}
