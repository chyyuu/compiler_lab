package miniceduapp.views.events

import tornadofx.*

data class FileExtensionFilter(val description: String, val extensions: List<String>)

class RequestFilePathEvent(val filters: List<FileExtensionFilter>, var result: String? = null) : FXEvent()
