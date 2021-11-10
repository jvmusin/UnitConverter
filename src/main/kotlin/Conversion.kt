package converter

class NegativeAmountException(type: MeasurementType) : RuntimeException("$type shouldn't be negative.")

interface Conversion {
    val from: Measurement
    val to: Measurement
    val type get() = from.type
    fun convert(amount: Double): Double
    fun selfConversions() = listOf(from.selfConversion, to.selfConversion)
    fun convertFormatted(amount: Double): String {
        if (!type.allowsNegative && amount < 0) throw NegativeAmountException(type)
        return "${from.toString(amount)} is ${to.toString(convert(amount))}"
    }
}

data class LinearConversion(
    override val from: Measurement,
    override val to: Measurement,
    val coefficient: Double
) : Conversion {
    fun inverse() = LinearConversion(to, from, 1 / coefficient)
    override fun convert(amount: Double) = amount * coefficient
}

data class FunctionalConversion(
    override val from: Measurement,
    override val to: Measurement,
    val convert: (Double) -> Double
) : Conversion {
    override fun convert(amount: Double) = convert.invoke(amount)
}