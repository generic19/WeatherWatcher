package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

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
    val precipitationType: String,
    val probability: String,
) {
    companion object {
        val preview = PrecipitationCardData(
            precipitation = "3 mm",
            precipitationType = "Rain",
            probability = "10%",
        )
    }
}

@Composable
fun PrecipitationCard(modifier: Modifier, data: PrecipitationCardData) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_raindrop),
        label = R.string.lbl_precipitation,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = data.precipitation,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
            )
            Text(
                text = data.precipitationType,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.tmpl_probability).format(data.probability),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
    }
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