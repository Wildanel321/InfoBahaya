package com.example.infobahaya.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infobahaya.core.di.ServiceLocator
import com.example.infobahaya.domain.model.NotificationCategory
import com.example.infobahaya.domain.model.NotificationItem
import com.example.infobahaya.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val filteredNotifications: List<NotificationItem> = emptyList(),
    val selectedCategory: NotificationCategory? = null,
    val unreadCount: Int = 0,
    val isLoading: Boolean = false
)

class NotificationViewModel(
    private val notificationRepository: NotificationRepository = ServiceLocator.provideNotificationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            notificationRepository.getNotifications().collect { list ->
                val unread = list.count { !it.isRead }
                _uiState.value = _uiState.value.copy(
                    notifications = list,
                    unreadCount = unread,
                    isLoading = false
                )
                applyCategoryFilter()
            }
        }
    }

    fun onSelectCategory(cat: NotificationCategory?) {
        val newCat = if (_uiState.value.selectedCategory == cat) null else cat
        _uiState.value = _uiState.value.copy(selectedCategory = newCat)
        applyCategoryFilter()
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    private fun applyCategoryFilter() {
        val s = _uiState.value
        val filtered = if (s.selectedCategory == null) {
            s.notifications
        } else {
            s.notifications.filter { it.category == s.selectedCategory }
        }
        _uiState.value = s.copy(filteredNotifications = filtered)
    }
}
