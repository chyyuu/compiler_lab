package miniceduapp.views.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import miniceduapp.views.editor.HighlightStyle
import tornadofx.*

class BytecodeHighlightStyles : Stylesheet(), HighlightStyle {

    companion object {
        val bytecodelabel by cssclass()
    }

    override val classes: List<String>
        get() = listOf(
                bytecodelabel.name
        )

    init {
        bytecodelabel {
            fill = Color.BLUE
            fontWeight = FontWeight.BOLD
        }
    }
}
