package com.basilalasadi.iti.kotlin.weatherwatcher.utility.math

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

sealed class AppMath {
    companion object {
        /**
         * Converts polar coordinates to cartesian coordinates using cylindrical projection.
         *
         * ø: latitude
         * λ: longitude
         * W: map width
         * H: map height
         *
         * N = ln(tan( π/4 + øʳ/2 ))
         *
         * x = (λ° + 180) * W / 360
         * y = H/2 - N*W / (2π)
         */
        fun mercatorProjection(coordinates: City.Coordinates, mapWidth: Double, mapHeight: Double): Pair<Double, Double> {
            val latDeg = coordinates.latitude
            val lonDeg = coordinates.longitude
            
            val latRad = latDeg * PI / 180
            val merc = ln(tan(PI / 4 + latRad / 2))
            
            val x = (lonDeg + 180) * mapWidth / 360
            val y = mapHeight / 2 - mapWidth * merc / (2 * PI)
            
            return x to y
        }
        
        fun coordinatesToMapTile(coordinates: City.Coordinates, zoomLevel: Int = 3): Pair<Int, Int> {
            val size = 2.0.pow(zoomLevel)
            
            return mercatorProjection(coordinates, size, size)
                .run { first.toInt() to second.toInt() }
        }
    }
}
