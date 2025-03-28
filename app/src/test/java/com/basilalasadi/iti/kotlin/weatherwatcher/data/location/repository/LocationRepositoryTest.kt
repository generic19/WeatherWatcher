package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.repository

import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.LocationException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.LocationLocalDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.LocationRemoteDataSource
import io.mockk.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LocationRepositoryTest {
    private lateinit var remoteDataSource: LocationRemoteDataSource
    private lateinit var localDataSource: LocationLocalDataSource
    
    @Before
    fun setup() {
        remoteDataSource = mockkClass(LocationRemoteDataSource::class, relaxed = true)
        localDataSource = mockkClass(LocationLocalDataSource::class, relaxed = true)
    }
    
    @Test
    fun favoritesFlow_flow_sameAsGiven() = runTest {
        val city = City(
            coordinates = City.Coordinates(1.0, 1.0),
            name = LocalizedName("testAr", "testEn"),
            country = City.Country.EG,
        )
        
        val expected = listOf(city)
        every { localDataSource.favoritesFlow } returns flowOf<List<City>>(expected)
        
        val repository = LocationRepositoryImpl(localDataSource, remoteDataSource)
        val actual = repository.favoritesFlow.firstOrNull()
        
        assertEquals(expected, actual)
    }
    
    
    @OptIn(FlowPreview::class)
    @Test
    fun `searchCities - arabic and english query - loading,loadingWithLocal,successWithRemote`() = runTest {
        val cityFactory = { postfixAr: String, postfixEn: String ->
            City(
                coordinates = City.Coordinates(1.0, 1.0),
                name = LocalizedName("سيتي $postfixAr", "city $postfixEn"),
                country = City.Country.EG,
            )
        }
        
        val localCities = (1..5).map { cityFactory("لوكال $it", "local $it") }
        val remoteCities = (1..5).map { cityFactory("ريموت $it", "remote $it") }
        
        for (query in sequenceOf("سيتي", "city")) {
            coEvery {
                localDataSource.findCitiesByName(query)
            } coAnswers {
                delay(100)
                localCities
            }
            
            coEvery {
                remoteDataSource.findCitiesByQuery(query)
            } coAnswers {
                delay(1000)
                remoteCities
            }
            
            val repository = LocationRepositoryImpl(localDataSource, remoteDataSource)
            
            val resultsFlow = repository.searchCities(query)
            val results = resultsFlow.toList()
            
            assertEquals(3, results.size)
            
            assertEquals(Result.Loading<List<City>>(), results[0])
            
            assertTrue(results[1] is Result.Loading)
            assertTrue(
                results[1].value?.all {
                    it in localCities && (
                        it.name.arabic?.contains(query) == true
                        || it.name.english?.contains(query) == true
                    )
                } == true
            )
            
            assertTrue(results[2] is Result.Success)
            assertTrue(
                results[2].value?.all {
                    it in localCities && (
                        it.name.arabic?.contains(query) == true
                        || it.name.english?.contains(query) == true
                    )
                } == true
            )
        }
    }
    
    @Test
    fun `searchCities - remote throws - loading,loadingWithLocal,failureWithLocal`() = runTest {
        coEvery {
            localDataSource.findCitiesByName("city")
        } coAnswers {
            delay(100)
            listOf()
        }
        
        coEvery {
            remoteDataSource.findCitiesByQuery("city")
        } coAnswers {
            delay(1000)
            throw LocationException("Cannot reach API.")
        }
        
        val repository = LocationRepositoryImpl(localDataSource, remoteDataSource)
        
        val resultsFlow = repository.searchCities("city")
        val results = resultsFlow.toList()
        
        assertEquals(3, results.size)
        
        assertEquals(Result.Loading<List<City>>(), results[0])
        assertEquals(Result.Loading<List<City>>(listOf()), results[1])
        
        assertTrue(results[2] is Result.Failure<*, *>)
        assertNotNull((results[2] as Result.Failure<*, *>).value)
    }
}
