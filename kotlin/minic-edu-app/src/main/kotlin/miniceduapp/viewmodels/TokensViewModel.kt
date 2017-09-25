package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.util.Duration
import minic.Compiler
import minic.frontend.lexer.Token
import miniceduapp.views.events.ErrorEvent
import tornadofx.*

class TokensViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val tokens = mutableListOf<Token>().observable()

    val selectedTokenProperty = SimpleObjectProperty<Token>()
    var selectedToken by selectedTokenProperty

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadTokens()
            }
        }
    }

    fun loadTokens() {
        timerTask?.cancel()

        val code = mainViewModel.programCode

        if (code == programCode || status.running.value) {
            return
        }

        runAsync(status) {
            Compiler(code).tokens
        } ui {
            _programCode = code
            selectedToken = null
            tokens.clear()
            tokens.addAll(it)
        } fail {
            fire(ErrorEvent(it))
        }
    }

    fun setSelectedTokenFromCode(cursorPos: Int) {
        selectedToken = tokens.firstOrNull {
            cursorPos >= it.startIndex && cursorPos <= it.endIndex
        }
    }
}
