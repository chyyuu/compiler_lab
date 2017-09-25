package miniceduapp.views.editor

import javafx.scene.control.IndexRange
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.junit.Test
import kotlin.test.*

class SelectionHighlighterTest {

    @Test
    fun highlightsRegions() {
        val text = "hello world! qwerty asdf"

        val result = SelectionHighlighter("class1", IndexRange(6, 11), IndexRange(13, 19)).computeHighlighting(text)

        val expected = StyleSpansBuilder<Collection<String>>().apply {
            add(emptySet<String>(), 6)
            add(setOf("class1"), 5)
            add(emptySet<String>(), 2)
            add(setOf("class1"), 6)
            add(emptySet<String>(), 5)
        }.create()

        assertEquals(expected, result)
    }

    @Test
    fun highlightsRegionsAtBeginEnd() {
        val text = "hello world! qwerty asdf"

        val result = SelectionHighlighter("class1", IndexRange(0, 5), IndexRange(20, 24)).computeHighlighting(text)

        val expected = StyleSpansBuilder<Collection<String>>().apply {
            add(setOf("class1"), 5)
            add(emptySet<String>(), 15)
            add(setOf("class1"), 4)
        }.create()

        assertEquals(expected, result)
    }

    @Test
    fun handlesWhenNoRegions() {
        val text = "hello world! qwerty asdf"

        val result = SelectionHighlighter("class1").computeHighlighting(text)

        val expected = StyleSpansBuilder<Collection<String>>().apply {
            add(emptySet<String>(), 24)
        }.create()

        assertEquals(expected, result)
    }

    @Test
    fun canChangeRegions() {
        val text = "hello world! qwerty asdf"

        val highlighter = SelectionHighlighter("class1", IndexRange(6, 11), IndexRange(13, 19))

        val result = highlighter.computeHighlighting(text)

        val expected = StyleSpansBuilder<Collection<String>>().apply {
            add(emptySet<String>(), 6)
            add(setOf("class1"), 5)
            add(emptySet<String>(), 2)
            add(setOf("class1"), 6)
            add(emptySet<String>(), 5)
        }.create()

        assertEquals(expected, result)

        highlighter.setRegions(IndexRange(0, 5), IndexRange(20, 24))

        val result2 = highlighter.computeHighlighting(text)

        val expected2 = StyleSpansBuilder<Collection<String>>().apply {
            add(setOf("class1"), 5)
            add(emptySet<String>(), 15)
            add(setOf("class1"), 4)
        }.create()

        assertEquals(expected2, result2)
    }
}
