package converter

class ConverterBuilder {
    private val conversions = mutableListOf<Conversion>()
    operator fun LinearConversion.unaryPlus() {
        conversions += this
        conversions += inverse()
    }

    operator fun FunctionalConversion.unaryPlus() {
        conversions += this
    }

    fun build(vararg baseMeasurements: Measurement) = Converter(
        baseMeasurements,
        conversions + conversions.flatMap { it.selfConversions() }
    )
}

inline fun buildConverter(vararg baseMeasurements: Measurement, block: ConverterBuilder.() -> Unit): Converter {
    return ConverterBuilder().run {
        block()
        build(*baseMeasurements)
    }
}