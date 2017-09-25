package miniceduapp.bytecodde

import java.lang.StringBuilder
import java.util.*

class BytecodeInterpreter(val bytecode: List<Instruction>,  val onOutput: (String) -> Unit = {}) {

    private val _operandStack = ArrayDeque<Value>()
    private val _variables = mutableListOf<Value?>()

    private var _nextInsruction = 0

    private val _labelInstructionMap = bytecode.filter { it is Label }.map { (it as Label).id to bytecode.indexOf(it) }.toMap()

    private var _isExecuting = false

    val operandStack get() = _operandStack.toList()
    val variables get() = _variables.toList()

    val nextInsruction get() = _nextInsruction

    val isExecuting: Boolean get() = _isExecuting

    init {
        for (i in 1..100) {
            _variables.add(null)
        }
    }

    fun start() {
        _variables.replaceAll { null }
        _variables[0] = ObjectValue(null, "<this>")

        _operandStack.clear()

        _nextInsruction = 0

        _isExecuting = true
    }

    fun stop() {
        _isExecuting = false
    }

    fun executeNextInstruction() {
        assert(isExecuting)

        val instruction = bytecode[_nextInsruction++]
        instruction.execute()
    }

    fun execute() {
        start()
        while (_isExecuting) {
            executeNextInstruction()
        }
    }

    private fun Instruction.execute() {
        when (this) {
            is SimpleInstruction -> {
                when (name) {
                    "LDC" -> {
                        if (arguments!!.startsWith('"')) {
                            pushString(arguments.substring(1, arguments.length - 1))
                        } else {
                            try {
                                pushInt(arguments.toInt())
                            } catch (_: NumberFormatException) {
                                pushDouble(arguments.toDouble())
                            }
                        }
                    }
                    "ASTORE", "DSTORE", "ISTORE" -> {
                        val index = arguments!!.toInt()
                        val size = peek().size
                        for (i in index..index + size - 1) {
                            _variables[i] = pop()
                        }
                    }
                    "ALOAD", "DLOAD", "ILOAD" -> {
                        val index = arguments!!.toInt()
                        val size = _variables[index]!!.size
                        for (i in (index + size - 1) downTo index) {
                            push(_variables[i]!!)
                        }
                    }
                    "RETURN" -> stop()
                    "GETSTATIC" -> {
                        val arg = arguments!!.substringBefore(" : ")
                        val (className, fieldName) = parseMember(arg)
                        pushObject(Class.forName(className).getField(fieldName).get(null))
                    }
                    "INVOKESTATIC" -> {
                        val arg = arguments!!.substringBefore(" ")
                        val (className, methodName) = parseMember(arg)
                        // this and INVOKESTATIC can be executed via reflection to allow any method,
                        // but currently Mini-C uses only print, exit, toString and string comparison/concatenation,
                        // and the first two need special handling anyway to not interfere with the app
                        when (methodName) {
                            "toString" -> {
                                val value = when (peek()) {
                                    is IntValue -> {
                                        if (className.endsWith("Boolean")) {
                                            popInt() != 0
                                        } else {
                                            popInt()
                                        }
                                    }
                                    is DoubleValue -> popDouble()
                                    is ObjectValue -> popObject()
                                    else -> throw UnsupportedOperationException(peek().javaClass.canonicalName)
                                }
                                pushString(value!!.toString())
                            }
                            "exit" -> {
                                pop()
                                stop()
                            }
                            "equals" -> pushInt(if (Objects.equals(popObject(), popObject())) 1 else 0)
                            else -> throw UnsupportedOperationException(methodName)
                        }
                    }
                    "INVOKEVIRTUAL" -> {
                        val arg = arguments!!.substringBefore(" ")
                        val (_, methodName) = parseMember(arg)
                        when (methodName) {
                            "print" -> {
                                onOutput(popString())
                                pop()
                            }
                            "println" -> {
                                onOutput(popString() + "\n")
                                pop()
                            }
                            "append" -> {
                                val value = popString()
                                pushObject((popObject() as StringBuilder).append(value))
                            }
                            "toString" -> {
                                pushString(popObject().toString())
                            }
                            else -> throw UnsupportedOperationException(methodName)
                        }
                    }
                    "INVOKESPECIAL" -> {
                        val arg = arguments!!.substringBefore(" ")
                        val (className, _) = parseMember(arg)
                        when (className) {
                            "java.lang.StringBuilder" -> pop()
                            else -> throw UnsupportedOperationException(className)
                        }
                    }
                    "NEW" -> {
                        val className = arguments!!
                        when (className) {
                            "java/lang/StringBuilder" -> pushObject(StringBuilder())
                            else -> throw UnsupportedOperationException(className)
                        }
                    }
                    "DUP" -> {
                        push(peek())
                    }
                    "I2D" -> pushDouble(popInt().toDouble())
                    "DNEG" -> pushDouble(-popDouble())
                    "DADD" -> pushDouble(popDouble() + popDouble())
                    "DSUB" -> {
                        val num1 = popDouble()
                        val num2 = popDouble()
                        pushDouble(num2 - num1)
                    }
                    "DMUL" -> pushDouble(popDouble() * popDouble())
                    "DDIV" -> {
                        val num1 = popDouble()
                        val num2 = popDouble()
                        pushDouble(num2 / num1)
                    }
                    "DREM" -> {
                        val num1 = popDouble()
                        val num2 = popDouble()
                        pushDouble(num2 % num1)
                    }
                    "DCMPL", "DCMPG" -> {
                        val num1 = popDouble()
                        val num2 = popDouble()
                        pushInt(num2.compareTo(num1))
                    }
                    "IADD" -> pushInt(popInt() + popInt())
                    "ISUB" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        pushInt(num2 - num1)
                    }
                    "IMUL" -> pushInt(popInt() * popInt())
                    "IDIV" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        pushInt(num2 / num1)
                    }
                    "IREM" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        pushInt(num2 % num1)
                    }
                    "INEG" -> pushInt(-popInt())
                    "ICONST_0" -> pushInt(0)
                    "ICONST_1" -> pushInt(1)
                    else -> throw UnsupportedOperationException(name)
                }
            }
            is JumpInstruction -> {
                when (name) {
                    "GOTO" -> jumpTo(label)
                    "IFEQ" -> {
                        if (popInt() == 0) {
                            jumpTo(label)
                        }
                    }
                    "IFNE" -> {
                        if (popInt() != 0) {
                            jumpTo(label)
                        }
                    }
                    "IFGE" -> {
                        if (popInt() >= 0) {
                            jumpTo(label)
                        }
                    }
                    "IFLE" -> {
                        if (popInt() <= 0) {
                            jumpTo(label)
                        }
                    }
                    "IFGT" -> {
                        if (popInt() > 0) {
                            jumpTo(label)
                        }
                    }
                    "IFLT" -> {
                        if (popInt() < 0) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPNE" -> {
                        if (popInt() != popInt()) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPEQ" -> {
                        if (popInt() == popInt()) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPGE" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        if (num2 >= num1) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPLE" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        if (num2 <= num1) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPGT" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        if (num2 > num1) {
                            jumpTo(label)
                        }
                    }
                    "IF_ICMPLT" -> {
                        val num1 = popInt()
                        val num2 = popInt()
                        if (num2 < num1) {
                            jumpTo(label)
                        }
                    }
                    else -> throw UnsupportedOperationException(name)
                }
            }
            is Label -> {
                // do nothing
            }
            else -> throw UnsupportedOperationException(javaClass.canonicalName)
        }
    }

    private fun push(value: Value) {
        _operandStack.push(value)
    }

    private fun pop(): Value {
        return _operandStack.pop()
    }

    private fun peek(): Value {
        return _operandStack.peek()!!
    }

    private fun pushInt(value: Int) {
        push(IntValue(value))
    }

    private fun pushDouble(value: Double) {
        push(WideValuePart())
        push(DoubleValue(value))
    }

    private fun pushObject(value: Any?) {
        if (value is String) {
            pushString(value)
        } else {
            push(ObjectValue(value))
        }
    }

    private fun pushString(value: String) {
        push(StringValue(value))
    }

    private fun popInt(): Int {
        return (pop() as IntValue).value
    }

    private fun popDouble(): Double {
        val value = (pop() as DoubleValue).value
        pop()
        return value
    }

    private fun popObject(): Any? {
        return (pop() as ObjectValue).value
    }

    private fun popString(): String {
        return (pop() as StringValue).value.toString()
    }

    private fun jumpTo(label: Label) {
        _nextInsruction = _labelInstructionMap[label.id]!!
    }

    data class ClassMember(val className: String, val memberName: String)

    // parses java/lang/System.out, java/io/PrintStream.println etc.
    private fun parseMember(str: String): ClassMember {
        return ClassMember(str.substringBefore(".").replace("/", "."), str.substringAfter("."))
    }
}
