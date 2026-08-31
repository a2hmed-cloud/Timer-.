package com.example.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.NewsArticle
import com.example.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<NewsArticle> = emptyList(),
    val errorMessage: String? = null
)

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        _uiState.value = NewsUiState(isLoading = true)
        viewModelScope.launch {
            val result = newsRepository.getLatestNews()
            result.onSuccess { list ->
                _uiState.value = NewsUiState(
                    isLoading = false,
                    articles = list,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = NewsUiState(
                    isLoading = false,
                    articles = emptyList(),
                    errorMessage = error.message
                )
            }
        }
    }

    class Factory(
        private val newsRepository: NewsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsViewModel(newsRepository) as T
        }
    }
}
