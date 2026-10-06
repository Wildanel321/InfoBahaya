package com.example.infobahaya.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationRepositoryTest {

    private lateinit var notificationRepository: MockNotificationRepository

    @Before
    fun setUp() {
        notificationRepository = MockNotificationRepository()
    }

    @Test
    fun testNotificationsLoaded() = runTest {
        val notifications = notificationRepository.getNotifications().first()
        assertTrue(notifications.isNotEmpty())
    }

    @Test
    fun testMarkAsRead() = runTest {
        val notifications = notificationRepository.getNotifications().first()
        val unreadItem = notifications.first { !it.isRead }

        notificationRepository.markAsRead(unreadItem.id)
        val updatedList = notificationRepository.getNotifications().first()
        val target = updatedList.first { it.id == unreadItem.id }
        assertTrue(target.isRead)
    }

    @Test
    fun testMarkAllAsRead() = runTest {
        notificationRepository.markAllAsRead()
        val notifications = notificationRepository.getNotifications().first()
        val unreadCount = notificationRepository.getUnreadCount().first()
        assertEquals(0, unreadCount)
        assertTrue(notifications.all { it.isRead })
    }
}
