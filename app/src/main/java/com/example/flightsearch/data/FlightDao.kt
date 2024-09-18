package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Query(
        "SELECT * from airport " +
        "WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%' " +
        "ORDER BY passengers DESC"
    )
    fun getSuggestions(query: String): Flow<List<Airport>>

    @Query("SELECT * from Route WHERE departure_code = :query")
    fun getSearchResultsByCode(query: String): Flow<List<Route>>

    @Query("SELECT * from Route WHERE is_favourite = 1")
    fun getFavourites(): Flow<List<Route>>

    @Query("SELECT * from airport")
    suspend fun getAllAirports(): List<Airport>

    @Update
    suspend fun changeFavouriteState(route: Route)

    @Insert(
        entity = Route::class,
        onConflict = OnConflictStrategy.IGNORE
    )
    suspend fun insert(route: Route)
}