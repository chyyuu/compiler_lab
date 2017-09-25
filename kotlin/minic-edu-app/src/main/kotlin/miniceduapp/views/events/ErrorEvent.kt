package miniceduapp.views.events

import tornadofx.*

class ErrorEvent(val error: Throwable, val text: String? = null) : FXEvent()

class ErrorMessageEvent(val text: String) : FXEvent()
