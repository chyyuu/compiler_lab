package miniceduapp.bytecodde

class BytecodeTextParser(val bytecodeText: String) {
    fun parse() : List<Instruction> {
        val lines = bytecodeText
                .split('\r', '\n')
                .map { it.trim() }
                .filter { !it.isEmpty() }
                .filter { !it.startsWith("MAX") }
                .toList()

        val result = mutableListOf<Instruction>()

        var currentLine: Int? = null

        val unusedLabels = mutableSetOf<Int>()

        lines.forEach {
            val parts = it.split(' ')
            val op = parts.first()
            when (op) {
                "LINENUMBER" -> {
                    currentLine = parts[1].toInt()
                    unusedLabels.add(parseLabelId(parts[2]))
                }
                else -> {
                    if (op[0] == 'L' && op[1].isDigit()) {
                        result.add(Label(parseLabelId(op), currentLine))
                    } else if (op.startsWith("GOTO") || op.startsWith("IF")) {
                        result.add(JumpInstruction(op, Label(parseLabelId(parts[1])), currentLine))
                    } else {
                        val args = if (parts.size == 1) null else parts.subList(1, parts.size).joinToString(" ")
                        result.add(SimpleInstruction(op, args, currentLine))
                    }
                }
            }
        }

        result.removeIf { it is Label && unusedLabels.contains(it.id) }

        adjustLabelIds(result)

        return result
    }

    private fun parseLabelId(text: String): Int {
        return text.trim().substring(1).toInt()
    }

    fun adjustLabelIds(instructions: MutableList<Instruction>) {
        val labels = instructions
                .filter { it is Label }
                .sortedBy { (it as Label).id }
        var nextLabelId = 0
        labels.forEach {
            val currentId = (it as Label).id
            instructions.replaceAll {
                when (it) {
                    is Label -> if (it.id == currentId) Label(nextLabelId, it.line) else it
                    is JumpInstruction -> if (it.label.id == currentId) JumpInstruction(it.name, Label(nextLabelId), it.line) else it
                    else -> it
                }
            }
            nextLabelId++
        }

    }
}
