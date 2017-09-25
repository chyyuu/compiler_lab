package miniceduapp.views

import javafx.collections.ListChangeListener
import javafx.scene.control.IndexRange
import javafx.scene.layout.Priority
import minic.frontend.lexer.Token
import miniceduapp.viewmodels.TokensViewModel
import miniceduapp.views.editor.*
import miniceduapp.views.styles.SelectionHighlightStyles
import miniceduapp.views.styles.Styles
import org.fxmisc.richtext.CodeArea
import tornadofx.*

class TokensView : View("Lexer tokens") {
    val viewModel: TokensViewModel by inject()

    var codeArea: CodeArea by singleAssign()
    var outputArea: CodeArea by singleAssign()

    val codeSelectionHighlighter = SelectionHighlighter(SelectionHighlightStyles.selectedBlock.name)
    val outputSelectionHighlighter = SelectionHighlighter(SelectionHighlightStyles.selectedBlock.name)

    override val root = hbox(10) {
        addClass(Styles.windowContent)
        vbox {
            hgrow = Priority.ALWAYS
            codeArea = codeEditor(paneOp = {
                vgrow = Priority.ALWAYS
            }) {
                addSyntaxHighlighting(HighlightMerger(MiniCSyntaxHighlighter(), codeSelectionHighlighter))
                isEditable = false
                //showLineNumbers() // weird bug, onDock doesn't fire if called here
            }
        }
        stackpane {
            hgrow = Priority.ALWAYS
            outputArea = codeEditor(paneOp = {
                hgrow = Priority.ALWAYS
            }) {
                addSyntaxHighlighting(outputSelectionHighlighter)
                isWrapText = true
                isEditable = false
                //showLineNumbers() // weird bug, onDock doesn't fire if called here
            }
            imageview("loading.gif") {
                visibleWhen { viewModel.status.running }
            }
        }
    }

    init {
        viewModel.tokens.addListener { change: ListChangeListener.Change<out Token> ->
            val text = change.list
                    .groupBy { it.line }
                    .map { it.value.map { it.name }.joinToString(", ") }
                    .joinToString("\n")
            outputArea.replaceText(text)
        }

        viewModel.programCodeProperty.onChange {
            codeArea.replaceText(it)
        }

        viewModel.selectedTokenProperty.onChange {
            if (it != null && it.startIndex < codeArea.text.length && /* !EOF */ it.startIndex <= it.endIndex) {
                codeSelectionHighlighter.setRegions(IndexRange(it.startIndex, it.endIndex + 1))
                outputSelectionHighlighter.setRegions(outputTokenPosition(it))
            } else {
                codeSelectionHighlighter.setRegions()
                outputSelectionHighlighter.setRegions()
            }
            codeArea.updateSyntaxHighlighting()
            outputArea.updateSyntaxHighlighting()
        }

        codeArea.selectionProperty().onChange {
            if (it != null) {
                viewModel.setSelectedTokenFromCode(it.start)
            } else {
                viewModel.selectedToken = null
            }
        }
        outputArea.selectionProperty().onChange {
            if (it != null) {
                val text = outputArea.text
                val ind = text.substring(0, it.start).count { it == ' ' || it == '\n' }
                if (ind < viewModel.tokens.size) {
                    viewModel.selectedToken = viewModel.tokens[ind]
                }
            } else {
                viewModel.selectedToken = null
            }
        }
    }

    override fun onDock() {
        setWindowMinSize(800, 400)

        codeArea.showLineNumbers()
        outputArea.showLineNumbers()

        viewModel.loadTokens()
    }

    fun outputTokenPosition(token: Token): IndexRange {
        val ind = viewModel.tokens.indexOf(token)
        val text = outputArea.text
        var i = 0
        var delimitersCount = 0
        while (delimitersCount < ind) {
            if (text[i] == ' ' || text[i] == '\n') {
                delimitersCount++
            }
            i++
        }
        return IndexRange(i, i + token.name.length)
    }
}
