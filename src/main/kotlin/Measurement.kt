package converter

data class Measurement(
    val type: MeasurementType,
    val singularName: String,
    val pluralName: String,
    val additionalNames: List<String> = emptyList()
) {
    val names get() = listOf(singularName, pluralName) + additionalNames
    val selfConversion get() = LinearConversion(this, this, 1.0)
    fun toString(amount: Double) = "$amount ${if (amount == 1.0) singularName else pluralName}"
}

enum class MeasurementType(val allowsNegative: Boolean) {
    Length(false),
    Weight(false),
    Temperature(true)
}

fun Measurement(
    type: MeasurementType,
    fullSingularName: String,
    fullPluralName: String,
    vararg additionalNames: String
) = Measurement(type, fullSingularName, fullPluralName, additionalNames.toList())

class ConversionImpossibleException(from: Measurement?, to: Measurement?) :
    RuntimeException("Conversion from ${from.getName()} to ${to.getName()} is impossible") {

    companion object {
        fun Measurement?.getName() = this?.pluralName ?: "???"
    }

    override val message get() = super.message!!
}