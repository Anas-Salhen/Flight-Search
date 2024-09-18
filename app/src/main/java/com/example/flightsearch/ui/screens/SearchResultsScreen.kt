package com.example.flightsearch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.Route
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun SearchResults(
    routes: List<Route>,
    onFavouriteChange: (Route) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (routes.isNotEmpty()) {
            val departureCode = routes[0].departureCode
            Text(
                text = stringResource(R.string.flights_from, departureCode),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            FlightsColumn(routes = routes) { onFavouriteChange(it) }
        } else {
            Text(
                text = stringResource(R.string.no_results),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun SearchResultsPreview() {
    FlightSearchTheme {
        val routes = listOf(
            Route(
                id = 1,
                departureCode = "OPO",
                departureName = "Francisco Sá Carneiro Airport",
                destinationCode = "FCO",
                destinationName = "Leonardo da Vinci International Airport",
                isFavourite = 0
            ),
            Route(
                id = 1,
                departureCode = "OPO",
                departureName = "Francisco Sá Carneiro Airport",
                destinationCode = "FCO",
                destinationName = "Leonardo da Vinci International Airport",
                isFavourite = 0
            ),
            Route(
                id = 1,
                departureCode = "OPO",
                departureName = "Francisco Sá Carneiro Airport",
                destinationCode = "FCO",
                destinationName = "Leonardo da Vinci International Airport",
                isFavourite = 0
            ),
            Route(
                id = 1,
                departureCode = "OPO",
                departureName = "Francisco Sá Carneiro Airport",
                destinationCode = "FCO",
                destinationName = "Leonardo da Vinci International Airport",
                isFavourite = 0
            )
        )
        SearchResults(routes = routes) {}
    }
}