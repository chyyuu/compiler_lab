package miniceduapp.views.editor

import javafx.scene.control.IndexRange
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder

class SelectionHighlighter(val className: String, vararg selectedRegions: IndexRange) : SyntaxHighlighter {

    val selectedRegions = mutableListOf(*selectedRegions)

    fun setRegions(vararg selectedRegions: IndexRange) {
        this.selectedRegions.clear()
        this.selectedRegions.addAll(selectedRegions)
    }

    override fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        val regions = selectedRegions.sortedBy { it.start }

        var lastEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        regions.forEach { region ->
            spansBuilder.add(emptySet<String>(), IndexRange(lastEnd, region.start).length)
            spansBuilder.add(setOf(className), region.length)
            lastEnd = region.end
        }
        spansBuilder.add(emptySet<String>(), IndexRange(lastEnd, text.length).length)
        return spansBuilder.create()
    }
}
