package com.virasat.nammaguide.data

object HeritageQrParser {
    private const val PlacePrefix = "virasat://place/"

    fun parsePlaceId(rawValue: String?): String? {
        if (rawValue.isNullOrBlank()) return null

        val trimmedValue = rawValue.trim()
        return when {
            trimmedValue.startsWith(PlacePrefix) -> trimmedValue.removePrefix(PlacePrefix)
            MockHeritagePlaces.findById(trimmedValue) != null -> trimmedValue
            else -> null
        }?.takeIf { MockHeritagePlaces.findById(it) != null }
    }
}
