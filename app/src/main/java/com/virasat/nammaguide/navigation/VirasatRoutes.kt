package com.virasat.nammaguide.navigation

object VirasatRoutes {
    const val Splash = "splash"
    const val Home = "home"
    const val QrScanner = "qr_scanner"
    const val TravelHistory = "travel_history"
    const val PlaceDetail = "place_detail/{placeId}/{fromQr}"

    fun placeDetail(placeId: String, fromQr: Boolean = false): String {
        return "place_detail/$placeId/$fromQr"
    }
}
