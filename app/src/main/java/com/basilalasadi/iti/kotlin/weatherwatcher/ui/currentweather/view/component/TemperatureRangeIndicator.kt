package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.celsius
import kotlin.math.exp

@Composable
fun TemperatureRangeIndicator(
    modifier: Modifier = Modifier,
    lowTemperature: Units.Temperature,
    highTemperature: Units.Temperature,
    minTemperature: Units.Temperature,
    maxTemperature: Units.Temperature,
) {
    val lowTempCelsius = lowTemperature.toCelsius().toFloat()
    val highTempCelsius = highTemperature.toCelsius().toFloat()
    val minTempCelsius = minTemperature.toCelsius().toFloat()
    val maxTempCelsius = maxTemperature.toCelsius().toFloat()
    
    val lowTempScale = ((lowTempCelsius - minTempCelsius) / (maxTempCelsius - minTempCelsius)).toFloat()
    val highTempScale = ((highTempCelsius - minTempCelsius) / (maxTempCelsius - minTempCelsius)).toFloat()

    val colorStops = remember<Array<Pair<Float, Color>>>(lowTempCelsius, highTempCelsius) {
        val numSteps = ((highTempCelsius - lowTempCelsius) / 5).toInt().coerceIn(4..20)
        
        linearSpace(lowTempCelsius, highTempCelsius, numSteps)
            { x, f ->
                (lowTempScale + x * (highTempScale - lowTempScale)) to temperatureColor(f)
            }
            .toList()
            .toTypedArray()
    }
    
    val frontBrush = Brush.Companion.horizontalGradient(colorStops = colorStops)
    val backColor = MaterialTheme.colorScheme.surfaceDim
    
    Canvas(
        modifier = modifier
            .height(5.dp)
    ) {
        drawLine(
            start = Offset(x = 0f, y = size.height / 2),
            end = Offset(x = size.width, y = size.height / 2),
            strokeWidth = 5.dp.toPx(),
            color = backColor,
            cap = StrokeCap.Companion.Round,
        )
        drawRoundRect(
            brush = frontBrush,
            topLeft = Offset(size.width * lowTempScale, 0f),
            size = Size(size.width * (highTempScale - lowTempScale), 5.dp.toPx()),
            cornerRadius = CornerRadius(2.5.dp.toPx()),
        )
    }
}

private fun temperatureColor(celsius: Float): Color {
    val x = Math.clamp(celsius, -50f, 50f)
    
    val hue = 215 / (1 + exp(0.27f * (x - 24f)))
    val saturation = if (x < 30) { 1 - (x + 50) / 160 } else { 1f }
    val value = if (x < 30) { 0.35f + 0.65f * (x + 50) / 80 } else { (55 - x) / 25 }
    
    return Color.hsv(hue, saturation, value)
}

private fun<T> linearSpace(
    start: Float,
    end: Float,
    numPoints: Int,
    mapper: (x: Float, f: Float) -> T,
) = sequence<T> {
    val step = 1f / (numPoints - 1).coerceAtLeast(1)
    var x = 0f
    
    repeat(numPoints) {
        val f = (end - start) * x + start
        yield(mapper(x, f))
        x += step
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TemperatureRangeIndicator(
        lowTemperature = (22.0).celsius,
        highTemperature = 33.0.celsius,
        minTemperature = (12.0).celsius,
        maxTemperature = 33.0.celsius,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
