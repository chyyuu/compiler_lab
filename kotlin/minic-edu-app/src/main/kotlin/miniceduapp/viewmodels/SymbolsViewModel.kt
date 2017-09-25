package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.util.Duration
import minic.Compiler
import minic.frontend.ast.AstNode
import minic.frontend.ast.Program
import minic.frontend.ast.StatementsBlock
import minic.frontend.scope.Scope
import minic.frontend.scope.Symbol
import minic.frontend.scope.processWithSymbols
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class SymbolsViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val astProperty = SimpleObjectProperty<Program>()
    var ast: Program by astProperty

    val symbols = mutableListOf<Symbol>().observable()

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadSymbols()
            }
        }
    }

    fun loadSymbols() {
        timerTask?.cancel()

        mainViewModel.validateCode()
        if (mainViewModel.hasParsingErrors) {
            return
        }

        val code = mainViewModel.programCode

        if (status.running.value) {
            return
        }

        runAsync(status) {
            val compiler = Compiler(code)
            compiler.ast
        } ui {
            ast = it
            _programCode = code
        } fail {
            fire(ErrorEvent(it))
        }
    }

    fun setSelectedNodeFromCode(cursorLine: Int) {
        ast.processWithSymbols { node: AstNode, scope: Scope ->
            if (node !is Program && node !is StatementsBlock) {
                if (cursorLine == node.position!!.start.line) {
                    symbols.clear()
                    symbols.addAll(scope.allSymbols())
                    println(node)
                }
            }
        }
    }
}
