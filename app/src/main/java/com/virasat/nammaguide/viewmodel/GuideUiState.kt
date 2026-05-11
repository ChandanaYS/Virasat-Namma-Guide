package com.virasat.nammaguide.viewmodel

import com.virasat.nammaguide.data.HeritagePlace

data class GuideUiState(
    val places: List<HeritagePlace> = emptyList(),
    val visitedPlaces: List<HeritagePlace> = emptyList(),
    val visitedPlaceIds: Set<String> = emptySet(),
    val selectedPlace: HeritagePlace? = null,
    val scannedPlaceId: String? = null,
    val showHiddenFact: Boolean = false,
    val isAudioPlaying: Boolean = false
)
