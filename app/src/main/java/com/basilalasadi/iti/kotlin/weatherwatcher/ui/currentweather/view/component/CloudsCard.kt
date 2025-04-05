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

data class CloudsCardData(
    val coverage: String,
    @StringRes val coverageDescription: Int,
) {
    companion object {
        val preview = CloudsCardData(
            coverage = "24%",
            coverageDescription = R.string.few_clouds,
        )
    }
}

@Composable
fun CloudsCard(modifier: Modifier, data: CloudsCardData) {
    InfoCard(
        modifier = modifier,
        icon = R.drawable.wi_cloud,
        label = R.string.lbl_clouds,
        title = data.coverage,
        subtitle = stringResource(R.string.lbl_coverage),
        footnote = stringResource(data.coverageDescription),
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    CloudsCard(
        data = CloudsCardData.preview,
        modifier = Modifier
            .systemBarsPadding()
            .padding(16.dp)
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    )
}