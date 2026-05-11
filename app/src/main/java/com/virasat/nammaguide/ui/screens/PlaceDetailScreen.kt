package com.virasat.nammaguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virasat.nammaguide.data.HeritagePlace
import com.virasat.nammaguide.ui.theme.DeepStone
import com.virasat.nammaguide.ui.theme.LeafGreen
import com.virasat.nammaguide.ui.theme.TempleGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    place: HeritagePlace,
    isVisited: Boolean,
    showHiddenFact: Boolean,
    isAudioPlaying: Boolean,
    onBackClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onAudioToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var useKannada by remember { mutableStateOf(false) }
    val placeName = if (useKannada) place.nameKannada else place.nameEnglish
    val history = if (useKannada) place.historyKannada else place.historyEnglish

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = placeName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                TempleGold,
                                LeafGreen
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = place.nameEnglish.take(1),
                    color = DeepStone,
                    fontSize = 86.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = placeName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${place.distanceKm} km nearby",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "ಕನ್ನಡ",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Switch(
                        checked = useKannada,
                        onCheckedChange = { useKannada = it }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (useKannada) {
                        place.shortDescriptionKannada
                    } else {
                        place.shortDescriptionEnglish
                    },
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                DetailSection(
                    title = if (useKannada) "ಇತಿಹಾಸ" else "History",
                    body = history
                )

                Spacer(modifier = Modifier.height(14.dp))

                AudioGuideCard(
                    isAudioPlaying = isAudioPlaying,
                    onAudioToggle = { onAudioToggle(history) }
                )

                if (showHiddenFact) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HiddenFactCard(
                        fact = if (useKannada) place.hiddenFactKannada else place.hiddenFactEnglish
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onCheckInClick,
                    enabled = !isVisited,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVisited) {
                            LeafGreen
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        disabledContainerColor = LeafGreen,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVisited) {
                            if (useKannada) "ಈಗಾಗಲೇ ಭೇಟಿ ನೀಡಿದೆ" else "Already Checked In"
                        } else {
                            if (useKannada) "ಚೆಕ್-ಇನ್ ಮಾಡಿ" else "Check In"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f)
        )
    }
}

@Composable
private fun AudioGuideCard(
    isAudioPlaying: Boolean,
    onAudioToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Audio Guide",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isAudioPlaying) "Narration playing" else "Listen to the place history",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            FilledTonalButton(onClick = onAudioToggle) {
                Icon(
                    imageVector = if (isAudioPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isAudioPlaying) "Pause" else "Play")
            }
        }
    }
}

@Composable
private fun HiddenFactCard(
    fact: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FactCheck,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Column {
                Text(
                    text = "Hidden Fact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = fact,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
