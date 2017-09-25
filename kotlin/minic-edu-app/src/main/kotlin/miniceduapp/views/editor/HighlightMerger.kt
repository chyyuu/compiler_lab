package miniceduapp.views.editor

import org.fxmisc.richtext.model.StyleSpans

class HighlightMerger(val highlighters: List<SyntaxHighlighter>) : SyntaxHighlighter {
    constructor(vararg highlighters: SyntaxHighlighter) : this(highlighters.toList())

    init {
        assert(highlighters.isNotEmpty())
    }

    override fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        return highlighters.fold(highlighters.first().computeHighlighting(text)) { acc, highligher ->
            acc.overlay(highligher.computeHighlighting(text)) { t, u ->
                setOf<String>(*t.toTypedArray(), *u.toTypedArray())
            }
        }
    }
}
