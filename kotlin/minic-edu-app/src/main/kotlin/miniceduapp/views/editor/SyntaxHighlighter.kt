package miniceduapp.views.editor

import org.fxmisc.richtext.model.StyleSpans

interface SyntaxHighlighter {
    fun computeHighlighting(text: String): StyleSpans<Collection<String>>
}
