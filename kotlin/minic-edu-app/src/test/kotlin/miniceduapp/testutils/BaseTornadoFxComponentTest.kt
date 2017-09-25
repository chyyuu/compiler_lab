package miniceduapp.testutils

import miniceduapp.views.events.ErrorEvent
import miniceduapp.views.events.ErrorMessageEvent
import org.junit.After
import tornadofx.*
import java.util.*

open class BaseTornadoFxComponentTest : Component() {
    private var ignoreErrors = false
    private val events = mutableListOf<FXEventRegistration>()

    private val errors = Collections.synchronizedCollection(mutableListOf<Throwable>())

    init {
        events += subscribe<ErrorEvent> {
            if (!ignoreErrors) {
                errors.add(it.error)
                throw it.error
            }
        }
        events += subscribe<ErrorMessageEvent> {
            if (!ignoreErrors) {
                errors.add(Exception(it.text))
                throw Exception(it.text)
            }
        }
    }

    @After
    fun afterTest() {
        events.forEach {
            it.unsubscribe()
        }
        events.clear()

        if (errors.any()) {
            throw errors.first()
        }
    }

    protected fun ignoreErrors(op: () -> Unit) {
        ignoreErrors = true
        try {
            op()
        } finally {
            ignoreErrors = false
        }
    }
}
