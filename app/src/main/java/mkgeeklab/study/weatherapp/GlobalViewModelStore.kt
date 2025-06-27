package mkgeeklab.study.weatherapp

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object GlobalViewModelStore : ViewModelStoreOwner {
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore = store
}
