package miniceduapp.views.editor

import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.util.regex.Pattern

open class RegexSyntaxHighlighter(val regex: Pattern, val style: HighlightStyle) : SyntaxHighlighter {

    override fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        return computeHighlightingByRegex(text, regex)
    }

    protected fun computeHighlightingByRegex(text: String, regex: Pattern): StyleSpans<Collection<String>> {
        val matcher = regex.matcher(text)
        var lastKwEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        while (matcher.find()) {
            val styleClass = style.classes.first { matcher.group(it.toUpperCase()) != null }
            spansBuilder.add(emptyList<String>(), matcher.start() - lastKwEnd)
            spansBuilder.add(setOf(styleClass), matcher.end() - matcher.start())
            lastKwEnd = matcher.end()
        }
        spansBuilder.add(emptyList<String>(), text.length - lastKwEnd)
        return spansBuilder.create()
    }
}