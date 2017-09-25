package miniceduapp.viewmodels

import javafx.beans.property.*
import miniceduapp.CodeExecutor
import miniceduapp.bytecodde.BytecodeInterpreter
import miniceduapp.bytecodde.EmptyValue
import miniceduapp.bytecodde.Instruction
import miniceduapp.bytecodde.Value
import miniceduapp.views.events.ErrorMessageEvent
import tornadofx.*

class InteractiveExecutionViewModel : ViewModel() {
    val mainViewModel: MainViewModel by inject()
    val bytecodeViewModel: BytecodeViewModel by inject()

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val bytecode = mutableListOf<Instruction>().observable()

    private val _isExecutingProperty = ReadOnlyBooleanWrapper(false)
    private var _isExecuting by _isExecutingProperty
    val isExecutingProperty: ReadOnlyBooleanProperty get() = _isExecutingProperty.readOnlyProperty
    val isExecuting: Boolean get() = _isExecutingProperty.value

    private val _nextInstructionIndexProperty = ReadOnlyIntegerWrapper(-1)
    private var _nextInstructionIndex by _nextInstructionIndexProperty
    val nextInstructionIndexProperty: ReadOnlyIntegerProperty get() = _nextInstructionIndexProperty.readOnlyProperty
    val nextInstructionIndex: Int get() = _nextInstructionIndexProperty.value

    private val _nextLineProperty = ReadOnlyIntegerWrapper(-1)
    private var _nextLine by _nextLineProperty
    val nextLineProperty: ReadOnlyIntegerProperty get() = _nextLineProperty.readOnlyProperty
    val nextLine: Int get() = _nextLineProperty.value

    val variables = mutableListOf<Value>().observable()
    val operandStackValues = mutableListOf<Value>().observable()

    private var interpreter: BytecodeInterpreter? = null

    val executeCodeCommand = command(this::executeCode,
            enabled = isExecutingProperty.not().and(booleanBinding(mainViewModel.errors) { isEmpty() }))

    val stopCodeExecutionCommand = command(this::stopCodeExecution,
            enabled = isExecutingProperty)

    val executeNextInstructionCommand = command(this::executeNextInstruction,
            enabled = isExecutingProperty)

    val goToNextLineCommand = command(this::goToNextLine,
            enabled = isExecutingProperty)


    init {
        bytecodeViewModel.programCodeProperty.onChange {
            loadBytecode()
        }
        bytecodeViewModel.bytecode.onChange {
            loadBytecode()
        }
    }

    fun loadBytecode() {
        if (isExecuting) {
            return
        }

        _programCode = bytecodeViewModel.programCode

        bytecode.clear()
        bytecode.addAll(bytecodeViewModel.bytecode.flatMap { it.instructions })
    }

    private fun executeCode() {
        if (CodeExecutor(programCode, {}, {}).hasInputOperations) {
            fire(ErrorMessageEvent("Interactive execution does not support input operations. Please remove all read* expressions."))
            return
        }

        interpreter = BytecodeInterpreter(bytecode).apply {
            start()
        }

        _isExecuting = true

        loadState()
    }

    private fun stopCodeExecution() {
        interpreter?.stop()

        _isExecuting = false
        _nextInstructionIndex = -1
        _nextLine = -1

        loadBytecode()
    }

    private fun executeNextInstruction() {
        interpreter!!.executeNextInstruction()

        if (!interpreter!!.isExecuting) {
            stopCodeExecution()
        } else {
            loadState()
        }
    }

    private fun goToNextLine() {
        val currLine = _nextLine

        while (currLine == _nextLine && isExecuting) {
            executeNextInstruction()
        }
    }

    private fun loadState() {
        variables.clear()
        operandStackValues.clear()

        variables.addAll(interpreter!!.variables.filterNotNull())
        operandStackValues.addAll(interpreter!!.operandStack)

        while (variables.size < 100) {
            variables.add(EmptyValue())
        }
        while (operandStackValues.size < 100) {
            operandStackValues.add(EmptyValue())
        }
        
        _nextInstructionIndex = interpreter!!.nextInsruction
        if (_nextInstructionIndex < bytecode.size) {
            _nextLine = bytecode[_nextInstructionIndex]?.line ?: -1
        }
    }
}
