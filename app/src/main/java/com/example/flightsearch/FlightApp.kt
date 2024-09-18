package com.example.flightsearch

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.screens.AirportName
import com.example.flightsearch.ui.screens.FlightsViewModel
import com.example.flightsearch.ui.screens.HomeScreen
import com.example.flightsearch.ui.screens.SearchResults
import com.example.flightsearch.ui.screens.SearchState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class FlightScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    SearchResults(title = R.string.search_results)
}

@Composable
fun FlightApp() {
    val navController: NavHostController = rememberNavController()
    val viewModel: FlightsViewModel = viewModel(factory = FlightsViewModel.Factory)
    val uiState = viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FlightScreen.valueOf(
        backStackEntry?.destination?.route ?: FlightScreen.Home.name
    )
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            AppBar(
                onBackButtonClick = {
                    navController.navigateUp()
                    //viewModel.resetSearchState()
                },
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Search(
                query = uiState.value.initialQuery,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = {
                    runBlocking {
                        viewModel.onSearch()
                    }
                    navController.navigate(FlightScreen.SearchResults.name)
                },
                onSuggestionClick = {
                    runBlocking {
                        viewModel.getSearchResults(it)
                    }
                    navController.navigate(FlightScreen.SearchResults.name)
                },
                onBarClick = { viewModel.onBarClick() },
                onSystemBackClick = { viewModel.resetSearchState() },
                isActive = uiState.value.searchState == SearchState.Searching,
                airportSuggestions = uiState.value.airportSuggestions
            )
            NavHost(
                navController = navController,
                startDestination = FlightScreen.Home.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                composable(route = FlightScreen.Home.name) {
                    HomeScreen(
                        favouriteRoutes = uiState.value.favourites,
                        onFavouriteChange = { viewModel.onFavouriteChange(it) }
                    )
                }
                composable(route = FlightScreen.SearchResults.name) {
                    SearchResults(
                        routes = uiState.value.routes,
                        onFavouriteChange = { viewModel.onFavouriteChange(it) }
                    )
                }
                if (uiState.value.doInitialSearch) {
                    navController.navigate(FlightScreen.SearchResults.name)
                    viewModel.initialSearchDone()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onBackButtonClick: () -> Unit,
    currentScreen: FlightScreen,
    canNavigateBack: Boolean
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(currentScreen.title))
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.navigate_back))
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSuggestionClick: (Airport) -> Unit,
    onBarClick: () -> Unit,
    onSystemBackClick: () -> Unit,
    isActive: Boolean,
    airportSuggestions: List<Airport>
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = isActive,
        onActiveChange = {/*TODO*/},
        placeholder = {
            if (!isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onBarClick)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        leadingIcon = {
            IconButton(onClick = { onSearch(query) }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }
        },
    ) {
        BackHandler(true) {
            onSystemBackClick()
        }
        SearchSuggestions(
            airports = airportSuggestions,
            isTyped = query != "",
            onSuggestionClick = onSuggestionClick
        )
    }
}

@Composable
fun SearchSuggestions(
    airports: List<Airport>,
    isTyped: Boolean,
    onSuggestionClick: (Airport) -> Unit
) {
    if (isTyped && airports.isNotEmpty()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(airports) {
                AirportName(
                    iataCode = it.iataCode,
                    name = it.name,
                    modifier = Modifier.clickable { onSuggestionClick(it) }
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isTyped) {
                Text(
                    text = stringResource(R.string.no_results),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(stringResource(R.string.type_to_see_results))
            }
        }
    }
}