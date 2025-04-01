package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.basilalasadi.iti.kotlin.weatherwatcher.R

@Composable
fun PressureCard(modifier: Modifier) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_barometer),
        label = R.string.lbl_pressure,
        modifier = modifier
    ) { }
}