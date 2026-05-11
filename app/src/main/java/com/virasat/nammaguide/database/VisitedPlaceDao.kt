package com.virasat.nammaguide.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitedPlaceDao {
    @Query("SELECT * FROM visited_places ORDER BY checkedInAtMillis DESC")
    fun observeVisitedPlaces(): Flow<List<VisitedPlaceEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM visited_places WHERE placeId = :placeId)")
    fun observeIsVisited(placeId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedPlace(visitedPlace: VisitedPlaceEntity)
}
