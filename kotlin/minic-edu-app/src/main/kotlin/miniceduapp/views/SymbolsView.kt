package miniceduapp.views

import javafx.scene.layout.Priority
import minic.frontend.scope.Symbol
import miniceduapp.viewmodels.SymbolsViewModel
import miniceduapp.views.editor.*
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*

class SymbolsView : View("Symbols") {
    val viewModel: SymbolsViewModel by inject()

    var codeArea: CodeArea by singleAssign()

    override val root = hbox(10) {
        addClass(Styles.windowContent)
        codeArea = codeEditor(paneOp = {
            hgrow = Priority.ALWAYS
        }) {
            addSyntaxHighlighting(MiniCSyntaxHighlighter())
            isEditable = false
            selectionProperty().onChange {
                viewModel.setSelectedNodeFromCode(currentParagraph + 1)
            }
        }
        vbox {
            label("Variables available at selected line")
            tableview(viewModel.symbols) {
                column("Type", Symbol::type).cellFormat {
                    text = it.name
                }
                column("Name", Symbol::name)

                placeholder = label("")
                vgrow = Priority.ALWAYS
                columnResizePolicy = SmartResize.POLICY
            }
        }
    }

    init {
        viewModel.programCodeProperty.onChange {
            codeArea.replaceText(it)
        }
    }

    override fun onDock() {
        setWindowMinSize(800, 600)

        codeArea.showLineNumbers()

        viewModel.loadSymbols()
    }
}
