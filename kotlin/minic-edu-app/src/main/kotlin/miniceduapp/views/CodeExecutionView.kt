package miniceduapp.views

import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import miniceduapp.viewmodels.CodeExecutionViewModel
import miniceduapp.views.editor.*
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*

class CodeExecutionView : View("Execution") {
    val viewModel: CodeExecutionViewModel by inject()

    var outputArea: CodeArea by singleAssign()

    private var scrollingOutput = false // for autoscrolling to bottom via timer, doesn't work otherwise, not sure why

    override val root = vbox(10) {
        addClass(Styles.windowContent)
        vbox {
            vgrow = Priority.ALWAYS
            label("Output")
            outputArea = codeEditor(paneOp = {
                vgrow = Priority.ALWAYS
            }) {
                addClass(Styles.outputArea)
                isEditable = false
                addSyntaxHighlighting(ProgramExecutionHighlighter())
            }
        }
        hbox(10) {
            label("Input") {
                style {
                    alignment = Pos.CENTER_LEFT
                }
            }
            textfield(viewModel.inputProperty) {
                hgrow = Priority.ALWAYS
                setOnKeyPressed {
                    if (it.code == KeyCode.ENTER) {
                        viewModel.writeInputCommand.execute()
                    }
                }
            }
            button("Enter") {
                command = viewModel.writeInputCommand
            }
            removeWhen { viewModel.isExecutingProgramProperty.not().or(viewModel.hasInputOperationsProperty.not()) }
        }
        button("Close") {
            setOnAction {
                close()
            }
            removeWhen { viewModel.isExecutingProgramProperty }
        }
    }

    init {
        viewModel.outputProperty.onChange {
            outputArea.replaceText(it)

            // autoscroll
            if (!scrollingOutput) {
                scrollingOutput = true
                runLater(10.millis) {
                    outputArea.scrollBy(Point2D(0.0, Double.MAX_VALUE))
                    scrollingOutput = false
                }
            }
        }

        viewModel.isExecutingProgramProperty.onChange {
            runLater(100.millis) {
                outputArea.scrollBy(Point2D(0.0, Double.MAX_VALUE))
            }
        }
    }

    override fun onDock() {
        setWindowMinSize(550, 200)

        currentStage?.height = 300.0
    }

    override fun onUndock() {
        viewModel.stopCodeExecutionCommand.execute()
    }
}
