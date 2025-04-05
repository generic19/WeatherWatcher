package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
    modifier: Modifier,
    @DrawableRes icon: Int,
    @StringRes label: Int,
    title: String? = null,
    subtitle: String? = null,
    footnote: String? = null,
) {
    LabeledCard(
        painter = painterResource(icon),
        label = label,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )
            }
            
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                )
            }
            
            footnote?.let {
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
        }
    }
}
