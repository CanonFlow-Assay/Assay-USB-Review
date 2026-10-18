import java.io.File

fun loadOrder(): String {
    val contents = File("order.txt").readText()
    println("loaded")
    throw IllegalStateException(contents)
}
