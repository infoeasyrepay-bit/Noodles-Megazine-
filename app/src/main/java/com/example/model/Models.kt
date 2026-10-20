package com.example.model

enum class WidgetType {
    HERO_BANNER,
    LIVE_ALERTS_TICKER,
    QUICK_ACTIONS,
    ENGAGEMENT_METRICS,
    TRENDING_MEDIA,
    TOPIC_CHANNELS,
    EDITORIAL_FEED
}

data class DashboardWidget(
    val id: String,
    val type: WidgetType,
    val title: String,
    val subtitle: String,
    val isEnabled: Boolean = true,
    val order: Int = 0
)

data class MediaItem(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val durationOrReadTime: String,
    val views: String,
    val timestamp: String,
    val webUrl: String,
    val author: String = "Editorial Team",
    val isBookmarked: Boolean = false,
    val isTrending: Boolean = false,
    val tags: List<String> = emptyList()
)

enum class NotificationCategory {
    BREAKING,
    NEW_RELEASE,
    ENGAGEMENT,
    RECOMMENDED
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timestamp: String,
    val isRead: Boolean = false,
    val targetUrl: String = "https://noodlemagazine.com/home",
    val priorityHigh: Boolean = false
)

data class EngagementStats(
    val watchMinutes: Int = 142,
    val articlesRead: Int = 38,
    val savedCount: Int = 12,
    val streakDays: Int = 7,
    val engagementLevel: String = "Gold Reader"
)

data class UserSettings(
    val enableRealTimeAlerts: Boolean = true,
    val breakingAlerts: Boolean = true,
    val contentAlerts: Boolean = true,
    val dailyDigest: Boolean = true,
    val desktopMode: Boolean = false,
    val darkReaderMode: Boolean = true,
    val soundAlerts: Boolean = true,
    val vibrationAlerts: Boolean = true,
    val dashboardDensity: String = "Standard" // Compact, Standard, Spacious
)
