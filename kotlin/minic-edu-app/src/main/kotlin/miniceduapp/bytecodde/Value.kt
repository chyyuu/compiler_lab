package miniceduapp.bytecodde

abstract class Value {
    abstract val text: String
    open val size: Int = 1
}

data class IntValue(val value: Int) : Value() {
    override val text: String
        get() = value.toString()
}

data class DoubleValue(val value: Double) : Value() {
    override val text = String.format("%.2f", value)

    override val size = 2
}

data class WideValuePart(override val text: String = "") : Value()

open class ObjectValue(val value: Any?, val name: String = "<${value?.javaClass?.simpleName}>") : Value() {
    override val text = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ObjectValue

        if (value != other.value) return false
        if (name != other.name) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }

    override fun toString() = "ObjectValue(value=$value, name=$name, text='$text')"
}

class StringValue(value: String) : ObjectValue(value, "\"$value\"") {

    override fun toString() = "StringValue(value=$value)"
}

data class EmptyValue(override val text: String = "") : Value()
