package converter

class Converter(private val baseMeasurements: Array<out Measurement>, private val conversions: List<Conversion>) {
    private val units =
        conversions
            .run { map { it.from } + map { it.to } }
            .flatMap { it.names.map { name -> name.lowercase() to it } }
            .toMap()

    fun findConversion(from: String, to: String): Conversion {
        val fromUnit = from.lowercase().let { units[it] }
        val toUnit = to.lowercase().let { units[it] }
        if (fromUnit != null && toUnit != null) {
            for (base in baseMeasurements) {
                val toBase = conversions.firstOrNull { it.from == fromUnit && it.to == base }
                val fromBase = conversions.firstOrNull { it.from == base && it.to == toUnit }
                if (toBase != null && fromBase != null)
                    return FunctionalConversion(fromUnit, toUnit) { amount -> fromBase.convert(toBase.convert(amount)) }
            }
        }
        throw ConversionImpossibleException(fromUnit, toUnit)
    }
}

val DefaultConverter = run {
    val meters = Measurement(MeasurementType.Length, "meter", "meters", "m")
    val kilometers = Measurement(MeasurementType.Length, "kilometer", "kilometers", "km")
    val centimeters = Measurement(MeasurementType.Length, "centimeter", "centimeters", "cm")
    val millimeters = Measurement(MeasurementType.Length, "millimeter", "millimeters", "mm")
    val miles = Measurement(MeasurementType.Length, "mile", "miles", "mi")
    val yards = Measurement(MeasurementType.Length, "yard", "yards", "yd")
    val feet = Measurement(MeasurementType.Length, "foot", "feet", "ft")
    val inches = Measurement(MeasurementType.Length, "inch", "inches", "in")

    val grams = Measurement(MeasurementType.Weight, "gram", "grams", "g")
    val kilograms = Measurement(MeasurementType.Weight, "kilogram", "kilograms", "kg")
    val milligrams = Measurement(MeasurementType.Weight, "milligram", "milligrams", "mg")
    val pounds = Measurement(MeasurementType.Weight, "pound", "pounds", "lb")
    val ounces = Measurement(MeasurementType.Weight, "ounce", "ounces", "oz")

    val celsius = Measurement(MeasurementType.Temperature, "degree Celsius", "degrees Celsius", "celsius", "dc", "c")
    val fahrenheit =
        Measurement(MeasurementType.Temperature, "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f")
    val kelvins = Measurement(MeasurementType.Temperature, "kelvin", "kelvins", "k")

    buildConverter(meters, grams, celsius) {
        +LinearConversion(kilometers, meters, 1000.0)
        +LinearConversion(centimeters, meters, 0.01)
        +LinearConversion(millimeters, meters, 0.001)
        +LinearConversion(miles, meters, 1609.35)
        +LinearConversion(yards, meters, 0.9144)
        +LinearConversion(feet, meters, 0.3048)
        +LinearConversion(inches, meters, 0.0254)

        +LinearConversion(kilograms, grams, 1000.0)
        +LinearConversion(milligrams, grams, 0.001)
        +LinearConversion(pounds, grams, 453.592)
        +LinearConversion(ounces, grams, 28.3495)

        +FunctionalConversion(celsius, fahrenheit) { c -> c * 9 / 5 + 32 }
        +FunctionalConversion(fahrenheit, celsius) { f -> (f - 32) * 5 / 9 }

        +FunctionalConversion(celsius, kelvins) { c -> c + 273.15 }
        +FunctionalConversion(kelvins, celsius) { k -> k - 273.15 }
    }
}