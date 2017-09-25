package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(JfxRunner::class)
class BytecodeViewModelTest : BaseTornadoFxComponentTest() {

    val vm = BytecodeViewModel(updateDelay = 100.millis)

    @Test
    @TestInJfxThread
    fun loadsBytecode() {
        assertEquals(0, vm.bytecode.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadBytecode()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.bytecode.size)

        val prevCount = vm.bytecode[0].instructions.size
        val prevLinesCount = vm.bytecode.size

        vm.loadBytecode()

        vm.status.completed.awaitUntil()

        assertEquals(prevLinesCount, vm.bytecode.size)
        assertEquals(prevCount, vm.bytecode[0].instructions.size)
    }

    @Test
    @TestInJfxThread
    fun updatesBytecode() {
        assertEquals(0, vm.bytecode.size)

        vm.mainViewModel.programCode = "int x = 41;"
        vm.loadBytecode()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.bytecode.size)

        val prevCount = vm.bytecode[0].instructions.size

        vm.mainViewModel.programCode = "int x = 41 + 1;"

        vm.status.running.awaitUntil()
        vm.status.completed.awaitUntil()

        assertTrue(prevCount < vm.bytecode[0].instructions.size)
    }
}
