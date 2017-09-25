package miniceduapp.viewmodels

import javafx.beans.property.*
import miniceduapp.CodeExecutor
import miniceduapp.views.events.ErrorEvent
import org.apache.commons.io.FilenameUtils
import tornadofx.*
import java.nio.file.Paths

class CodeExecutionViewModel : ViewModel() {
    private val _isExecutingProgramProperty = ReadOnlyBooleanWrapper(false)
    private var _isExecutingProgram by _isExecutingProgramProperty
    val isExecutingProgramProperty: ReadOnlyBooleanProperty get() = _isExecutingProgramProperty.readOnlyProperty
    val isExecutingProgram: Boolean get() = _isExecutingProgramProperty.value

    private val _hasInputOperationsProperty = ReadOnlyBooleanWrapper(false)
    private var _hasInputOperations by _hasInputOperationsProperty
    val hasInputOperationsProperty: ReadOnlyBooleanProperty get() = _hasInputOperationsProperty.readOnlyProperty
    val hasInputOperations: Boolean get() = _hasInputOperationsProperty.value

    private val _outputProperty = ReadOnlyStringWrapper("")
    private var _output by _outputProperty
    val outputProperty: ReadOnlyStringProperty get() = _outputProperty.readOnlyProperty
    val output: String get() = _outputProperty.value

    val inputProperty = SimpleStringProperty("")
    var input: String by inputProperty

    private var codeExecutor: CodeExecutor? = null

    val executeCodeCommand = command<String>(this::executeCode,
            enabled = isExecutingProgramProperty.not())

    val stopCodeExecutionCommand = command(this::stopCodeExecution,
            enabled = isExecutingProgramProperty)

    val writeInputCommand = command(this::writeInput,
            enabled = isExecutingProgramProperty)

    private fun executeCode(programCode: String) {
        _output = ""

        val mainViewModel = find<MainViewModel>()
        val simulatedFileName = if (mainViewModel.filePath.isNullOrEmpty()) "program.mc" else Paths.get(mainViewModel.filePath).fileName.toString()
        _output += "> minic $simulatedFileName\n"

        _isExecutingProgram = true

        codeExecutor = CodeExecutor(programCode, onOutput = {
            _output += it
        }, onFail = {
            fire(ErrorEvent(it))
        }, onFinish = {
            _isExecutingProgram = false
        }, onCompiled = {
            _output += "> java ${FilenameUtils.getBaseName(simulatedFileName)}\n"
        })
        _hasInputOperations = codeExecutor!!.hasInputOperations

        codeExecutor!!.start()
    }

    private fun stopCodeExecution() {
        codeExecutor?.stop()
    }

    private fun writeInput() {
        val str = input.trim() + "\n"
        _output += str
        input = ""
        codeExecutor?.writeInput(str)
    }
}
