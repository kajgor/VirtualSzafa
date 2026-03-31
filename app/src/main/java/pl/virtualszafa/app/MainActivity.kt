package pl.virtualszafa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.virtualszafa.app.data.FakeWardrobeRepository
import pl.virtualszafa.app.ui.VirtualSzafaApp
import pl.virtualszafa.app.ui.theme.VirtualSzafaTheme
import pl.virtualszafa.app.viewmodel.HomeViewModel
import pl.virtualszafa.app.viewmodel.HomeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = FakeWardrobeRepository()
        setContent {
            VirtualSzafaTheme {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(repository),
                )
                VirtualSzafaApp(viewModel = viewModel)
            }
        }
    }
}
