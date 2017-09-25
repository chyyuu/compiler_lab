package miniceduapp

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import minic.Compiler
import minic.frontend.ast.InputFunction
import org.apache.commons.io.FilenameUtils
import tornadofx.*
import java.io.File
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer

/**
 * Compiles and executes Mini-C program
 * All callbacks are executed in the FX thread
 */
class CodeExecutor(val code: String,
                   val onOutput: CodeExecutor.(String) -> Unit,
                   val onFail: CodeExecutor.(Throwable) -> Unit,
                   val onFinish: CodeExecutor.() -> Unit = {},
                   val onCompiled: CodeExecutor.() -> Unit = {}) {

    companion object {
        private val javaPath: String

        init {
            val binDir = System.getProperty("java.home") + "/bin/"
            if (File(binDir).exists()) {
                javaPath = binDir + "java"
            } else {
                javaPath = "java"
            }
        }
    }

    private val _isFinishedProperty = ReadOnlyBooleanWrapper(false)
    private var _isFinished by _isFinishedProperty
    val isFinishedProperty: ReadOnlyBooleanProperty get() = _isFinishedProperty.readOnlyProperty
    @Suppress("unused")
    val isFinished: Boolean get() = _isFinishedProperty.value

    private var process: Process? = null

    private var inputList = Collections.synchronizedList(mutableListOf<String>())

    fun start() {
        _isFinished = false

        thread(start = true, isDaemon = true) {
            try {
                val programFilePath = Files.createTempFile("minicProgram", ".class").toString()

                compile(programFilePath)

                runLater { onCompiled() }

                run(programFilePath)
            } catch (ex: Throwable) {
                runLater { onFail(ex) }
            }
            runLater {
                _isFinished = true
                onFinish()
            }
        }
    }

    @Synchronized
    fun stop() {
        process?.destroy()
    }

    @Synchronized
    fun writeInput(input: String) {
        inputList.add(input)
    }

    val hasInputOperations: Boolean by lazy {
        var result = false
        Compiler(code).ast.process {
            if (it is InputFunction) {
                result = true
            }
        }
        result
    }

    private fun compile(outputFilePath: String) {
        Compiler(code).compile(outputFilePath)
    }

    private fun run(programFilePath: String) {
        val pb = ProcessBuilder(javaPath, FilenameUtils.getBaseName(programFilePath))
        pb.directory(File(FilenameUtils.getFullPath(programFilePath)))
        pb.redirectErrorStream(true)

        process = pb.start()

        val inputUpdater = if (hasInputOperations) {
            timer(daemon = true, period = 30.millis.toMillis().toLong()) {
                writeInput()
            }
        } else {
            null
        }

        try {
            while (true) {
                // TODO: should support unicode (non-latin)?
                val byte = process!!.inputStream.read()
                if (byte < 0) {
                    break
                }
                val str = byte.toChar().toString()
                if (str != "\r") {
                    runLater { onOutput(str) }
                }
            }
        } finally {
            inputUpdater?.cancel()
        }
    }

    @Synchronized
    private fun writeInput() {
        with(OutputStreamWriter(process!!.outputStream)) {
            write(inputList.joinToString(""))
            flush()
        }
        inputList.clear()
    }
}