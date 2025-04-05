package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
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

data class PressureCardData(
    val groundLevelPressure: Double,
    val seaLevelPressure: Double,
    @StringRes val pressureUnit: Int,
) {
    companion object {
        val preview = PressureCardData(
            groundLevelPressure = 1020.0,
            seaLevelPressure = 1022.0,
            pressureUnit = R.string.unit_hectopascal,
        )
    }
}

@Composable
fun PressureCard(modifier: Modifier, data: PressureCardData) {
    val template = if (data.pressureUnit == R.string.unit_hectopascal)
        "%.0f %s" else "%.2f %s"
    
    LabeledCard(
        painter = painterResource(R.drawable.wi_barometer),
        label = R.string.lbl_pressure,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(0.5f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_water),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.lbl_sea_level),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            
            Text(
                text =  template.format(data.seaLevelPressure, stringResource(data.pressureUnit)),
                style = MaterialTheme.typography.headlineSmall,
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ground),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = stringResource(R.string.lbl_ground_level),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            
            Text(
                text = template.format(data.groundLevelPressure, stringResource(data.pressureUnit)),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    PressureCard(
        data = PressureCardData.preview,
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}