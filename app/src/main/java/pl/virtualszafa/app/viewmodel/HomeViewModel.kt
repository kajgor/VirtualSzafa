package pl.virtualszafa.app.viewmodel

import androidx.lifecycle.ViewModel
import pl.virtualszafa.app.data.FakeWardrobeRepository

class HomeViewModel(
    private val repository: FakeWardrobeRepository,
) : ViewModel() {
    private var internalState = HomeUiState(
        items = repository.getItems(),
        outfits = repository.getOutfits(),
        listings = repository.getListings(),
    )

    val state: HomeUiState
        get() = internalState

    fun selectTab(tab: AppTab) {
        internalState = internalState.copy(selectedTab = tab)
    }
}
