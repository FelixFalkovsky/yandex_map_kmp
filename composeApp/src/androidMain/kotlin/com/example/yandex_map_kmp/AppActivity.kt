package com.example.yandex_map_kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.yandex.mapkit.MapKitFactory
import org.jetbrains.compose.ui.tooling.preview.Preview

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MapScreen() }

        MapKitFactory.setApiKey("API_KEY")
        MapKitFactory.setLocale("ru_RU")
    }
}

@Preview
@Composable
fun AppPreview() { MapContent(places = mokoLocatioData, false) }