package com.virasat.nammaguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.virasat.nammaguide.navigation.VirasatNavGraph
import com.virasat.nammaguide.ui.theme.VirasatNammaGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirasatNammaGuideTheme {
                VirasatNavGraph()
            }
        }
    }
}
