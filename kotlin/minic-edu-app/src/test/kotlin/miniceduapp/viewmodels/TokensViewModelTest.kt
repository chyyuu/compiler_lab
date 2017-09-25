package miniceduapp.viewmodels

import de.saxsys.mvvmfx.testingutils.jfxrunner.*
import miniceduapp.testutils.BaseTornadoFxComponentTest
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(JfxRunner::class)
class TokensViewModelTest : BaseTornadoFxComponentTest() {

    val vm = TokensViewModel(updateDelay = 100.millis)

    @Test
    @TestInJfxThread
    fun loadsTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertEquals(prevTokensCount, vm.tokens.size)
    }

    @Test
    @TestInJfxThread
    fun updatesTokens() {
        assertEquals(0, vm.tokens.size)

        vm.mainViewModel.programCode = "int x = 41;"
        vm.loadTokens()

        vm.status.completed.awaitUntil()

        assertNotEquals(0, vm.tokens.size)

        val prevTokensCount = vm.tokens.size

        vm.mainViewModel.programCode = "int x = 41 + 1;"

        vm.status.running.awaitUntil()
        vm.status.completed.awaitUntil()

        assertEquals(prevTokensCount + 2, vm.tokens.size)
    }

    @Test
    @TestInJfxThread
    fun selectsTokenFromCodeIndex() {
        assertEquals(null, vm.selectedToken)

        vm.setSelectedTokenFromCode(0)
        assertEquals(null, vm.selectedToken)

        vm.mainViewModel.programCode = "int x = 42;"
        vm.loadTokens()

        vm.status.completed.awaitUntil()
        assertNotEquals(0, vm.tokens.size)

        vm.setSelectedTokenFromCode(0)
        assertEquals(vm.tokens[0], vm.selectedToken)

        vm.setSelectedTokenFromCode(1)
        assertEquals(vm.tokens[0], vm.selectedToken)

        vm.setSelectedTokenFromCode(2)
        assertEquals(vm.tokens[0], vm.selectedToken)

        vm.setSelectedTokenFromCode(3)
        assertEquals(null, vm.selectedToken)

        vm.setSelectedTokenFromCode(4)
        assertEquals(vm.tokens[1], vm.selectedToken)

        vm.setSelectedTokenFromCode(5)
        assertEquals(null, vm.selectedToken)

        vm.setSelectedTokenFromCode(10)
        assertEquals(vm.tokens[4], vm.selectedToken)

        vm.setSelectedTokenFromCode(11)
        assertEquals(null, vm.selectedToken)
    }
}
