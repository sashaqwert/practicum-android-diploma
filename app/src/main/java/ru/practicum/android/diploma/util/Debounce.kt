package ru.practicum.android.diploma.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun <T> debounceFun(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    action: suspend (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null

    return { value ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(delayMillis.milliseconds)
            action(value)
        }
    }
}
