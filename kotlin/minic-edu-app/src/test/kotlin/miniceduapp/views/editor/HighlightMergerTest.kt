package miniceduapp.views.editor

import org.fxmisc.richtext.model.StyleSpansBuilder
import org.junit.Test
import kotlin.test.*

class HighlightMergerTest {

    @Test
    fun mergesHighlighters() {
        val h1 = object : SyntaxHighlighter {
            override fun computeHighlighting(text: String) = StyleSpansBuilder<Collection<String>>().apply {
                    add(setOf("class1"), 5)
                    add(emptySet<String>(), 1)
                    add(setOf("class2"), 8)
                    add(setOf("class3"), 10)
                }.create()
        }
        val h2 = object : SyntaxHighlighter {
            override fun computeHighlighting(text: String) = StyleSpansBuilder<Collection<String>>().apply {
                add(emptySet<String>(), 6)
                add(setOf("special-class"), 8)
                add(emptySet<String>(), 10)
            }.create()
        }
        val expected = StyleSpansBuilder<Collection<String>>().apply {
            add(setOf("class1"), 5)
            add(emptySet<String>(), 1)
            add(setOf("class2", "special-class"), 8)
            add(setOf("class3"), 10)
        }.create()

        val result = HighlightMerger(h1, h2).computeHighlighting("")

        assertEquals(expected, result)
    }
}
