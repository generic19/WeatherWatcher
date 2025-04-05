package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.basilalasadi.iti.kotlin.weatherwatcher.R

data class VisibilityCardData(
    val visibility: String,
    @StringRes val visibilityUnit: Int,
) {
    companion object {
        val preview = VisibilityCardData(
            visibility = "10",
            visibilityUnit = R.string.unit_kilometer
        )
    }
}

@Composable
fun VisibilityCard(modifier: Modifier = Modifier, data: VisibilityCardData) {
    InfoCard(
        icon = R.drawable.wi_fog,
        label = R.string.lbl_visibility,
        title = "${data.visibility} ${stringResource(data.visibilityUnit)}",
        modifier = modifier
    )
}