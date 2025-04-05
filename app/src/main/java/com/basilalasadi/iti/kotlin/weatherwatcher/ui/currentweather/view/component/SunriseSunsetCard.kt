package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R


data class SunriseSunsetCardData(
    val sunrise: String,
    val sunset: String,
) {
    companion object {
        val preview = SunriseSunsetCardData(
            sunrise = "5:53 AM",
            sunset = "6:19 PM",
        )
    }
}

@Composable
fun SunriseSunsetCard(modifier: Modifier, data: SunriseSunsetCardData) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_horizon_alt),
        label = R.string.lbl_daytime,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(0.5f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.wi_sunrise),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.lbl_sunrise),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            
            Text(
                text = data.sunrise,
                style = MaterialTheme.typography.headlineSmall,
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(0.5f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.wi_sunset),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = stringResource(R.string.lbl_sunset),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            
            Text(
                text = data.sunset,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    SunriseSunsetCard(
        data = SunriseSunsetCardData.preview,
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}