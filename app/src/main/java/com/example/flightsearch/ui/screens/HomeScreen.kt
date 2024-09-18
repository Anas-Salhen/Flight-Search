package com.example.flightsearch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.Route
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun HomeScreen(
    favouriteRoutes: List<Route>,
    onFavouriteChange: (Route) -> Unit
) {
    Favourites(favouriteRoutes, onFavouriteChange)
}

@Composable
fun Favourites(
    favouriteRoutes: List<Route>,
    onFavouriteChange: (Route) -> Unit
) {
    if (favouriteRoutes.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_favourite),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Text(
            text = stringResource(R.string.favourite_routes),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
    FlightsColumn(routes = favouriteRoutes) { onFavouriteChange(it) }
}

@Composable
fun FlightsColumn(
    routes: List<Route>,
    onFavouriteChange: (Route) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(routes) { route ->
            FlightCard(
                route = route,
                onFavouriteChange = { onFavouriteChange(route) }
            )
        }
    }
}

@Composable
fun FlightCard(
    route: Route,
    onFavouriteChange: (Route) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(stringResource(R.string.depart))
                AirportName(iataCode = route.departureCode, name = route.departureName)
                Text(stringResource(R.string.arrive))
                AirportName(iataCode = route.destinationCode, name = route.destinationName)
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .size(height = 150.dp, width = 50.dp)
                    .padding(end = 8.dp)
            ) {
                IconButton(onClick = { onFavouriteChange(route) }) {
                    if (route.isFavourite == 1) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.remove_favourite),
                            tint = Color(0xFF924A00)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.make_favourite)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AirportName(iataCode: String, name: String, modifier: Modifier = Modifier) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$iataCode  ")
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                append(name)
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun HomePreview() {
    FlightSearchTheme {
        //FlightApp()
    }
}