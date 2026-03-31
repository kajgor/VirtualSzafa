package pl.virtualszafa.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.virtualszafa.app.ui.screen.OutfitsScreen
import pl.virtualszafa.app.ui.screen.WardrobeScreen
import pl.virtualszafa.app.ui.screen.MarketplaceScreen
import pl.virtualszafa.app.viewmodel.AppTab
import pl.virtualszafa.app.viewmodel.HomeViewModel

@Composable
fun VirtualSzafaApp(viewModel: HomeViewModel) {
    val state = viewModel.state
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        label = { Text(tab.label) },
                        icon = { Text(tab.label.take(1)) },
                    )
                }
            }
        },
    ) { padding ->
        HomeContent(
            padding = padding,
            state = state,
        )
    }
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    state: pl.virtualszafa.app.viewmodel.HomeUiState,
) {
    when (state.selectedTab) {
        AppTab.WARDROBE -> WardrobeScreen(
            modifier = Modifier.padding(padding),
            items = state.items,
        )
        AppTab.OUTFITS -> OutfitsScreen(
            modifier = Modifier.padding(padding),
            outfits = state.outfits,
            items = state.items,
        )
        AppTab.MARKETPLACE -> MarketplaceScreen(
            modifier = Modifier.padding(padding),
            items = state.items,
            listings = state.listings,
        )
    }
}
