package miniceduapp.bytecodde

interface Instruction {
    val line: Int?

    val text: String
}

data class SimpleInstruction(val name: String, val arguments: String? = null, override val line: Int? = null) : Instruction {
    override val text = name + if (arguments == null) "" else " $arguments"
}

data class JumpInstruction(val name: String, val label: Label, override val line: Int? = null) : Instruction {
    override val text = "$name ${label.text}"
}

data class Label(val id: Int, override val line: Int? = null) : Instruction {
    override val text = "L$id"
}
