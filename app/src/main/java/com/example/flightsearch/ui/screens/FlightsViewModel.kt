package com.example.flightsearch.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.data.Route
import com.example.flightsearch.ui.FlightApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightsViewModel(
    private val flightRepository: FlightRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(FlightsUiState(SearchState.NoSearch, listOf(), "", listOf(), listOf(), false))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateFavourites()
        }
        viewModelScope.launch {
            initialSearch()
        }
    }

    private suspend fun initialSearch() {
        flightRepository.lastQuery.collect {lastQuery ->
            _uiState.update {flightsUiState ->
                flightsUiState.copy(
                    initialQuery = lastQuery,
                    doInitialSearch = true
                )
            }
            if (_uiState.value.initialQuery != "") {
                onQueryChange(_uiState.value.initialQuery)
                onSearch()
            }
        }
    }

    fun initialSearchDone() {
        _uiState.update {
            it.copy(doInitialSearch = false)
        }
    }

    fun onQueryChange(query: String) {
        viewModelScope.launch {
            flightRepository.getSuggestions(query).collect { airportSuggestions ->
                _uiState.update { flightsUiState ->
                    flightsUiState.copy(
                        initialQuery = query,
                        searchState = SearchState.Searching,
                        airportSuggestions = airportSuggestions
                    )
                }
            }
        }
        viewModelScope.launch {
            flightRepository.saveLastQuery(query)
        }
    }

    fun onSearch() {
        if (_uiState.value.airportSuggestions.size == 1) {
            viewModelScope.launch {
                getSearchResults(_uiState.value.airportSuggestions[0])
            }
        }
    }

    private suspend fun updateFavourites() {
        flightRepository.getFavourites().collect {favourites ->
            _uiState.update { flightsUiState ->
                flightsUiState.copy(
                    favourites = favourites
                )
            }
        }
    }

    fun onFavouriteChange(route: Route) {
        viewModelScope.launch {
            if (route.isFavourite == 1) {
                flightRepository.onFavouriteChange(route.copy(isFavourite = 0))
            } else {
                flightRepository.onFavouriteChange(route.copy(isFavourite = 1))
            }
            updateFavourites()
        }
    }

    fun onBarClick() {
        _uiState.update {
            it.copy(
                searchState = SearchState.Searching
            )
        }
    }

    fun resetSearchState() {
        _uiState.update {
            it.copy(
                searchState = SearchState.NoSearch
            )
        }
    }

    suspend fun getSearchResults(departureAirport: Airport) {
        val routes = flightRepository.getSearchResults(departureAirport).first()
        _uiState.update {flightsUiState ->
            flightsUiState.copy(
                searchState = SearchState.Searched,
                routes = routes
            )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightApplication)
                FlightsViewModel(application.container.flightRepository)
            }
        }
    }
}

data class FlightsUiState(
    val searchState: SearchState = SearchState.NoSearch,
    val favourites: List<Route>,
    val initialQuery: String,
    val airportSuggestions: List<Airport>,
    val routes: List<Route>,
    var doInitialSearch: Boolean
)

enum class SearchState {
    NoSearch,
    Searching,
    Searched
}