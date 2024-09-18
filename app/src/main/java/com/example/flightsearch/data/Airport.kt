package com.example.flightsearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "iata_code")
    val iataCode: String,
    val passengers: Int
)

@Entity
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "departure_code")
    val departureCode: String,
    @ColumnInfo(name = "departure_name")
    val departureName: String,
    @ColumnInfo(name = "destination_code")
    val destinationCode: String,
    @ColumnInfo(name = "destination_name")
    val destinationName: String,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Int
)