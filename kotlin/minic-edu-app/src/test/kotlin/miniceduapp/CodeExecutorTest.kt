package miniceduapp

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner
import de.saxsys.mvvmfx.testingutils.jfxrunner.TestInJfxThread
import org.junit.Test
import org.junit.runner.RunWith
import tornadofx.*
import kotlin.test.*

@RunWith(JfxRunner::class)
class CodeExecutorTest {

    @Test
    @TestInJfxThread
    fun executes() {
        val code = """
println("Hello");
"""
        var error: Throwable? = null
        var finishCalled = false
        var compiledCalled = false
        val sb = StringBuilder()

        val executor = CodeExecutor(code, onOutput = {
            sb.append(it)
        }, onFail = {
            error = it
        }, onFinish = {
            finishCalled = true
        }, onCompiled = {
            compiledCalled = true
        })
        executor.start()

        executor.isFinishedProperty.awaitUntil()

        assertNull(error, error.toString())
        assertTrue(finishCalled)
        assertTrue(compiledCalled)
        assertEquals("Hello", sb.toString().trim())
    }

    @Test
    @TestInJfxThread
    fun canBeStopped() {
        val code = """
while (true) {
    println("42");
}
"""
        var error: Throwable? = null
        var finishCalled = false

        val executor = CodeExecutor(code, onOutput = {
            stop()
        }, onFail = {
            error = it
        }, onFinish = {
            finishCalled = true
        })
        executor.start()

        executor.isFinishedProperty.awaitUntil()

        assertNull(error, error.toString())
        assertTrue(finishCalled)
    }

    @Test
    @TestInJfxThread
    fun acceptsInput() {
        val code = """
println("Enter: ");
int x = readInt();
string name = readLine();
println("x = " + toString(x));
println("Hello, " + name);
"""
        var error: Throwable? = null
        var finishCalled = false
        val sb = StringBuilder()

        var sentInput = false

        val executor = CodeExecutor(code, onOutput = {
            sb.append(it)

            if (!sentInput) {
                runLater(100.millis) {
                    writeInput("John\n")
                }
                sentInput = false
            }
        }, onFail = {
            error = it
        }, onFinish = {
            finishCalled = true
        })
        executor.start()

        executor.writeInput("42\n")

        executor.isFinishedProperty.awaitUntil()

        assertNull(error, error.toString())
        assertTrue(finishCalled)
        assertEquals("Enter: \nx = 42\nHello, John", sb.toString().trim())
    }

    @Test
    @TestInJfxThread
    fun returnsError() {
        val code = """
incorrect code
"""
        var error: Throwable? = null
        var finishCalled = false

        val executor = CodeExecutor(code, onOutput = {
        }, onFail = {
            error = it
        }, onFinish = {
            finishCalled = true
        })
        executor.start()

        executor.isFinishedProperty.awaitUntil()

        assertNotNull(error)
        assertTrue(finishCalled)
    }

    @Test
    @TestInJfxThread
    fun detectsInputOperations() {
        assertFalse(CodeExecutor("int x = 42; println(toString(x);", {}, {}).hasInputOperations)
        assertTrue(CodeExecutor("int x = readInt();", {}, {}).hasInputOperations)
        assertTrue(CodeExecutor("double x = readDouble();", {}, {}).hasInputOperations)
        assertTrue(CodeExecutor("string s = readLine();", {}, {}).hasInputOperations)
    }

}