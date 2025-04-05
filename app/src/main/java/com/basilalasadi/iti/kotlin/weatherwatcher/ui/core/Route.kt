package com.basilalasadi.iti.kotlin.weatherwatcher.ui.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable object CurrentWeather : Route()
    @Serializable object SavedLocations : Route()
    @Serializable object Alerts : Route()
    @Serializable object Settings : Route()
}

sealed class TabRoute(val route: Route, val icon: ImageVector) {
    object CurrentWeather : TabRoute(route = Route.CurrentWeather, icon = Icons.Outlined.Home)
    object SavedLocations : TabRoute(route = Route.SavedLocations, icon = Icons.Outlined.LocationOn)
    object Alerts : TabRoute(route = Route.Alerts, icon = Icons.Outlined.Notifications)
    object Settings : TabRoute(route = Route.Settings, icon = Icons.Outlined.Settings)
}

val tabRoutes = listOf<TabRoute>(
    TabRoute.CurrentWeather,
    TabRoute.SavedLocations,
    TabRoute.Alerts,
    TabRoute.Settings,
)