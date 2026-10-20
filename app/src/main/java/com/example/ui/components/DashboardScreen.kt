package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DashboardWidget
import com.example.model.EngagementStats
import com.example.model.MediaItem
import com.example.model.NotificationItem
import com.example.model.UserSettings
import com.example.model.WidgetType
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NoirSurfaceCard
import com.example.ui.theme.NoirSurfaceElevated

@Composable
fun DashboardScreen(
    widgets: List<DashboardWidget>,
    mediaItems: List<MediaItem>,
    trendingItems: List<MediaItem>,
    latestNotification: NotificationItem?,
    engagement: EngagementStats,
    settings: UserSettings,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onOpenPortal: () -> Unit,
    onViewNotifications: () -> Unit,
    onViewSaved: () -> Unit,
    onCustomize: () -> Unit,
    onTriggerTestAlert: () -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val enabledWidgets = widgets.filter { it.isEnabled }.sortedBy { it.order }

    val contentSpacing: Dp = when (settings.dashboardDensity) {
        "Compact" -> 10.dp
        "Spacious" -> 20.dp
        else -> 14.dp
    }

    val categories = listOf("All", "Technology", "Digital Art", "Media", "Engagement", "Entertainment", "UI / UX")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        // Search & Customize Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_search_field"),
                    placeholder = {
                        Text(
                            "Search stories, videos, topics...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = NoirSurfaceCard,
                        unfocusedContainerColor = NoirSurfaceCard
                    )
                )

                IconButton(
                    onClick = onCustomize,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NoirSurfaceCard)
                        .testTag("header_customize_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize Dashboard",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Dynamic Widget Engine: Render enabled widgets strictly according to their order
        enabledWidgets.forEach { widget ->
            when (widget.type) {
                WidgetType.HERO_BANNER -> {
                    item(key = "widget_hero_banner") {
                        HeroBannerWidget(
                            onOpenPortal = onOpenPortal,
                            onCustomize = onCustomize
                        )
                    }
                }
                WidgetType.LIVE_ALERTS_TICKER -> {
                    item(key = "widget_live_alerts") {
                        LiveAlertsTickerWidget(
                            latestNotification = latestNotification,
                            onViewAlerts = onViewNotifications,
                            onTriggerTestAlert = onTriggerTestAlert
                        )
                    }
                }
                WidgetType.QUICK_ACTIONS -> {
                    item(key = "widget_quick_actions") {
                        QuickActionsWidget(
                            onOpenPortal = onOpenPortal,
                            onViewNotifications = onViewNotifications,
                            onViewSaved = onViewSaved,
                            onCustomize = onCustomize
                        )
                    }
                }
                WidgetType.ENGAGEMENT_METRICS -> {
                    item(key = "widget_engagement_metrics") {
                        EngagementMetricsWidget(stats = engagement)
                    }
                }
                WidgetType.TRENDING_MEDIA -> {
                    item(key = "widget_trending_media") {
                        TrendingMediaWidget(
                            items = trendingItems,
                            onItemClick = onMediaClick,
                            onToggleBookmark = onToggleBookmark
                        )
                    }
                }
                WidgetType.TOPIC_CHANNELS -> {
                    item(key = "widget_topic_channels") {
                        TopicChannelsWidget(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onSelectCategory = onCategorySelect
                        )
                    }
                }
                WidgetType.EDITORIAL_FEED -> {
                    item(key = "widget_feed_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedCategory == "All") "Latest Feed" else "$selectedCategory Feed",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${mediaItems.size} items",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (mediaItems.isEmpty()) {
                        item(key = "widget_feed_empty") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NoirSurfaceCard)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No content matches your search query.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(mediaItems, key = { it.id }) { item ->
                            MediaItemCard(
                                item = item,
                                onClick = { onMediaClick(item) },
                                onToggleBookmark = { onToggleBookmark(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
