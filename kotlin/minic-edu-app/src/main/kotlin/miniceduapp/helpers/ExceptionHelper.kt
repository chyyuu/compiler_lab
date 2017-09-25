package miniceduapp.helpers

fun Throwable.messageOrString(): String = message ?: toString()
