package miniceduapp.views

import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import miniceduapp.viewmodels.AstViewModel
import miniceduapp.views.editor.MiniCSyntaxHighlighter
import miniceduapp.views.editor.addSyntaxHighlighting
import miniceduapp.views.editor.codeEditor
import miniceduapp.views.editor.showLineNumbers
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class AstView : View("AST") {

    val viewModel: AstViewModel by inject()

    var img: ImageView by singleAssign()
    var zoomSlider: Slider by singleAssign()

    var codeArea: CodeArea by singleAssign()

    override val root = hbox(15) {
        addClass(Styles.windowContent)
        vbox(10) {
            hgrow = Priority.ALWAYS
            maxWidth = 500.0
            codeArea = codeEditor(paneOp = {
                minWidth = 300.0
                vgrow = Priority.ALWAYS
            }) {
                addSyntaxHighlighting(MiniCSyntaxHighlighter())
                //showLineNumbers() // weird bug, onDock doesn't fire if called here
                selectionProperty().onChange {
                    viewModel.setSelectedNodeFromCode(currentParagraph + 1, caretColumn)
                }
            }
            checkbox("Highlight node selected in code", viewModel.highlightSelectedNodeProperty)
        }
        vbox {
            hgrow = Priority.ALWAYS
            stackpane {
                vgrow = Priority.ALWAYS
                scrollpane {
                    img = imageview {
                        preserveRatioProperty().set(true)
                        imageProperty().bind(viewModel.astImageProperty)
                    }
                    addClass(Styles.whitePanel)
                }
                imageview("loading.gif") {
                    visibleWhen { viewModel.status.running }
                }
            }
            hbox {
                zoomSlider = slider(10.0, 600.0) {
                    hgrow = Priority.ALWAYS
                    valueProperty().onChange { value ->
                        val ratio = value / 100
                        img.scaleX = ratio
                        img.scaleY = ratio
                    }
                    value = 100.0
                }
                label {
                    bind(zoomSlider.valueProperty(), format = DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH)))
                }
                label("%")
            }
        }
    }

    init {
        viewModel.programCodeProperty.onChange {
            if (it != codeArea.text) {
                codeArea.replaceText(it)
            }
        }
    }

    override fun onDock() {
        setWindowMinSize(1100, 700)
        codeArea.showLineNumbers()

        viewModel.loadAst()
    }
}
