package com.virasat.nammaguide.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.virasat.nammaguide.data.HeritagePlace
import com.virasat.nammaguide.ui.components.PlaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    places: List<HeritagePlace>,
    onPlaceClick: (String) -> Unit,
    onQrClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var radiusKm by remember { mutableIntStateOf(10) }
    var useKannada by remember { mutableStateOf(false) }
    val visiblePlaces = remember(radiusKm, places) {
        places.filter { it.distanceKm <= radiusKm }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Virasat-Namma Guide",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Travel history"
                        )
                    }
                    IconButton(onClick = onQrClick) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan QR code"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = if (useKannada) "ಹತ್ತಿರದ ಪರಂಪರೆ ತಾಣಗಳು" else "Nearby Heritage Places",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = radiusKm == 5,
                    onClick = { radiusKm = 5 },
                    label = { Text("5 km") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = radiusKm == 10,
                    onClick = { radiusKm = 10 },
                    label = { Text("10 km") }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "ಕನ್ನಡ",
                    style = MaterialTheme.typography.labelLarge
                )
                Switch(
                    checked = useKannada,
                    onCheckedChange = { useKannada = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    AboutHeritageGuideCard()
                }
                items(visiblePlaces, key = { it.id }) { place ->
                    PlaceCard(
                        place = place,
                        useKannada = useKannada,
                        onClick = { onPlaceClick(place.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutHeritageGuideCard(modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "About Virasat-Namma Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Welcome to Virasat-Namma Guide, built to make heritage discovery smarter, faster, and more personal.\n" +
                        "The app combines modern mobile technology with a clean interface for exploring Karnataka's historic places.\n" +
                        "It helps users discover nearby monuments, scan QR codes, listen to audio guides, and save visits like a travel passport.\n" +
                        "Designed for reliability and accessibility, the experience stays responsive across phones and tablets.\n" +
                        "The platform focuses on user convenience, real-time guidance, and meaningful cultural learning.\n" +
                        "Our goal is to make every heritage visit easier, richer, and more memorable.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f)
                )
            }
        }
    }
}
