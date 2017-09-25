package miniceduapp.helpers

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import tornadofx.*
import javafx.scene.layout.Pane



fun <T> TableView<T>.setOnRowDoubleClick(op: (T) -> Unit) {
    setRowFactory {
        val row = TableRow<T>()
        row.setOnMouseClicked {
            if (it.clickCount == 2 && !row.isEmpty) {
                op(row.item)
            }
        }
        row
    }
}

fun <T> TableView<T>.hideHeader() {
    widthProperty().onChange {
        val header = lookup("TableHeaderRow") as? Pane
        if (header != null && header.isVisible) {
            header.apply {
                maxHeight = 0.0
                minHeight = 0.0
                prefHeight = 0.0
                isVisible = false
            }
        }
    }
}

