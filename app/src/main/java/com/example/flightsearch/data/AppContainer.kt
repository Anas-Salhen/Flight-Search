package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

interface AppContainer {
    val flightRepository: FlightRepository
}

private const val QUERY_PREFERENCE_NAME = "query_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = QUERY_PREFERENCE_NAME
)

class AppContainerImpl(private val context: Context): AppContainer {
    override val flightRepository by lazy {
        FlightRepositoryImpl(
            flightDao = FlightDatabase.getDatabase(context).flightDao(),
            dataStore = context.dataStore
        )
    }
}