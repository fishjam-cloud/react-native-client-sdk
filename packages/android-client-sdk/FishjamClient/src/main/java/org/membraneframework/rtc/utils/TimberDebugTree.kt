package org.membraneframework.rtc.utils

import timber.log.Timber

class TimberDebugTree : Timber.DebugTree() {
    private val CALL_STACK_INDEX = 5

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        val stackTrace = Throwable().stackTrace
        if (stackTrace.size <= CALL_STACK_INDEX) {
            super.log(priority, tag, message, t)
            return
        }
        val stackTraceElement = stackTrace[CALL_STACK_INDEX]
        super.log(
            priority,
            tag,
            "In file: ${stackTraceElement.fileName} in method: ${stackTraceElement.methodName}" +
                " on line ${stackTraceElement.lineNumber}: $message",
            t
        )
    }
}
