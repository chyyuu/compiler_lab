package miniceduapp.viewmodels

import javafx.beans.property.*
import javafx.util.Duration
import minic.Compiler
import minic.frontend.validation.Error
import miniceduapp.helpers.messageOrString
import miniceduapp.views.*
import miniceduapp.views.events.*
import tornadofx.*
import java.io.File

class MainViewModel(val updateDelay: Duration = 1.seconds) : ViewModel() {
    val codeExecutionViewModel: CodeExecutionViewModel by inject()

    val programCodeProperty = SimpleStringProperty("")
    var programCode: String by programCodeProperty

    private val _filePathProperty = ReadOnlyStringWrapper("")
    private var _filePath by _filePathProperty
    val filePathProperty: ReadOnlyStringProperty get() = _filePathProperty.readOnlyProperty
    val filePath: String get() = _filePathProperty.value

    private val _hasUnsavedCodeProperty = ReadOnlyBooleanWrapper(false)
    private var _hasUnsavedCode by _hasUnsavedCodeProperty
    val hasUnsavedCodeProperty: ReadOnlyBooleanProperty get() = _hasUnsavedCodeProperty.readOnlyProperty
    val hasUnsavedCode: Boolean get() = _hasUnsavedCodeProperty.value

    val isExecutingProgramProperty: ReadOnlyBooleanProperty get() = codeExecutionViewModel.isExecutingProgramProperty
    val isExecutingProgram: Boolean get() = isExecutingProgramProperty.value

    val errors = mutableListOf<Error>().observable()

    private val _hasParsingErrorsProperty = ReadOnlyBooleanWrapper(false)
    private var _hasParsingErrors by _hasParsingErrorsProperty
    val hasParsingErrorsProperty: ReadOnlyBooleanProperty get() = _hasParsingErrorsProperty.readOnlyProperty
    val hasParsingErrors: Boolean get() = _hasParsingErrorsProperty.value

    val validationTaskStatus = TaskStatus()

    val codeFileFilters = listOf(
            FileExtensionFilter("Mini-C source code", listOf("*.mc")),
            FileExtensionFilter("All files", listOf("*.*"))
    )

    private var validationTimerTask: FXTimerTask? = null

    init {
        programCodeProperty.onChange {
            _hasUnsavedCode = true
            validationTimerTask?.cancel()

            validationTimerTask = runLater(updateDelay) {
                validateCodeAsync()
            }
        }
    }

    val saveCodeFileCommand = command(this::saveCodeFile,
            enabled = hasUnsavedCodeProperty.or(filePathProperty.isEmpty))

    val saveNewCodeFileCommand = command<String>(this::saveCodeFile)

    val openCodeFileCommand = command(this::openCodeFile)

    val createNewCodeCommand = command(this::createNewCode)

    val loadSampleCodeCommand = command(this::loadSampleCode)

    val executeCodeCommand = command(this::executeCode,
            enabled = codeExecutionViewModel.executeCodeCommand.enabled.and(booleanBinding(errors) { isEmpty() }))

    val stopCodeExecutionCommand = command(this::stopCodeExecution,
            enabled = isExecutingProgramProperty)

    val openTokensWindow = command {
        fire(OpenWindowEvent(TokensView::class))
    }

    val openSymbolsWindow = command(enabled = hasParsingErrorsProperty.not()) {
        fire(OpenWindowEvent(SymbolsView::class))
    }

    val openAstWindow = command(enabled = hasParsingErrorsProperty.not()) {
        fire(OpenWindowEvent(AstView::class))
    }

    val openBytecodeWindow = command(enabled = booleanBinding(errors) { isEmpty() }) {
        fire(OpenWindowEvent(BytecodeView::class))
    }

    val openInteractiveExecutionWindow = command(enabled = booleanBinding(errors) { isEmpty() }) {
        fire(OpenWindowEvent(InteractiveExecutionView::class))
    }

    fun validateCode() {
        validationTimerTask?.cancel()

        try {
            val compiler = Compiler(programCode)
            val newErrors = compiler.validate()
            if (errors != newErrors) {
                errors.clear()
                errors.addAll(newErrors)
                _hasParsingErrors = compiler.parsingResult.errors.any()
            }
        } catch (ex: Throwable) {
            fire(ErrorEvent(ex))
        }
    }

    fun validateCodeAsync() {
        validationTimerTask?.cancel()

        val code = programCode // probably shouldn't access property from another thread

        runAsync(validationTaskStatus) {
            val compiler = Compiler(code)
            compiler.validate() to compiler.parsingResult.errors.any()
        } ui {
            val (newErrors, hasParsingErrors) = it
            if (errors != newErrors) {
                errors.clear()
                errors.addAll(newErrors)
                _hasParsingErrors = hasParsingErrors
            }
        } fail {
            fire(ErrorEvent(it))
        }
    }

    private fun saveCodeFile() {
        val fp = if (filePath.isNullOrEmpty()) {
            val request = RequestFilePathEvent(codeFileFilters)
            fire(request)
            if (request.result == null) {
                return
            }
            request.result!!
        } else {
            this.filePath
        }

        saveCodeFile(fp)
    }

    private fun saveCodeFile(newFilePath: String) {
        try {
            File(newFilePath).writeText(programCode)
            _filePath = newFilePath
            _hasUnsavedCode = false
        } catch (ex: Throwable) {
            fire(ErrorEvent(ex, "Failed to save file '$newFilePath': ${ex.messageOrString()}."))
        }
    }

    private fun openCodeFile(newFilePath: String) {
        if (!File(newFilePath).exists()) {
            fire(ErrorMessageEvent("File '$newFilePath' not found."))
            return
        }

        try {
            programCode = File(newFilePath).readText()
            _filePath = newFilePath
            _hasUnsavedCode = false

            validateCodeAsync()
        } catch (ex: Throwable) {
            fire(ErrorEvent(ex, "Failed to open file '$newFilePath': ${ex.messageOrString()}."))
        }
    }

    private fun createNewCode() {
        programCode = ""
        _filePath = ""
        _hasUnsavedCode = false
        errors.clear()
    }

    private fun loadSampleCode() {
        createNewCode()
        programCode = """println("Hello");
int x = 42;
int y = x + 6 * 2 / (3 - 1);
print("y: " + toString(y));
"""
        _hasUnsavedCode = false
    }
    
    private fun executeCode() {
        validateCode()
        if (errors.isNotEmpty()) {
            return
        }

        codeExecutionViewModel.executeCodeCommand.execute(programCode)

        fire(OpenWindowEvent(CodeExecutionView::class))
    }

    private fun stopCodeExecution() {
        codeExecutionViewModel.stopCodeExecutionCommand.execute()
    }
}
