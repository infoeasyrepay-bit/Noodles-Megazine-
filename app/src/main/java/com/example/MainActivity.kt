package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notification.NotificationHelper
import com.example.ui.AppDestination
import com.example.ui.MainViewModel
import com.example.ui.components.CustomizeDashboardScreen
import com.example.ui.components.DashboardScreen
import com.example.ui.components.NotificationsScreen
import com.example.ui.components.SavedHistoryScreen
import com.example.ui.components.WebPortalScreen
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NoirBackground
import com.example.ui.theme.NoirSurfaceCard

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val targetUrl = it.getStringExtra(NotificationHelper.EXTRA_TARGET_URL)
            val targetTab = it.getStringExtra(NotificationHelper.EXTRA_TARGET_TAB)
            if (targetUrl != null && targetTab == "portal") {
                viewModel.openWebPortal(targetUrl)
            } else if (targetTab == "alerts") {
                viewModel.navigateTo(AppDestination.NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val destination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val widgets by viewModel.widgets.collectAsStateWithLifecycle()
    val mediaItems by viewModel.filteredMedia.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMediaItems.collectAsStateWithLifecycle()
    val notifications by viewModel.filteredNotifications.collectAsStateWithLifecycle()
    val latestNotification = notifications.firstOrNull()
    val engagement by viewModel.engagement.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedNotifFilter by viewModel.selectedNotificationFilter.collectAsStateWithLifecycle()
    val webUrl by viewModel.webUrl.collectAsStateWithLifecycle()
    val alertMessage by viewModel.alertMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(alertMessage) {
        alertMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearAlertMessage()
        }
    }

    // Back handler for sub-screens
    BackHandler(enabled = destination != AppDestination.DASHBOARD) {
        viewModel.navigateTo(AppDestination.DASHBOARD)
    }

    val bookmarkedItems = remember(allMedia) { allMedia.filter { it.isBookmarked } }
    val trendingItems = remember(allMedia) { allMedia.filter { it.isTrending } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirBackground),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        )
                        Text(
                            text = when (destination) {
                                AppDestination.DASHBOARD -> stringResource(R.string.app_name)
                                AppDestination.WEB_PORTAL -> stringResource(R.string.portal_title)
                                AppDestination.NOTIFICATIONS -> stringResource(R.string.notifications_title)
                                AppDestination.SAVED -> stringResource(R.string.saved_items_title)
                                AppDestination.CUSTOMIZE -> stringResource(R.string.customize_title)
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (destination == AppDestination.DASHBOARD) {
                        Surface(
                            color = GoldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else if (destination != AppDestination.CUSTOMIZE) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppDestination.CUSTOMIZE) },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("topbar_customize_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Customize",
                                tint = GoldPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NoirSurfaceCard
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NoirSurfaceCard,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = destination == AppDestination.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppDestination.DASHBOARD) },
                    icon = {
                        Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard")
                    },
                    label = { Text("Dashboard", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E1700),
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = destination == AppDestination.WEB_PORTAL,
                    onClick = { viewModel.openWebPortal("https://noodlemagazine.com/home") },
                    icon = {
                        Icon(imageVector = Icons.Default.Language, contentDescription = "Web Portal")
                    },
                    label = { Text("Portal", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E1700),
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_portal")
                )

                NavigationBarItem(
                    selected = destination == AppDestination.NOTIFICATIONS,
                    onClick = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = CoralAccent) {
                                        Text("$unreadCount", color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E1700),
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_notifications")
                )

                NavigationBarItem(
                    selected = destination == AppDestination.SAVED,
                    onClick = { viewModel.navigateTo(AppDestination.SAVED) },
                    icon = {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved")
                    },
                    label = { Text("Saved", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1E1700),
                        selectedTextColor = GoldPrimary,
                        indicatorColor = GoldPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_saved")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = destination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NoirBackground)
        ) { currentDest ->
            when (currentDest) {
                AppDestination.DASHBOARD -> {
                    DashboardScreen(
                        widgets = widgets,
                        mediaItems = mediaItems,
                        trendingItems = trendingItems,
                        latestNotification = latestNotification,
                        engagement = engagement,
                        settings = settings,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = viewModel::setSearchQuery,
                        onCategorySelect = viewModel::setCategory,
                        onOpenPortal = { viewModel.openWebPortal("https://noodlemagazine.com/home") },
                        onViewNotifications = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                        onViewSaved = { viewModel.navigateTo(AppDestination.SAVED) },
                        onCustomize = { viewModel.navigateTo(AppDestination.CUSTOMIZE) },
                        onTriggerTestAlert = { viewModel.sendTestSystemNotification() },
                        onMediaClick = viewModel::onMediaItemClicked,
                        onToggleBookmark = viewModel::toggleBookmark
                    )
                }

                AppDestination.WEB_PORTAL -> {
                    WebPortalScreen(
                        initialUrl = webUrl,
                        onUpdateWebState = viewModel::updateWebState
                    )
                }

                AppDestination.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        selectedFilter = selectedNotifFilter,
                        settings = settings,
                        onSelectFilter = viewModel::setNotificationFilter,
                        onMarkRead = viewModel::markNotificationAsRead,
                        onMarkAllRead = viewModel::markAllNotificationsAsRead,
                        onClearAll = viewModel::clearAllNotifications,
                        onTriggerTestAlert = { viewModel.sendTestSystemNotification() },
                        onUpdateSettings = viewModel::updateSettings,
                        onOpenWebUrl = { url -> viewModel.openWebPortal(url) }
                    )
                }

                AppDestination.SAVED -> {
                    SavedHistoryScreen(
                        bookmarkedItems = bookmarkedItems,
                        historyItems = history,
                        onItemClick = viewModel::onMediaItemClicked,
                        onToggleBookmark = viewModel::toggleBookmark,
                        onOpenPortal = { viewModel.openWebPortal("https://noodlemagazine.com/home") }
                    )
                }

                AppDestination.CUSTOMIZE -> {
                    CustomizeDashboardScreen(
                        widgets = widgets,
                        settings = settings,
                        onToggleWidget = viewModel::toggleWidget,
                        onMoveUp = viewModel::moveWidgetUp,
                        onMoveDown = viewModel::moveWidgetDown,
                        onResetWidgets = viewModel::resetWidgets,
                        onUpdateSettings = viewModel::updateSettings,
                        onDone = { viewModel.navigateTo(AppDestination.DASHBOARD) }
                    )
                }
            }
        }
    }
}
