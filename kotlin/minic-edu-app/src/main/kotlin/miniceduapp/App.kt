package miniceduapp

import javafx.application.Application
import miniceduapp.views.MainView
import miniceduapp.views.styles.*
import tornadofx.*


class MinicEduApp: App(MainView::class,
        Styles::class, CodeHighlightStyles::class, ProgramExecutionHighlightStyles::class, SelectionHighlightStyles::class, BytecodeHighlightStyles::class)

fun main(args: Array<String>) {
    importStylesheet("/richtextfx.css")

    Application.launch(MinicEduApp::class.java, *args)
}
