package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner
import de.saxsys.mvvmfx.testingutils.jfxrunner.TestInJfxThread
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(JfxRunner::class)
class CodeExecutionViewModelTest : BaseTornadoFxComponentTest() {

    val vm = CodeExecutionViewModel()

    @Test
    @TestInJfxThread
    fun hasValidInitialState() {
        assertEquals(false, vm.isExecutingProgram)

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.writeInputCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun canExecuteProgram() {
        val code = """
int x = 42;
println("x = " + toString(x));
"""
        assertTrue(vm.executeCodeCommand.isEnabled)
        vm.executeCodeCommand.execute(code)

        assertFalse(vm.executeCodeCommand.isEnabled)
        assertTrue(vm.stopCodeExecutionCommand.isEnabled)
        assertTrue(vm.writeInputCommand.isEnabled)
        assertFalse(vm.hasInputOperations)

        vm.isExecutingProgramProperty.not().awaitUntil()

        val outputLines = vm.output.trim().split("\n")
        assertThat(outputLines[0], containsString("minic"))
        assertThat(outputLines[1], containsString("java"))
        assertEquals("x = 42", outputLines[2])

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.writeInputCommand.isEnabled)
    }

    @Test
    @TestInJfxThread
    fun canExecuteProgramWithInput() {
        val code = """
print("Enter: ");
int x = readInt();
string name = readLine();
println("x = " + toString(x));
println("Hello, " + name);
"""
        assertTrue(vm.executeCodeCommand.isEnabled)
        vm.executeCodeCommand.execute(code)

        assertFalse(vm.executeCodeCommand.isEnabled)
        assertTrue(vm.stopCodeExecutionCommand.isEnabled)
        assertTrue(vm.writeInputCommand.isEnabled)
        assertTrue(vm.hasInputOperations)

        vm.outputProperty.booleanBinding(op = { it!!.contains("Enter: ") }).awaitUntil()

        vm.input = "42"
        vm.writeInputCommand.execute()
        vm.input = "John"
        vm.writeInputCommand.execute()

        vm.isExecutingProgramProperty.not().awaitUntil()

        val outputLines = vm.output.trim().split("\n")
        assertThat(outputLines[0], containsString("minic"))
        assertThat(outputLines[1], containsString("java"))
        assertEquals("Enter: 42", outputLines[2])
        assertEquals("John", outputLines[3])
        assertEquals("x = 42", outputLines[4])
        assertEquals("Hello, John", outputLines[5])

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.writeInputCommand.isEnabled)
    }
}
