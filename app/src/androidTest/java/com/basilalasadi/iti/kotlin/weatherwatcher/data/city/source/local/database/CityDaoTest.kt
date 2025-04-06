package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database

import androidx.compose.ui.util.fastForEach
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database.AppDatabase
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.CityEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.pow

@SmallTest
@RunWith(AndroidJUnit4::class)
class CityDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: CityDao
    
    private val cities = ('A'..'C')
        .flatMap { letter ->
            (1..5)
                .map { number ->
                    val coord = 30.0 + 0.0001 * 10.0.pow(number.toDouble())
                    
                    CityEntity(
                        name = "city$letter$number",
                        country = City.Country.EG,
                        localizedName = LocalizedName("سيتي$letter$number", "city$letter$number"),
                        coordinates = City.Coordinates(coord, coord),
                    )
                }
        }
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        
        dao = database.getCityDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun putCities_citiesAdded() = runTest {
        dao.putCities(cities)
        
        val actual = dao.findCitiesByName("city", limit = 100)
        assertTrue(actual.containsAll(cities))
    }
    
    @Test
    fun putCities_citiesUpdated() = runTest {
        dao.putCities(cities)
        
        val expected = cities[0].copy(coordinates = City.Coordinates(1.0, 2.0))
        dao.putCities(listOf(expected))
        
        dao.findCitiesByName("city", limit = 100).fastForEach {
            it.run { println("city  name=$name country=$country coordinates=${coordinates.latitude},${coordinates.longitude}") }
        }
        
        val actual = dao.findCitiesByName(expected.name, limit = 1).getOrNull(0)
        
        assertEquals(expected.coordinates, actual?.coordinates)
    }
    
    @Test
    fun findCitiesByName_cityBEnglish_cityB() = runTest {
        dao.putCities(cities)
        
        val expected = cities.filter { it.name.startsWith("cityB") }
        val actual = dao.findCitiesByName("cityB", limit = 100)
        
        assertTrue(actual.containsAll(expected))
    }
    
    @Test
    fun findCitiesByName_cityBArabic_cityB() = runTest {
        dao.putCities(cities)
        
        val expected = cities.filter { it.name.startsWith("cityB") }
        val actual = dao.findCitiesByName("سيتيB", limit = 100)
        
        assertTrue(actual.containsAll(expected))
    }
    
    @Test
    fun findCloseCity_closestCity() = runTest {
        val lat = 50.0
        val lon = 10.0
        
        val city1 = cities[0].copy(coordinates = City.Coordinates(lat, lon))
        val city2 = cities[1].copy(coordinates = City.Coordinates(lat + 0.1, lon - 0.1))
        val city3 = cities[2].copy(coordinates = City.Coordinates(lat + 1.0, lon - 1.0))
        
        dao.putCities(listOf(city1, city2, city3))
        
        assertEquals(city1.name, dao.findCloseCity(lat, lon, 0.3)?.name)
        
        dao.putCities(listOf(city1.copy(coordinates = City.Coordinates(0.0, 0.0))))
        assertEquals(city2.name, dao.findCloseCity(lat, lon, 0.3)?.name)
        
        dao.putCities(listOf(city2.copy(coordinates = City.Coordinates(0.0, 0.0))))
        assertNull(dao.findCloseCity(lat, lon, 0.3))
    }
    
    @Test
    fun setFavorite_true_favoriteSet() = runTest {
        dao.putCities(cities)
        
        val expected = cities.subList(3, 6)
            .map { it.copy(isFavorite = true) }
            .toHashSet()
        
        expected.forEach {
            dao.setFavorite(it.name, it.country, true)
        }
        
        val actual = dao.getFavoritesFlow().first().toHashSet()
        assert(expected == actual) { "expected ${expected.map { it.name }}, got ${actual.map { it.name }}" }
    }
}
