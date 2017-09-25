package miniceduapp.views

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import miniceduapp.bytecodde.Instruction
import miniceduapp.bytecodde.Label
import miniceduapp.bytecodde.Value
import miniceduapp.bytecodde.WideValuePart
import miniceduapp.viewmodels.InteractiveExecutionViewModel
import miniceduapp.views.editor.*
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import tornadofx.*

class InteractiveExecutionView : View("Interactive Bytecode Execution") {
    val viewModel: InteractiveExecutionViewModel by inject()

    var codeArea: CodeArea by singleAssign()
    var bytecodeArea: CodeArea by singleAssign()
    
    val cellBorderColor = Color.LIGHTGREY

    override val root = hbox(10) {
        addClass(Styles.windowContent)
        codeArea = codeEditor(paneOp = {
            hgrow = Priority.ALWAYS
        }) {
            addSyntaxHighlighting(MiniCSyntaxHighlighter())
            isEditable = false
        }
        borderpane {
            top {
                borderpane {
                    left {
                        vbox {
                            button("Next", imageview("next.png")) {
                                shortcut("F6")
                                tooltip("Go to the next source code line (F6)")
                                command = viewModel.goToNextLineCommand
                            }
                            visibleWhen { viewModel.isExecutingProperty }
                        }
                    }
                    center {
                        vbox(5) {
                            alignment = Pos.CENTER
                            button("Execute", imageview("run.png")) {
                                shortcut("F8")
                                tooltip("Execute the program step-by-step (F8)")
                                command = viewModel.executeCodeCommand
                            }
                            button("Stop", imageview("stop.png")) {
                                shortcut("F10")
                                tooltip("Stop the program (F10)")
                                command = viewModel.stopCodeExecutionCommand
                                visibleWhen { viewModel.isExecutingProperty }
                            }
                        }
                    }
                    right {
                        vbox {
                            button("Next", imageview("next.png")) {
                                shortcut("F7")
                                tooltip("Execute next bytecode instruction (F7)")
                                command = viewModel.executeNextInstructionCommand
                            }
                            visibleWhen { viewModel.isExecutingProperty }
                        }
                    }
                }
                center {
                    hbox(5) {
                        vbox {
                            label("Variables")
                            tableview(viewModel.variables) {
                                column("#", Value::text) {
                                    isSortable = false
                                    cellFormat {
                                        text = index.toString()
                                        style {
                                            backgroundColor += Color.WHITE
                                            borderColor += box(cellBorderColor, cellBorderColor, Color.TRANSPARENT, Color.TRANSPARENT)
                                        }
                                    }
                                }
                                column("Value", Value::text) {
                                    isSortable = false
                                    cellFormat {
                                        text = it

                                        style {
                                            backgroundColor += Color.WHITE
                                            borderColor += box(if (items[index] is WideValuePart) Color.TRANSPARENT else cellBorderColor, cellBorderColor, Color.TRANSPARENT, Color.TRANSPARENT)
                                        }
                                    }
                                }

                                maxWidth = 150.0
                                vgrow = Priority.ALWAYS
                                columnResizePolicy = SmartResize.POLICY
                            }
                        }
                        vbox {
                            label("Operand stack")
                            tableview(viewModel.operandStackValues) {
                                column("Value", Value::text) {
                                    isSortable = false
                                    cellFormat {
                                        text = it

                                        style {
                                            backgroundColor += Color.WHITE
                                            borderColor += box(if (items[index] is WideValuePart) Color.TRANSPARENT else cellBorderColor, cellBorderColor, Color.TRANSPARENT, Color.TRANSPARENT)
                                        }
                                    }
                                }

                                maxWidth = 130.0
                                vgrow = Priority.ALWAYS
                                columnResizePolicy = SmartResize.POLICY
                            }
                        }
                        style {
                            paddingTop = 10
                        }
                        visibleWhen { viewModel.isExecutingProperty }
                    }
                }
            }
        }
        bytecodeArea = codeEditor(paneOp = {
            hgrow = Priority.ALWAYS
        }) {
            isEditable = false
            addSyntaxHighlighting(BytecodeHighlighter())
        }
    }

    init {
        viewModel.programCodeProperty.onChange {
            codeArea.replaceText(it)
        }
        viewModel.bytecode.onChange {
            val text = viewModel.bytecode.map { it.indent() + it.text }.joinToString("\n")
            bytecodeArea.replaceText(text)
        }

    }

    override fun onDock() {
        setWindowMinSize(900, 600)

        currentWindow?.width = 1200.0

        bytecodeArea.paragraphGraphicFactory = ArrowFactory(viewModel.nextInstructionIndexProperty)

        val numberFactory = LineNumberFactory.get(codeArea)
        val arrowFactory = ArrowFactory(viewModel.nextLineProperty.subtract(1))
        val graphicFactory = { line: Int ->
            hbox {
                opcr(this, numberFactory.apply(line))
                opcr(this, arrowFactory.apply(line))
                alignment = Pos.CENTER_LEFT
            }
        }
        codeArea.setParagraphGraphicFactory(graphicFactory)

        viewModel.nextInstructionIndexProperty.onChange {
            if (it >= 0) {
                bytecodeArea.setCursorPosition(it, 0)
                bytecodeArea.showParagraphInViewport(it + 1)
            }
        }
        viewModel.nextLineProperty.onChange {
            if (it >= 0) {
                codeArea.setCursorPosition(it - 1, 0)
                codeArea.showParagraphInViewport(it)
            }
        }

        viewModel.loadBytecode()
    }

    override fun onUndock() {
        viewModel.stopCodeExecutionCommand.execute()
    }

    fun Instruction.indent() = when (this) {
        is Label -> ""
        else -> "  "
    }
}
