package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

interface FlightRepository {
    fun getSuggestions(query: String): Flow<List<Airport>>

    fun getSearchResults(airport: Airport): Flow<List<Route>>

    suspend fun onFavouriteChange(route: Route)

    fun getFavourites(): Flow<List<Route>>

    suspend fun saveLastQuery(query: String)

    val lastQuery: Flow<String>
}

class FlightRepositoryImpl(
    private val flightDao: FlightDao,
    private val dataStore: DataStore<Preferences>
): FlightRepository {
    companion object {
        val LAST_QUERY = stringPreferencesKey("last_query")
        const val TAG = "FlightRepository"
    }
    override fun getSuggestions(query: String): Flow<List<Airport>> = flightDao.getSuggestions(query)

    override fun getSearchResults(airport: Airport): Flow<List<Route>> = flightDao.getSearchResultsByCode(airport.iataCode)

    override suspend fun onFavouriteChange(route: Route) = flightDao.changeFavouriteState(route)

    override fun getFavourites(): Flow<List<Route>> = flightDao.getFavourites()

    override suspend fun saveLastQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[LAST_QUERY] = query
        }
    }

    override val lastQuery: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            it[LAST_QUERY] ?: ""
        }
}