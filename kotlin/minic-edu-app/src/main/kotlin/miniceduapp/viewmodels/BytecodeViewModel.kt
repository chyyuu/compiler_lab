package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.util.Duration
import minic.Compiler
import minic.CompilerConfiguration
import miniceduapp.bytecodde.BytecodeTextParser
import miniceduapp.bytecodde.Instruction
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

data class BytecodeLine(val line: Int, val instructions: List<Instruction>)

class BytecodeViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val bytecode = mutableListOf<BytecodeLine>().observable()

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadBytecode()
            }
        }

        loadBytecode()
    }

    fun loadBytecode() {
        timerTask?.cancel()

        mainViewModel.validateCode()
        if (mainViewModel.errors.any()) {
            return
        }

        val code = mainViewModel.programCode

        if (code == programCode || status.running.value) {
            return
        }

        runAsync(status) {
            BytecodeTextParser(Compiler(code, CompilerConfiguration(debugInfo = true)).bytecodeText()).parse()
        } ui {
            _programCode = code
            bytecode.clear()
            bytecode.addAll(it.groupBy { it.line!! }.map { BytecodeLine(it.key, it.value) })

        } fail {
            fire(ErrorEvent(it))
        }
    }
}
