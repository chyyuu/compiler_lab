package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.bytecodde.EmptyValue
import miniceduapp.bytecodde.IntValue
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(JfxRunner::class)
class InteractiveExecutionModelTest : BaseTornadoFxComponentTest() {

    val vm = InteractiveExecutionViewModel()

    @Test
    @TestInJfxThread
    fun executesAndStops() {
        assertEquals(0, vm.bytecode.size)

        vm.mainViewModel.programCode = """
int x = 1 + 2 * 3 - 4 + 5 * 6;
int y = x;
""".trim()
        vm.loadBytecode()

        booleanBinding(vm.bytecode) { size > 10 }.awaitUntil()

        assertNotEquals(0, vm.bytecode.size)

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.executeNextInstructionCommand.isEnabled)
        assertFalse(vm.goToNextLineCommand.isEnabled)

        assertFalse(vm.isExecuting)
        assertEquals(-1, vm.nextInstructionIndex)
        assertEquals(-1, vm.nextLine)

        vm.executeCodeCommand.execute()

        assertFalse(vm.executeCodeCommand.isEnabled)
        assertTrue(vm.stopCodeExecutionCommand.isEnabled)
        assertTrue(vm.executeNextInstructionCommand.isEnabled)
        assertTrue(vm.goToNextLineCommand.isEnabled)

        assertTrue(vm.isExecuting)
        assertEquals(0, vm.nextInstructionIndex)
        assertEquals(1, vm.nextLine)
        assertEquals(100, vm.variables.size)
        assertEquals(100, vm.operandStackValues.size)
        assertNotEquals(EmptyValue(), vm.variables[0])
        assertTrue(vm.operandStackValues.all { it is EmptyValue })
        assertTrue(vm.variables.subList(1, 100).all { it is EmptyValue })

        vm.executeNextInstructionCommand.execute()

        assertEquals(1, vm.nextInstructionIndex)
        assertEquals(1, vm.nextLine)
        assertEquals(100, vm.variables.size)
        assertEquals(100, vm.operandStackValues.size)
        assertNotEquals(EmptyValue(), vm.operandStackValues[0])
        assertTrue(vm.operandStackValues.subList(1, 100).all { it is EmptyValue })
        assertTrue(vm.variables.subList(1, 100).all { it is EmptyValue })

        vm.goToNextLineCommand.execute()

        assertTrue(vm.nextInstructionIndex > 10, vm.nextInstructionIndex.toString())
        assertEquals(2, vm.nextLine)
        assertEquals(100, vm.variables.size)
        assertEquals(100, vm.operandStackValues.size)
        assertEquals(IntValue(33), vm.variables[1])
        assertTrue(vm.variables.subList(2, 100).all { it is EmptyValue })

        vm.stopCodeExecutionCommand.execute()

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.executeNextInstructionCommand.isEnabled)
        assertFalse(vm.goToNextLineCommand.isEnabled)

        assertFalse(vm.isExecuting)
        assertEquals(-1, vm.nextInstructionIndex)
        assertEquals(-1, vm.nextLine)
    }

    @Test
    @TestInJfxThread
    fun executesUntilEnd() {
        assertEquals(0, vm.bytecode.size)

        vm.mainViewModel.programCode = """
int x = 1 + 2 * 3 - 4 + 5 * 6;
int y = x;
""".trim()
        vm.loadBytecode()

        booleanBinding(vm.bytecode) { isNotEmpty() }.awaitUntil()

        assertNotEquals(0, vm.bytecode.size)

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.executeNextInstructionCommand.isEnabled)
        assertFalse(vm.goToNextLineCommand.isEnabled)

        assertFalse(vm.isExecuting)
        assertEquals(-1, vm.nextInstructionIndex)
        assertEquals(-1, vm.nextLine)

        vm.executeCodeCommand.execute()

        while (vm.isExecuting) {
            vm.executeNextInstructionCommand.execute()
        }

        assertTrue(vm.executeCodeCommand.isEnabled)
        assertFalse(vm.stopCodeExecutionCommand.isEnabled)
        assertFalse(vm.executeNextInstructionCommand.isEnabled)
        assertFalse(vm.goToNextLineCommand.isEnabled)

        assertFalse(vm.isExecuting)
        assertEquals(-1, vm.nextInstructionIndex)
        assertEquals(-1, vm.nextLine)
    }
}
