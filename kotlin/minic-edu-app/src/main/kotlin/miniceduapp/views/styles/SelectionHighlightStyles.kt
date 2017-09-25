package miniceduapp.views.styles

import javafx.scene.paint.Color
import miniceduapp.views.editor.HighlightStyle
import tornadofx.*

class SelectionHighlightStyles : Stylesheet(), HighlightStyle {

    companion object {
        val selectedBlock by cssclass()

        private val rtfxBackgroundColor by cssproperty<Color>("-rtfx-background-color")
    }

    override val classes: List<String>
        get() = listOf(
                selectedBlock.name
        )

    init {
        selectedBlock {
            rtfxBackgroundColor.value = Color.YELLOW
        }
    }
}
