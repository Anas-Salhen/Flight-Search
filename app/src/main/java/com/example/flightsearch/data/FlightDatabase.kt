package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Route::class], version = 1, exportSchema = false)
abstract class FlightDatabase: RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile
        private var Instance: FlightDatabase? = null

        private suspend fun populateRouteTable(flightDao: FlightDao) {
            val airports = flightDao.getAllAirports()
            airports.forEach { departureAirport ->
                airports.forEach { destinationAirport ->
                    if (departureAirport != destinationAirport) {
                        flightDao.insert(
                            Route(
                                departureCode = departureAirport.iataCode,
                                departureName = departureAirport.name,
                                destinationCode = destinationAirport.iataCode,
                                destinationName = destinationAirport.name,
                                isFavourite = 0
                            )
                        )
                    }
                }
            }
        }

        fun getDatabase(context: Context): FlightDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlightDatabase::class.java, "flight_database")
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                        /*runBlocking {
                            populateRouteTable(Instance!!.flightDao())
                        }*/
                    }
            }
        }
    }
}