package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("""
        select * from CityEntity where
            (name like '%' || :name || '%')
            or (localizedName_arabic like '%' || :name || '%')
            or (localizedName_english like '%' || :name || '%')
        limit :limit
    """)
    suspend fun findCitiesByName(name: String, limit: Int = 5): List<CityEntity>

    @Query("""
        select
            *,
            (latitude - :latitude) * (latitude - :latitude)
                + (longitude - :longitude) * (longitude - :longitude)
            as deviation
        from CityEntity
        where deviation <= :maxDeviation
        order by deviation
        limit 1
    """)
    suspend fun findCloseCity(latitude: Double, longitude: Double, maxDeviation: Double): CityEntity?

    @Upsert
    suspend fun putCities(cities: List<CityEntity>)
 
    @Query("""
        update CityEntity
        set isFavorite = :isFavorite
        where name = :name and country = :country
    """)
    suspend fun setFavorite(name: String, country: City.Country, isFavorite: Boolean): Int

    @Query("""
        select * from CityEntity where isFavorite = 1
    """)
    fun getFavoritesFlow(): Flow<List<CityEntity>>
}
