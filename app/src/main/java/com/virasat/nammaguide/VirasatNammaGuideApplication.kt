package com.virasat.nammaguide

import android.app.Application
import com.virasat.nammaguide.data.HeritageRepository
import com.virasat.nammaguide.database.VirasatDatabase

class VirasatNammaGuideApplication : Application() {
    val repository: HeritageRepository by lazy {
        HeritageRepository(
            visitedPlaceDao = VirasatDatabase.getInstance(this).visitedPlaceDao()
        )
    }
}
