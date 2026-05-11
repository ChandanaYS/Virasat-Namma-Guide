package com.virasat.nammaguide.data

data class HeritagePlace(
    val id: String,
    val nameEnglish: String,
    val nameKannada: String,
    val shortDescriptionEnglish: String,
    val shortDescriptionKannada: String,
    val historyEnglish: String,
    val historyKannada: String,
    val hiddenFactEnglish: String,
    val hiddenFactKannada: String,
    val imageName: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val audioResName: String
)
