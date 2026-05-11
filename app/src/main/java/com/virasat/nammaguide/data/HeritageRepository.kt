package com.virasat.nammaguide.data

import com.virasat.nammaguide.database.VisitedPlaceDao
import com.virasat.nammaguide.database.VisitedPlaceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HeritageRepository(
    private val visitedPlaceDao: VisitedPlaceDao
) {
    val places: List<HeritagePlace> = MockHeritagePlaces.places

    fun getPlace(placeId: String): HeritagePlace? = MockHeritagePlaces.findById(placeId)

    fun observeVisitedPlaceIds(): Flow<Set<String>> {
        return visitedPlaceDao.observeVisitedPlaces()
            .map { visitedPlaces -> visitedPlaces.map { it.placeId }.toSet() }
    }

    fun observeVisitedPlaces(): Flow<List<HeritagePlace>> {
        return visitedPlaceDao.observeVisitedPlaces()
            .map { visitedEntities ->
                visitedEntities.mapNotNull { entity -> getPlace(entity.placeId) }
            }
    }

    fun observeIsVisited(placeId: String): Flow<Boolean> {
        return visitedPlaceDao.observeIsVisited(placeId)
    }

    suspend fun checkIn(placeId: String) {
        visitedPlaceDao.insertVisitedPlace(
            VisitedPlaceEntity(
                placeId = placeId,
                checkedInAtMillis = System.currentTimeMillis()
            )
        )
    }
}
