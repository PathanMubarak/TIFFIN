
package com.propel.tiffin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.propel.tiffin.data.Kitchen
import com.propel.tiffin.data.KitchenRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface KitchenUiState {
    data object Loading : KitchenUiState
    data class Success(val kitchens: List<Kitchen>) : KitchenUiState
    data object Empty : KitchenUiState
    data class Error(val message: String) : KitchenUiState
}

class KitchenViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        KitchenRepository(application.applicationContext)

    private val _uiState =
        MutableStateFlow<KitchenUiState>(KitchenUiState.Loading)

    val uiState: StateFlow<KitchenUiState> =
        _uiState.asStateFlow()

    init {
        loadKitchens()
    }

    fun loadKitchens() {
        viewModelScope.launch {
            _uiState.value = KitchenUiState.Loading

            try {
                val kitchens = repository.getKitchens()

                _uiState.value = if (kitchens.isEmpty()) {
                    KitchenUiState.Empty
                } else {
                    KitchenUiState.Success(kitchens)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = KitchenUiState.Error(
                    exception.message ?: "Unable to load kitchens."
                )
            }
        }
    }
}
