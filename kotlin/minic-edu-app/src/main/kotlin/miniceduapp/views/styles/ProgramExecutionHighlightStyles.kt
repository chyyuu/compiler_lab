package miniceduapp.views.styles

import javafx.scene.paint.Color
import miniceduapp.views.editor.HighlightStyle
import tornadofx.*

class ProgramExecutionHighlightStyles : Stylesheet(), HighlightStyle {

    companion object {
        val command by cssclass()
        val exception by cssclass()

        private val rtfxBackgroundColor by cssproperty<Color>("-rtfx-background-color")
    }

    override val classes: List<String>
        get() = listOf(
                command.name,
                exception.name
        )

    init {
        command {
            fill = c("#65657D")
            rtfxBackgroundColor.value = c("#ecfaeb")
        }
        exception {
            fill = Color.RED
        }
    }
}
