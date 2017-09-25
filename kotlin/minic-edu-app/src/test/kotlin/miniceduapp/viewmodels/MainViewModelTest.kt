package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.testutils.BaseTornadoFxComponentTest
import miniceduapp.views.events.ErrorMessageEvent
import miniceduapp.views.events.RequestFilePathEvent
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import tornadofx.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(JfxRunner::class)
class MainViewModelTest : BaseTornadoFxComponentTest() {

    val vm = MainViewModel()

    @Rule
    @JvmField
    val tmpFolder = TemporaryFolder()

    @Test
    @TestInJfxThread
    fun hasValidInitialState() {
        assertEquals("", vm.programCode)
        assertEquals("", vm.filePath)
        assertEquals(false, vm.hasUnsavedCode)
        assertEquals(false, vm.isExecutingProgram)

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        assertTrue(vm.saveNewCodeFileCommand.isEnabled)
        assertTrue(vm.openCodeFileCommand.isEnabled)
        assertTrue(vm.createNewCodeCommand.isEnabled)

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)

        assertTrue(vm.openTokensWindow.isEnabled)
        assertTrue(vm.openAstWindow.isEnabled)
        assertTrue(vm.openSymbolsWindow.isEnabled)
        assertTrue(vm.openBytecodeWindow.isEnabled)
        assertTrue(vm.openInteractiveExecutionWindow.isEnabled)

        assertTrue(vm.errors.isEmpty())
    }

    @Test
    @TestInJfxThread
    fun loadsSampleCode() {
        assertTrue(vm.loadSampleCodeCommand.isEnabled)
        vm.loadSampleCodeCommand.execute()

        assertNotEquals("", vm.programCode)
        assertEquals("", vm.filePath)
        assertEquals(false, vm.hasUnsavedCode)
    }

    @Test
    @TestInJfxThread
    fun loadsFile() {
        val filepath = tmpFolder.root.absolutePath + "/program.mc"

        File(filepath).writeText("hi")

        assertTrue(vm.openCodeFileCommand.isEnabled)
        vm.openCodeFileCommand.execute(filepath)

        assertEquals("hi", vm.programCode)
        assertEquals(filepath, vm.filePath)

        assertTrue(vm.openCodeFileCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun firesErrorIfFileNotExists() {
        val filepath = tmpFolder.root.absolutePath + "/not_existing.mc"

        var error = ""
        subscribe<ErrorMessageEvent>(times = 1) {
            error = it.text
        }

        ignoreErrors {
            vm.openCodeFileCommand.execute(filepath)
        }

        assertThat(error, containsString("not found"))
    }

    @Test
    @TestInJfxThread
    fun savesFile() {
        vm.programCode = "hello"

        val filepath = tmpFolder.root.absolutePath + "/program.mc"
        subscribe<RequestFilePathEvent>(times = 1) {
            it.result = filepath
        }

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello", File(filepath).readText())

        vm.programCode = "hello world"

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello world", File(filepath).readText())
    }

    @Test
    @TestInJfxThread
    fun savesNewFile() {
        vm.programCode = "hello"

        val filepath = tmpFolder.root.absolutePath + "/program.mc"
        subscribe<RequestFilePathEvent>(times = 1) {
            it.result = filepath
        }

        assertTrue(vm.saveCodeFileCommand.isEnabled)
        vm.saveCodeFileCommand.execute()

        assertTrue(File(filepath).exists())
        assertEquals("hello", File(filepath).readText())

        vm.programCode = "hello world"

        val newFilepath = tmpFolder.root.absolutePath + "/new_program.mc"

        assertTrue(vm.saveNewCodeFileCommand.isEnabled)
        vm.saveNewCodeFileCommand.execute(newFilepath)

        assertTrue(File(newFilepath).exists())
        assertEquals("hello world", File(newFilepath).readText())
        assertEquals("hello", File(filepath).readText())
    }

    @Test
    @TestInJfxThread
    fun saveDisabledOnlyWhenNoChangesAfterSaveOrOpen() {
        vm.createNewCodeCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        val filepath = tmpFolder.root.absolutePath + "/program.mc"

        subscribe<RequestFilePathEvent>(times = 1) {
            it.result = filepath
        }

        vm.saveCodeFileCommand.execute()

        assertFalse(vm.saveCodeFileCommand.isEnabled)

        vm.programCode = "hello"

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        assertTrue(vm.openCodeFileCommand.isEnabled)
        vm.openCodeFileCommand.execute(filepath)

        assertFalse(vm.saveCodeFileCommand.isEnabled)

        vm.programCode = "hello"

        assertTrue(vm.saveCodeFileCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun canAbortFileSave() {
        vm.createNewCodeCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)

        subscribe<RequestFilePathEvent>(times = 1) {
            //it.result = null
        }

        vm.saveCodeFileCommand.execute()

        assertTrue(vm.saveCodeFileCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun canExecuteProgram() {
        vm.programCode = """
int x = 42;
println("x = " + toString(x));
"""
        assertTrue(vm.executeCodeCommand.isEnabled)
        vm.executeCodeCommand.execute()

        assertFalse(vm.executeCodeCommand.isEnabled)
        assertTrue(vm.stopCodeExecutionCommand.isEnabled)

        vm.isExecutingProgramProperty.not().awaitUntil()

        val outputLines = vm.codeExecutionViewModel.output.trim().split("\n")
        assertThat(outputLines[0], containsString("minic"))
        assertThat(outputLines[1], containsString("java"))
        assertEquals("x = 42", outputLines[2])

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun notExecutesWhenErrors() {
        vm.programCode = """
int x = undefVar;
"""
        assertTrue(vm.errors.isEmpty())
        assertTrue(vm.executeCodeCommand.isEnabled)

        vm.executeCodeCommand.execute()

        assertFalse(vm.isExecutingProgram)
        assertTrue(vm.errors.isNotEmpty())
        assertFalse(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun detectsErrorsAsync() {
        assertTrue(vm.errors.isEmpty())

        vm.programCode = "int x = y;"

        vm.validateCodeAsync()

        vm.validationTaskStatus.completed.awaitUntil()
        assertTrue(vm.errors.isNotEmpty())
        assertFalse(vm.hasParsingErrors)
        assertTrue(vm.openAstWindow.isEnabled)
        assertTrue(vm.openSymbolsWindow.isEnabled)
        assertFalse(vm.openBytecodeWindow.isEnabled)
        assertFalse(vm.openInteractiveExecutionWindow.isEnabled)

        vm.programCode = "int x = 42;"

        vm.validateCodeAsync()

        vm.validationTaskStatus.completed.awaitUntil()
        assertTrue(vm.errors.isEmpty())

        vm.programCode = "int x = 42"

        vm.validateCodeAsync()

        vm.validationTaskStatus.completed.awaitUntil()
        assertTrue(vm.errors.isNotEmpty())
        assertTrue(vm.hasParsingErrors)
        assertFalse(vm.openAstWindow.isEnabled)
        assertFalse(vm.openSymbolsWindow.isEnabled)
        assertFalse(vm.openBytecodeWindow.isEnabled)
        assertFalse(vm.openInteractiveExecutionWindow.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun detectsErrors() {
        assertTrue(vm.errors.isEmpty())

        vm.programCode = "int x = y;"

        vm.validateCode()

        assertTrue(vm.errors.isNotEmpty())
        assertFalse(vm.hasParsingErrors)
        assertTrue(vm.openAstWindow.isEnabled)
        assertTrue(vm.openSymbolsWindow.isEnabled)
        assertFalse(vm.openBytecodeWindow.isEnabled)
        assertFalse(vm.openInteractiveExecutionWindow.isEnabled)

        vm.programCode = "int x = 42;"

        vm.validateCode()

        assertTrue(vm.errors.isEmpty())

        vm.programCode = "int x = 42"

        vm.validateCode()

        assertTrue(vm.errors.isNotEmpty())
        assertTrue(vm.hasParsingErrors)
        assertFalse(vm.openAstWindow.isEnabled)
        assertFalse(vm.openSymbolsWindow.isEnabled)
        assertFalse(vm.openBytecodeWindow.isEnabled)
        assertFalse(vm.openInteractiveExecutionWindow.isEnabled)
    }
}
