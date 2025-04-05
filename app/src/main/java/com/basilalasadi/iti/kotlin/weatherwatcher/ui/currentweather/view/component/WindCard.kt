package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.metersPerSecond

data class WindCardData(
    val windSpeed: Units.Speed,
    val windGust: Units.Speed,
    @StringRes val speedUnit: Int,
    val directionDegrees: Double,
    @StringRes val generalDirection: Int,
) {
    companion object {
        val preview = WindCardData(
            windSpeed = 12.0.metersPerSecond,
            windGust = 22.0.metersPerSecond,
            speedUnit = R.string.meters_per_second,
            directionDegrees = 33.0,
            generalDirection = R.string.direction_north_east,
        )
    }
}

@Composable
fun WindCard(modifier: Modifier, data: WindCardData) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_strong_wind),
        label = R.string.lbl_wind,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row {
                    Text(
                        text = "Wind",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = "%.1f %s".format(data.windSpeed.value, stringResource(data.speedUnit)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                HorizontalDivider()
                
                Row {
                    Text(
                        text = "Gusts",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = "%.1f %s".format(data.windGust.value, stringResource(data.speedUnit)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                HorizontalDivider()
                
                Row {
                    Text(
                        text = "Direction",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = "%.0f° %s".format(data.directionDegrees, stringResource(data.generalDirection)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            WindDirectionIndicator(
                data = data,
                modifier = Modifier
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun WindDirectionIndicator(modifier: Modifier = Modifier, data: WindCardData) {
    val compass = painterResource(R.drawable.compass)
    val arrow = painterResource(R.drawable.compass_arrow)
    val contentColor = LocalContentColor.current
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            with(compass) {
                draw(
                    size = size,
                    alpha = 0.25f,
                    colorFilter = ColorFilter.tint(contentColor)
                )
            }
            
            with(arrow) {
                rotate(data.directionDegrees.toFloat()) {
                    draw(
                        size = size,
                        alpha = 0.85f,
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                }
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "%.1f".format(data.windSpeed.value),
                style = MaterialTheme.typography.titleMedium
                    .copy(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Bottom,
                            trim = LineHeightStyle.Trim.Both,
                        )
                    )
            )
            Text(
                text = stringResource(data.speedUnit),
                style = MaterialTheme.typography.bodySmall
                    .copy(
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Top,
                            trim = LineHeightStyle.Trim.Both,
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    WindCard(
        data = WindCardData.preview,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .aspectRatio(2f)
    )
}
