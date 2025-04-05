package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.modifier

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onPointerInteractionStartEnd(
    onPointerStart: () -> Unit,
    onPointerEnd: () -> Unit,
) = pointerInput(onPointerStart, onPointerEnd) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        
        onPointerStart()
        do {
            val event = awaitPointerEvent()
        } while (event.changes.any { it.pressed })
        onPointerEnd()
    }
}