package com.example.data

import com.example.model.DashboardWidget
import com.example.model.EngagementStats
import com.example.model.MediaItem
import com.example.model.NotificationCategory
import com.example.model.NotificationItem
import com.example.model.UserSettings
import com.example.model.WidgetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardRepository {

    private val initialWidgets = listOf(
        DashboardWidget(
            id = "hero_banner",
            type = WidgetType.HERO_BANNER,
            title = "Magazine Spotlight",
            subtitle = "Featured editorial spotlight and web portal launch",
            isEnabled = true,
            order = 0
        ),
        DashboardWidget(
            id = "live_alerts",
            type = WidgetType.LIVE_ALERTS_TICKER,
            title = "Live Alert Pulse",
            subtitle = "Real-time updates & breaking notification ticker",
            isEnabled = true,
            order = 1
        ),
        DashboardWidget(
            id = "quick_actions",
            type = WidgetType.QUICK_ACTIONS,
            title = "Quick Access Hub",
            subtitle = "Direct web portal, search, bookmarks, and refresh",
            isEnabled = true,
            order = 2
        ),
        DashboardWidget(
            id = "engagement_metrics",
            type = WidgetType.ENGAGEMENT_METRICS,
            title = "Engagement & Streak",
            subtitle = "Personal reading and viewing stats",
            isEnabled = true,
            order = 3
        ),
        DashboardWidget(
            id = "trending_media",
            type = WidgetType.TRENDING_MEDIA,
            title = "Trending Now",
            subtitle = "Top viral topics and most-viewed releases",
            isEnabled = true,
            order = 4
        ),
        DashboardWidget(
            id = "topic_channels",
            type = WidgetType.TOPIC_CHANNELS,
            title = "Categories & Channels",
            subtitle = "Explore content by dedicated topic",
            isEnabled = true,
            order = 5
        ),
        DashboardWidget(
            id = "editorial_feed",
            type = WidgetType.EDITORIAL_FEED,
            title = "Personalized Feed",
            subtitle = "Latest releases tailored to your interests",
            isEnabled = true,
            order = 6
        )
    )

    private val initialMediaItems = listOf(
        MediaItem(
            id = "m1",
            title = "The Evolution of Next-Gen Video Streaming Platforms in 2026",
            summary = "How ultra-responsive architectures and adaptive bitrates are transforming digital web magazines.",
            category = "Technology",
            durationOrReadTime = "6 min read",
            views = "48.2K",
            timestamp = "15m ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Alex Vance",
            isBookmarked = true,
            isTrending = true,
            tags = listOf("Streaming", "Tech", "Future")
        ),
        MediaItem(
            id = "m2",
            title = "Cinematic Visuals & Digital Culture: Inside the New Aesthetic Wave",
            summary = "A deep dive into high-contrast dark visual art and motion graphics captivating audiences worldwide.",
            category = "Digital Art",
            durationOrReadTime = "8 min read",
            views = "112.5K",
            timestamp = "1h ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Elena Rostova",
            isBookmarked = false,
            isTrending = true,
            tags = listOf("Design", "Culture", "Trends")
        ),
        MediaItem(
            id = "m3",
            title = "Live Audio & Podcast Synthesis: The Next Stage of Media Engagement",
            summary = "Interactive audio feeds and seamless background streaming keep subscribers engaged twice as long.",
            category = "Media",
            durationOrReadTime = "14 min listen",
            views = "29.7K",
            timestamp = "3h ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Marcus Brody",
            isBookmarked = false,
            isTrending = false,
            tags = listOf("Audio", "Podcasts", "Media")
        ),
        MediaItem(
            id = "m4",
            title = "Community First: Why Interactive Real-Time Notifications Matter",
            summary = "How instant alerts drive retention, live discussions, and seamless browsing loops across web apps.",
            category = "Engagement",
            durationOrReadTime = "4 min read",
            views = "63.9K",
            timestamp = "5h ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Sara Chen",
            isBookmarked = true,
            isTrending = true,
            tags = listOf("Mobile", "Retention", "RealTime")
        ),
        MediaItem(
            id = "m5",
            title = "Global Cinema & Indie Film Highlights: Weekly Curated Review",
            summary = "Our editors select the 10 most captivating independent short films and visual stories this week.",
            category = "Entertainment",
            durationOrReadTime = "10 min read",
            views = "85.1K",
            timestamp = "1d ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Jordan Lee",
            isBookmarked = false,
            isTrending = false,
            tags = listOf("Cinema", "Film", "Reviews")
        ),
        MediaItem(
            id = "m6",
            title = "Hyper-Personalized Dashboards: Designing for True User Control",
            summary = "Modular widgets, custom reordering, and density controls give users genuine ownership over their feed.",
            category = "UI / UX",
            durationOrReadTime = "5 min read",
            views = "34.0K",
            timestamp = "2d ago",
            webUrl = "https://noodlemagazine.com/home",
            author = "Kai Tanaka",
            isBookmarked = false,
            isTrending = false,
            tags = listOf("Design", "Product", "Customization")
        )
    )

    private val initialNotifications = listOf(
        NotificationItem(
            id = "n1",
            title = "Breaking: Live Spotlight Streaming Now",
            message = "New featured broadcast is live on Noodle Magazine portal. Tap to tune in directly.",
            category = NotificationCategory.BREAKING,
            timestamp = "Just now",
            isRead = false,
            targetUrl = "https://noodlemagazine.com/home",
            priorityHigh = true
        ),
        NotificationItem(
            id = "n2",
            title = "New Release in Technology",
            message = "'The Evolution of Next-Gen Video Streaming Platforms in 2026' has been published.",
            category = NotificationCategory.NEW_RELEASE,
            timestamp = "18 min ago",
            isRead = false,
            targetUrl = "https://noodlemagazine.com/home"
        ),
        NotificationItem(
            id = "n3",
            title = "7-Day Reading Streak Reached!",
            message = "You've unlocked the Gold Reader badge on your custom dashboard. Keep it up!",
            category = NotificationCategory.ENGAGEMENT,
            timestamp = "2 hours ago",
            isRead = true,
            targetUrl = "https://noodlemagazine.com/home"
        ),
        NotificationItem(
            id = "n4",
            title = "Trending in Digital Art",
            message = "Over 100K users viewed 'Cinematic Visuals & Digital Culture' today.",
            category = NotificationCategory.RECOMMENDED,
            timestamp = "Yesterday",
            isRead = true,
            targetUrl = "https://noodlemagazine.com/home"
        )
    )

    private val _widgets = MutableStateFlow(initialWidgets)
    val widgets: StateFlow<List<DashboardWidget>> = _widgets.asStateFlow()

    private val _mediaItems = MutableStateFlow(initialMediaItems)
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _engagement = MutableStateFlow(EngagementStats())
    val engagement: StateFlow<EngagementStats> = _engagement.asStateFlow()

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _history = MutableStateFlow<List<MediaItem>>(listOf(initialMediaItems[0], initialMediaItems[1]))
    val history: StateFlow<List<MediaItem>> = _history.asStateFlow()

    // Widget customization methods
    fun toggleWidget(id: String) {
        _widgets.update { current ->
            current.map { widget ->
                if (widget.id == id) widget.copy(isEnabled = !widget.isEnabled) else widget
            }
        }
    }

    fun moveWidgetUp(id: String) {
        _widgets.update { current ->
            val list = current.sortedBy { it.order }.toMutableList()
            val index = list.indexOfFirst { it.id == id }
            if (index > 0) {
                val temp = list[index]
                list[index] = list[index - 1]
                list[index - 1] = temp
                list.mapIndexed { i, w -> w.copy(order = i) }
            } else {
                current
            }
        }
    }

    fun moveWidgetDown(id: String) {
        _widgets.update { current ->
            val list = current.sortedBy { it.order }.toMutableList()
            val index = list.indexOfFirst { it.id == id }
            if (index != -1 && index < list.size - 1) {
                val temp = list[index]
                list[index] = list[index + 1]
                list[index + 1] = temp
                list.mapIndexed { i, w -> w.copy(order = i) }
            } else {
                current
            }
        }
    }

    fun resetWidgetsToDefault() {
        _widgets.value = initialWidgets
    }

    // Media & Bookmarks methods
    fun toggleBookmark(id: String) {
        _mediaItems.update { items ->
            items.map { item ->
                if (item.id == id) item.copy(isBookmarked = !item.isBookmarked) else item
            }
        }
        val currentSaved = _mediaItems.value.count { it.isBookmarked }
        _engagement.update { it.copy(savedCount = currentSaved) }
    }

    fun recordMediaRead(item: MediaItem) {
        _history.update { current ->
            (listOf(item) + current.filter { it.id != item.id }).take(25)
        }
        _engagement.update {
            it.copy(
                articlesRead = it.articlesRead + 1,
                watchMinutes = it.watchMinutes + 5
            )
        }
    }

    // Notification methods
    fun markNotificationAsRead(id: String) {
        _notifications.update { items ->
            items.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { items ->
            items.map { it.copy(isRead = true) }
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun addNotification(item: NotificationItem) {
        _notifications.update { current ->
            listOf(item) + current
        }
    }

    fun triggerSimulatedLiveAlert(title: String, message: String, category: NotificationCategory) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        val newItem = NotificationItem(
            id = "alert_${System.currentTimeMillis()}",
            title = title,
            message = message,
            category = category,
            timestamp = "Just now ($timeStr)",
            isRead = false,
            targetUrl = "https://noodlemagazine.com/home",
            priorityHigh = (category == NotificationCategory.BREAKING)
        )
        addNotification(newItem)
    }

    // Settings update
    fun updateSettings(transform: (UserSettings) -> UserSettings) {
        _settings.update(transform)
    }
}
