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