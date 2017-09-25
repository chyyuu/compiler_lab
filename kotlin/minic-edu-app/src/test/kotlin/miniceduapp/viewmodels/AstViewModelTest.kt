package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import minic.frontend.ast.IntLiteral
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.*

@RunWith(JfxRunner::class)
class AstViewModelTest : BaseTornadoFxComponentTest() {

    val vm = AstViewModel(updateDelay = 100.millis)

    @Test
    @TestInJfxThread
    fun loadsAst() {
        assertNull(vm.astImage)

        vm.mainViewModel.programCode = "int x = 86;"
        vm.loadAst()

        vm.status.completed.awaitUntil()

        assertNotNull(vm.astImage)
        assertEquals("int x = 86;", vm.programCode)
    }

    @Test
    @TestInJfxThread
    fun selectsNodeFromPosition() {
        assertNull(vm.astImage)

        vm.mainViewModel.programCode = "int x = 89;"
        vm.loadAst()

        vm.status.completed.awaitUntil()

        assertNotNull(vm.astImage)
        assertNull(vm.selectedAstNode)
        assertEquals("int x = 89;", vm.programCode)

        vm.setSelectedNodeFromCode(1, 8)
        assertNotNull(vm.selectedAstNode)
        assertTrue(vm.selectedAstNode is IntLiteral, vm.selectedAstNode!!.javaClass.simpleName)

        vm.setSelectedNodeFromCode(2, 8)
        assertNull(vm.selectedAstNode)
    }
}
