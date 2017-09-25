package miniceduapp.views.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val windowContent by cssclass()
        val arrowLabel by cssclass()
        val whitePanel by cssclass()
        val iconButton by cssclass()
        val modifiedInput by cssclass()
        val outputArea by cssclass()

        private val paragraphBox by cssclass("paragraph-box")
        private val hasCaret by csspseudoclass("has-caret")
    }

    init {
        windowContent {
            padding = box(10.px)
        }
        arrowLabel {
            fontSize = 1.6.em
            fontWeight = FontWeight.BOLD
        }

        whitePanel {
            backgroundColor = multi(Color.WHITE)
            viewport {
                backgroundColor = multi(Color.TRANSPARENT)
            }
        }

        paragraphBox {
            and(hasCaret) {
                backgroundColor += c("#f2f9fc")
                backgroundInsets += box(3.px, 3.px, 0.px, 0.px)
            }
        }

        outputArea {
            paragraphBox {
                and(hasCaret) {
                    backgroundColor += Color.TRANSPARENT
                }
            }
        }

        val iconButtonBorder = mixin {
            padding = box(1.px)
            borderColor += box(c(100, 100, 100))
        }

        iconButton {
            padding = box(2.px)
            backgroundColor += Color.TRANSPARENT
            and(hover) {
                backgroundColor += Color.LIGHTGRAY
                +iconButtonBorder
            }
            and(pressed) {
                backgroundColor += Color.DARKGRAY
                +iconButtonBorder
            }
        }

        modifiedInput {
            textFill = Color.BLUE
        }
    }
}

