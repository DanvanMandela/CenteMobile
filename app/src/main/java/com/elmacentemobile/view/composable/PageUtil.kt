package com.elmacentemobile.view.composable


enum class PageState {
    Loading, Ui, Error
}

sealed class UiState {
    class Success(message: String) : UiState()
    class Error(error: String) : UiState()
    object Ui : UiState()
}

sealed interface UiCallback {
    fun onSuccess(): UiCallback
}

open class ScreenState {
    object Loading : ScreenState()
    class Ui(state: UiCallback) : ScreenState()
}

