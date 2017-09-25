package miniceduapp.views.events

import tornadofx.*
import kotlin.reflect.KClass

class OpenWindowEvent<T: View>(val windowClass: KClass<T>) : FXEvent()
