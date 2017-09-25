package miniceduapp.views.editor


import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.scene.Node
import org.fxmisc.flowless.Virtualized
import org.fxmisc.flowless.VirtualizedScrollPane

class VirtualizedScrollPaneExt<T>(content: T) : VirtualizedScrollPane<T>(content) where T : Node, T : Virtualized {
    private val FOCUSED = PseudoClass.getPseudoClass("focused")

    init {
        content.focusedProperty().addListener { _, _, newVal -> pseudoClassStateChanged(FOCUSED, newVal!!) }
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val ins = insets
        content.resizeRelocate(ins.left, ins.top, width - ins.left - ins.right, height - ins.top - ins.bottom)
    }
}
