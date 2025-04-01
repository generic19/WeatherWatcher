package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun LabeledCard(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    painter: Painter,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
            alpha = 0.5f
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.Companion
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.Companion
                        .size(16.dp)
                        .alpha(0.6f)
                )
                Text(
                    text = stringResource(label).toUpperCase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.Companion
                        .alpha(0.6f)
                )
            }
            content()
        }
    }
}