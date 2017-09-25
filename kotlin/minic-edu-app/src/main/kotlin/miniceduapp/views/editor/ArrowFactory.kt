package miniceduapp.views.editor

import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import org.reactfx.value.Val
import java.util.function.IntFunction

internal class ArrowFactory(private val shownLine: ObservableValue<Number>) : IntFunction<Node> {

    override fun apply(lineNumber: Int): Node {
        val triangle = Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0)
        triangle.fill = Color.GREEN

        val visible = Val.map(shownLine) { it == lineNumber }

        triangle.visibleProperty().bind(
                Val.flatMap<Scene, Boolean>(triangle.sceneProperty()) { scene -> if (scene != null) visible else Val.constant(false) })

        return triangle
    }
}
