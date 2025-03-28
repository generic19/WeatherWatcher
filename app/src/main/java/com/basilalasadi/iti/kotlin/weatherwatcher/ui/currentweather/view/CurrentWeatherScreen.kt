package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Preview(showSystemUi = true)
@Composable
fun CurrentWeatherScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        CurrentWeatherDisplay(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        HourlyForecastCard(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )

        DailyForecastCard(
            Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )

        LabeledCard(
            iconVector = Icons.Outlined.LocationOn,
            label = "TEMPERATURE MAP",
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
                .aspectRatio(1f)
        ) { }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            LabeledCard(
                iconVector = Icons.Outlined.Notifications,
                label = "CLOUDS",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) { }
            LabeledCard(
                iconVector = Icons.Outlined.CheckCircle,
                label = "PRECIPITATION",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) { }
        }

        LabeledCard(
            iconVector = Icons.Outlined.Notifications,
            label = "WIND",
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
                .aspectRatio(2f)
        ) { }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            LabeledCard(
                iconVector = Icons.Outlined.Notifications,
                label = "HUMIDITY",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) { }
            LabeledCard(
                iconVector = Icons.Outlined.CheckCircle,
                label = "PRESSURE",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) { }
        }
    }
}

@Composable
private fun DailyForecastCard(modifier: Modifier) {
    LabeledCard(
        iconVector = Icons.Outlined.DateRange,
        label = "DAILY FORECAST",
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
        ) {
            listOf<Triple<String, Float, Float>>(
                Triple("Saturday", 12f, 19f),
                Triple("Sunday", 13f, 21f),
                Triple("Monday", 22f, 33f),
                Triple("Tuesday", 14f, 22f),
                Triple("Wednesday", 13f, 20f),
            ).forEach { item ->
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(2f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.weight(0.25f))
                    Text(
                        text = "${item.second}°C",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    TemperatureRangeIndicator(
                        lowTempCelsius = item.second,
                        highTempCelsius = item.third,
                        minTempCelsius = 12f,
                        maxTempCelsius = 33f,
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 12.dp)
                    )
                    Text(
                        text = "${item.third}°C",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

val temperatureColors = arrayOf(
    Color.hsv(215f, 1f, 0.35f),
    Color.hsv(215f, 1f, 0.4f),
    Color.hsv(215f, 1f, 0.425f),
    Color.hsv(215f, 1f, 0.45f),
    Color.hsv(215f, 1f, 0.475f),
    Color.hsv(215f, 0.9f, 0.5f),
    Color.hsv(215f, 0.9f, 0.525f),
    Color.hsv(215f, 0.9f, 0.55f),
    Color.hsv(215f, 0.9f, 0.575f),
    Color.hsv(215f, 0.8f, 0.6f),
    Color.hsv(215f, 0.8f, 0.675f),
    Color.hsv(215f, 0.8f, 0.65f),
    Color.hsv(215f, 0.8f, 0.6f),
    Color.hsv(215f, 0.8f, 0.7f),
    Color.hsv(215f, 0.75f, 0.8f),
    Color.hsv(215f, 0.75f, 0.9f),
    Color.hsv(210f, 0.75f, 0.9f),
    Color.hsv(205f, 0.75f, 0.9f),
    Color.hsv(190f, 0.7f, 0.9f),
    Color.hsv(185f, 0.7f, 0.9f),
    Color.hsv(150f, 0.7f, 0.9f),
    Color.hsv(100f, 0.7f, 0.9f),
    Color.hsv(53f, 0.7f, 1f),
    Color.hsv(45f, 0.5f, 1f),
    Color.hsv(30f, 0.7f, 1f),
    Color.hsv(0f, 0.7f, 1f),
    Color.hsv(0f, 0.7f, 0.6f),
    Color.hsv(0f, 1f, 0.5f),
    Color.hsv(0f, 1f, 0.2f),
)

@Composable
fun TemperatureRangeIndicator(
    modifier: Modifier = Modifier,
    lowTempCelsius: Float,
    highTempCelsius: Float,
    minTempCelsius: Float,
    maxTempCelsius: Float,
) {
    val temperatureToScale = remember<(Float) -> Float>(minTempCelsius, maxTempCelsius) {
        { t -> (t - minTempCelsius) / (maxTempCelsius - minTempCelsius) }
    }

    val fractionToScale = remember<(Float) -> Float>(minTempCelsius, maxTempCelsius) {
        { x -> (100 * x - minTempCelsius - 50) / (maxTempCelsius - minTempCelsius) }
    }

    val colorStops = remember(minTempCelsius, maxTempCelsius) {
        temperatureColors.mapIndexed { i, color ->
            val x = i / (temperatureColors.size - 1).toFloat()
            val s = fractionToScale(x)

            s to color
        }
    }

    val lowScale = remember(minTempCelsius, maxTempCelsius, lowTempCelsius) { temperatureToScale(lowTempCelsius) }
    val highScale = remember(minTempCelsius, maxTempCelsius, highTempCelsius) { temperatureToScale(highTempCelsius) }

    val frontBrush = Brush.horizontalGradient(
        colorStops = colorStops.toTypedArray(),
    )

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
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            brush = frontBrush,
            topLeft = Offset(size.width * lowScale, 0f),
            size = Size(size.width * (highScale - lowScale), 5.dp.toPx()),
            cornerRadius = CornerRadius(2.5.dp.toPx()),
        )

    }
}

@Composable
private fun CurrentWeatherDisplay(
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(vertical = 32.dp)
    ) {
        Text(
            text = "Cairo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .alpha(0.7f)
        )
        Row {
            Text(
                text = "28",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Text(
                text = "°C",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.6f)
            )
            Text(
                text = "Mostly Clear",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .alpha(0.6f)
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Low: ",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alpha(0.8f)
            )
            Text(
                text = "16°C",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
            Text(
                text = "High: ",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alpha(0.8f)
            )
            Text(
                text = "28°C",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
            )
        }
        Text(
            text = "April 5, 2025 – 8:59 AM",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .alpha(0.4f)
        )
    }
}

@Composable
private fun LabeledCard(
    modifier: Modifier = Modifier,
    label: String,
    iconVector: ImageVector,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
            alpha = 0.5f
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .alpha(0.6f)
                        .alignByBaseline()
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .alpha(0.6f)
                        .alignByBaseline()
                )
            }
            content()
        }
    }
}

@Composable
private fun HourlyForecastCard(modifier: Modifier = Modifier) {
    LabeledCard(
        modifier = modifier,
        label = "HOURLY FORECAST",
        iconVector = Icons.Outlined.Info,
    ) {
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            modifier = Modifier
        ) {
            itemsIndexed(
                items = listOf<Pair<String, String>>(
                    "12°C" to "Now",
                    "12°C" to "12 AM",
                    "13°C" to "3 AM",
                    "15°C" to "6 AM",
                    "17°C" to "9 AM",
                    "21°C" to "12 PM",
                    "19°C" to "3 PM",
                    "18°C" to "6 PM",
                    "15°C" to "9 PM",
                ),
            ) { _, item ->
                HourlyWeatherItem(item.first, item.second)
            }
        }
    }
}

@Composable
private fun HourlyWeatherItem(temperature: String, time: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
        )
        Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .alpha(0.9f)
        )
        Text(
            text = temperature,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
    }
}