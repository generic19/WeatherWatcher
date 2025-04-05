package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R

@Composable
fun HumidityCard(modifier: Modifier, humidity: String) {
    InfoCard(
        modifier = modifier,
        icon = R.drawable.wi_humidity,
        label = R.string.lbl_humidity,
        title = humidity,
        subtitle = stringResource(R.string.lbl_saturation),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    HumidityCard(
        humidity = "55%",
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}