package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DashboardRepository
import com.example.model.NotificationCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Noodle Magazine", appName)
  }

  @Test
  fun `test dashboard repository widget customization`() {
    val repository = DashboardRepository()
    val initialWidgets = repository.widgets.value
    assertTrue(initialWidgets.isNotEmpty())

    val firstWidget = initialWidgets[0]
    assertTrue(firstWidget.isEnabled)

    // Toggle widget off
    repository.toggleWidget(firstWidget.id)
    val updatedWidgets = repository.widgets.value
    assertFalse(updatedWidgets.first { it.id == firstWidget.id }.isEnabled)

    // Toggle back on
    repository.toggleWidget(firstWidget.id)
    assertTrue(repository.widgets.value.first { it.id == firstWidget.id }.isEnabled)
  }

  @Test
  fun `test notification addition and marking read`() {
    val repository = DashboardRepository()
    repository.triggerSimulatedLiveAlert(
      title = "Test Live Alert",
      message = "Checking real time notification pipeline",
      category = NotificationCategory.BREAKING
    )

    val notifications = repository.notifications.value
    val alert = notifications.first { it.title == "Test Live Alert" }
    assertFalse(alert.isRead)

    repository.markNotificationAsRead(alert.id)
    assertTrue(repository.notifications.value.first { it.id == alert.id }.isRead)
  }
}
