package com.example.infobahaya.domain.repository

import com.example.infobahaya.domain.model.NotificationCategory
import com.example.infobahaya.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(category: NotificationCategory? = null): Flow<List<NotificationItem>>
    fun getUnreadCount(): Flow<Int>
    suspend fun markAsRead(id: String): Result<Unit>
    suspend fun markAllAsRead(): Result<Unit>
    suspend fun addNotification(item: NotificationItem): Result<Unit>
}
