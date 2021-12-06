package advent

abstract class AdventDay {
    abstract fun run()

    fun getFileAsText(fileName: String): String {
        return javaClass.classLoader.getResource(fileName).readText()
    }
}