import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun readLines(dayNumber: String): List<String> {
    return Path("src/input/day$dayNumber.txt").readLines()
}

fun readWholeFile(dayNumber: String): String {
    return File("src/input/day$dayNumber.txt").readText()
}

val numbersRegex = "\\d+".toRegex()

data class Point(val x: Int, val y: Int) {
    fun inBoard(array: Array<Array<Char>>): Boolean {
        return x >= 0 && x <= array.first().size - 1 && y >= 0 && y <= array.size - 1
    }

    fun applyOffset(offset: Point): Point {
        return Point(
            x = this.x + offset.x,
            y = this.y + offset.y
        )
    }
}