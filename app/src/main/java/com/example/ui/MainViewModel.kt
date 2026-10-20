package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DashboardRepository
import com.example.model.DashboardWidget
import com.example.model.EngagementStats
import com.example.model.MediaItem
import com.example.model.NotificationCategory
import com.example.model.NotificationItem
import com.example.model.UserSettings
import com.example.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppDestination {
    DASHBOARD,
    WEB_PORTAL,
    NOTIFICATIONS,
    SAVED,
    CUSTOMIZE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DashboardRepository()

    // Navigation State
    private val _currentDestination = MutableStateFlow(AppDestination.DASHBOARD)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Web Portal State
    private val _webUrl = MutableStateFlow("https://noodlemagazine.com/home")
    val webUrl: StateFlow<String> = _webUrl.asStateFlow()

    private val _webTitle = MutableStateFlow("Noodle Magazine Home")
    val webTitle: StateFlow<String> = _webTitle.asStateFlow()

    private val _webLoading = MutableStateFlow(false)
    val webLoading: StateFlow<Boolean> = _webLoading.asStateFlow()

    private val _webProgress = MutableStateFlow(0)
    val webProgress: StateFlow<Int> = _webProgress.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    // Search and Category Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Notification Filter
    private val _selectedNotificationFilter = MutableStateFlow<NotificationCategory?>(null)
    val selectedNotificationFilter: StateFlow<NotificationCategory?> = _selectedNotificationFilter.asStateFlow()

    // Repository Flows
    val widgets: StateFlow<List<DashboardWidget>> = repository.widgets
    val allMediaItems: StateFlow<List<MediaItem>> = repository.mediaItems
    val allNotifications: StateFlow<List<NotificationItem>> = repository.notifications
    val engagement: StateFlow<EngagementStats> = repository.engagement
    val settings: StateFlow<UserSettings> = repository.settings
    val history: StateFlow<List<MediaItem>> = repository.history

    // Filtered media items based on search and category
    val filteredMedia: StateFlow<List<MediaItem>> = combine(
        allMediaItems,
        _searchQuery,
        _selectedCategory
    ) { items, query, category ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.summary.contains(query, ignoreCase = true) ||
                    item.tags.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered notifications
    val filteredNotifications: StateFlow<List<NotificationItem>> = combine(
        allNotifications,
        _selectedNotificationFilter
    ) { notifs, filter ->
        if (filter == null) notifs else notifs.filter { it.category == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = allNotifications.combine(_currentDestination) { notifs, _ ->
        notifs.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Alert feedback message
    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()

    init {
        NotificationHelper.initNotificationChannels(application)
        startSimulatedRealTimeTicker()
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
    }

    fun openWebPortal(url: String = "https://noodlemagazine.com/home") {
        _webUrl.value = url
        _currentDestination.value = AppDestination.WEB_PORTAL
    }

    fun updateWebState(title: String, loading: Boolean, progress: Int, back: Boolean, forward: Boolean) {
        _webTitle.value = title
        _webLoading.value = loading
        _webProgress.value = progress
        _canGoBack.value = back
        _canGoForward.value = forward
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setNotificationFilter(filter: NotificationCategory?) {
        _selectedNotificationFilter.value = filter
    }

    fun toggleWidget(id: String) {
        repository.toggleWidget(id)
    }

    fun moveWidgetUp(id: String) {
        repository.moveWidgetUp(id)
    }

    fun moveWidgetDown(id: String) {
        repository.moveWidgetDown(id)
    }

    fun resetWidgets() {
        repository.resetWidgetsToDefault()
    }

    fun toggleBookmark(id: String) {
        repository.toggleBookmark(id)
    }

    fun onMediaItemClicked(item: MediaItem) {
        repository.recordMediaRead(item)
        openWebPortal(item.webUrl)
    }

    fun markNotificationAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    fun clearAllNotifications() {
        repository.clearAllNotifications()
    }

    fun sendTestSystemNotification(
        title: String = "Noodle Magazine: Live Spotlight",
        message: String = "Exclusive video editorial just dropped on the web portal. Tap to explore!",
        category: NotificationCategory = NotificationCategory.BREAKING
    ) {
        val app = getApplication<Application>()
        val notifId = (System.currentTimeMillis() % 100000).toInt()
        NotificationHelper.sendSystemNotification(
            context = app,
            notificationId = notifId,
            title = title,
            message = message,
            category = category,
            targetUrl = "https://noodlemagazine.com/home"
        )
        repository.triggerSimulatedLiveAlert(title, message, category)
        _alertMessage.value = "Real-time notification sent to system tray!"
    }

    fun clearAlertMessage() {
        _alertMessage.value = null
    }

    fun updateSettings(transform: (UserSettings) -> UserSettings) {
        repository.updateSettings(transform)
    }

    private fun startSimulatedRealTimeTicker() {
        viewModelScope.launch {
            // Periodically post subtle live engagement pulse to showcase real-time alerts
            delay(45000) // Initial delay
            while (true) {
                if (settings.value.enableRealTimeAlerts) {
                    val titles = listOf(
                        "Live Trend: High engagement on new Magazine issue",
                        "Editor's Pick: Fresh digital story published",
                        "Trending Spotlight: Community discussing current highlights"
                    )
                    val picked = titles.random()
                    repository.triggerSimulatedLiveAlert(
                        title = picked,
                        message = "Explore fresh updates on the Noodle Magazine web dashboard.",
                        category = NotificationCategory.NEW_RELEASE
                    )
                }
                delay(90000)
            }
        }
    }
}
