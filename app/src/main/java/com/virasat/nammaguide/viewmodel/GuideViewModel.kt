package com.virasat.nammaguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virasat.nammaguide.data.HeritageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GuideViewModel(
    private val repository: HeritageRepository
) : ViewModel() {
    private val transientState = MutableStateFlow(
        GuideUiState(places = repository.places)
    )

    val uiState: StateFlow<GuideUiState> = combine(
        transientState,
        repository.observeVisitedPlaces(),
        repository.observeVisitedPlaceIds()
    ) { currentState, visitedPlaces, visitedPlaceIds ->
        currentState.copy(
            places = repository.places,
            visitedPlaces = visitedPlaces,
            visitedPlaceIds = visitedPlaceIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GuideUiState(places = repository.places)
    )

    fun selectPlace(placeId: String, revealHiddenFact: Boolean = false) {
        transientState.update { currentState ->
            currentState.copy(
                selectedPlace = repository.getPlace(placeId),
                scannedPlaceId = if (revealHiddenFact) placeId else currentState.scannedPlaceId,
                showHiddenFact = revealHiddenFact,
                isAudioPlaying = false
            )
        }
    }

    fun clearSelectedPlace() {
        transientState.update { currentState ->
            currentState.copy(
                selectedPlace = null,
                showHiddenFact = false,
                isAudioPlaying = false
            )
        }
    }

    fun checkInSelectedPlace() {
        val placeId = uiState.value.selectedPlace?.id ?: return
        checkInPlace(placeId)
    }

    fun checkInPlace(placeId: String) {
        viewModelScope.launch {
            repository.checkIn(placeId)
        }
    }

    fun markQrScanResult(placeId: String) {
        selectPlace(placeId = placeId, revealHiddenFact = true)
    }

    fun toggleAudio() {
        transientState.update { currentState ->
            currentState.copy(isAudioPlaying = !currentState.isAudioPlaying)
        }
    }

    fun stopAudio() {
        transientState.update { currentState ->
            currentState.copy(isAudioPlaying = false)
        }
    }
}
