package miniceduapp.views.editor

import javafx.event.EventTarget
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import tornadofx.*

fun EventTarget.codeEditor(paneOp: (VirtualizedScrollPane<CodeArea>.() -> Unit)? = null, op: (CodeArea.() -> Unit)? = null): CodeArea {
    val codeArea = CodeArea()
    val scrollPane = VirtualizedScrollPaneExt(codeArea)

    addChildIfPossible(scrollPane)

    paneOp?.invoke(scrollPane)
    op?.invoke(codeArea)

    return codeArea
}

fun CodeArea.showLineNumbers() {
    paragraphGraphicFactory = LineNumberFactory.get(this)
}

fun CodeArea.addSyntaxHighlighting(syntaxHighlighter: SyntaxHighlighter) {
    richChanges()
            .filter { ch -> ch.inserted != ch.removed}
            .subscribe {
                setStyleSpans(0, syntaxHighlighter.computeHighlighting(text))
            }
}

fun CodeArea.updateSyntaxHighlighting() {
    clearStyle(0, text.length)
}

fun CodeArea.setCursorPosition(line: Int, col: Int) {
    selectRange(line, col, line, col)
}
