package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R

data class PrecipitationCardData(
    val precipitation: String,
    @StringRes val precipitationType: Int,
    val probability: String?,
) {
    companion object {
        val preview = PrecipitationCardData(
            precipitation = "3 mm",
            precipitationType = R.string.cond_rain,
            probability = "10%",
        )
    }
}

@Composable
fun PrecipitationCard(modifier: Modifier, data: PrecipitationCardData) {
    InfoCard(
        modifier = modifier,
        icon = R.drawable.wi_raindrop,
        label = R.string.lbl_precipitation,
        title = data.precipitation,
        subtitle = stringResource(data.precipitationType),
        footnote = data.probability?.let { stringResource(R.string.tmpl_probability).format(it) },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    PrecipitationCard(
        data = PrecipitationCardData.preview,
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}