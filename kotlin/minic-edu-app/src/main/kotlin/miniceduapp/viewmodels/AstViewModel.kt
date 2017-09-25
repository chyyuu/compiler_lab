package miniceduapp.viewmodels

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.util.Duration
import minic.Compiler
import minic.backend.info.tree.NodeStyle
import minic.backend.info.tree.TreePainter
import minic.frontend.ast.AstNode
import minic.frontend.ast.Program
import minic.frontend.ast.StatementsBlock
import miniceduapp.views.events.ErrorEvent
import tornadofx.*
import java.awt.Color

class AstViewModel(val updateDelay: Duration = 2.seconds) : ViewModel() {
    val mainViewModel: MainViewModel by inject()

    val status = TaskStatus()

    val astImageProperty = SimpleObjectProperty<Image>()
    var astImage: Image by astImageProperty

    val astProperty = SimpleObjectProperty<Program>()
    var ast: Program by astProperty

    val selectedAstNodeProperty = SimpleObjectProperty<AstNode>()
    var selectedAstNode: AstNode? by selectedAstNodeProperty

    private val _programCodeProperty = ReadOnlyStringWrapper("")
    private var _programCode by _programCodeProperty
    val programCodeProperty: ReadOnlyStringProperty get() = _programCodeProperty.readOnlyProperty
    val programCode: String get() = _programCodeProperty.value

    val highlightSelectedNodeProperty = SimpleBooleanProperty(true)
    var highlightSelectedNode by highlightSelectedNodeProperty

    private var timerTask: FXTimerTask? = null

    init {
        mainViewModel.programCodeProperty.onChange {
            timerTask?.cancel()

            timerTask = runLater(updateDelay) {
                loadAst()
            }
        }
        selectedAstNodeProperty.onChange {
            loadAst()
        }
        highlightSelectedNodeProperty.onChange {
            loadAst()
        }

        loadAst()
    }

    fun loadAst() {
        timerTask?.cancel()

        mainViewModel.validateCode()
        if (mainViewModel.hasParsingErrors) {
            return
        }

        val code = mainViewModel.programCode

        if (status.running.value) {
            return
        }

        val painter = if (selectedAstNode != null && highlightSelectedNode) {
            object : TreePainter {
                override fun paintNode(node: AstNode): NodeStyle {
                    if (node == selectedAstNode) {
                        return NodeStyle(fillColor = Color.yellow)
                    }
                    return super.paintNode(node)
                }
            }
        } else null

        runAsync(status) {
            val compiler = Compiler(code)
            SwingFXUtils.toFXImage(compiler.drawAst(painter), null) to compiler.ast
        } ui {
            astImage = it.first
            ast = it.second
            if (_programCode != code) {
                _programCode = code
            }
        } fail {
            fire(ErrorEvent(it))
        }
    }

    fun setSelectedNodeFromCode(cursorLine: Int, cursorCol: Int) {
        var node: AstNode? = null
        ast.process {
            if (it !is Program && it !is StatementsBlock) {
                if (cursorLine == it.position!!.start.line && cursorCol >= it.position!!.start.column && cursorCol <= it.position!!.end.column) {
                    node = it
                }
            }
        }
        selectedAstNode = node
    }

}
