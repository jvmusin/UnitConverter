package converter

data class ConvertRequest(val from: String, val to: String, val amount: Double)
class ParseError : RuntimeException("Parse error")

fun String.toConvertRequest(): ConvertRequest {
    val rx = "(?<amount>\\S+) (?<from>(degrees? |)\\S+) \\S+ (?<to>(degrees? |)\\S+)".toRegex()
    val m = rx.matchEntire(lowercase()) ?: throw ParseError()
    m.next()
    val amount = m.groups["amount"]!!.value.toDoubleOrNull() ?: throw ParseError()
    val from = m.groups["from"]!!.value
    val to = m.groups["to"]!!.value
    return ConvertRequest(from, to, amount)
}

fun Converter.process(input: String): String {
    return try {
        val (from, to, amount) = input.toConvertRequest()
        findConversion(from, to).convertFormatted(amount)
    } catch (e: RuntimeException) {
        e.message!!
    }
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!
        if (input == "exit") return
        println(DefaultConverter.process(input))
        println()
    }
}
