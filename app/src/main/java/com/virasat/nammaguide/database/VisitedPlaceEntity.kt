package com.virasat.nammaguide.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visited_places")
data class VisitedPlaceEntity(
    @PrimaryKey val placeId: String,
    val checkedInAtMillis: Long
)
