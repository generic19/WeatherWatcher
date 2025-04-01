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

interface CloudsCardData {
    val coverage: String
    @get:StringRes val coverageDescription: Int
}

@Composable
fun CloudsCard(modifier: Modifier, data: CloudsCardData) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_cloud),
        label = R.string.lbl_clouds,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = data.coverage,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
            )
            Text(
                text = stringResource(R.string.lbl_coverage),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(data.coverageDescription),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    CloudsCard(
        data = object : CloudsCardData {
            override val coverage: String = "24%"
            override val coverageDescription: Int = R.string.few_clouds
        },
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}