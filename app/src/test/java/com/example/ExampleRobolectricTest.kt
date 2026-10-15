package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BirthdayContact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Celebrate", appName)
  }

  @Test
  fun `birthday contact calculations are accurate`() {
    val today = LocalDate.of(2026, 10, 15)
    val contactToday = BirthdayContact(
      name = "Maya Lin",
      birthMonth = 10,
      birthDay = 15,
      birthYear = 2000,
      relationship = "Partner"
    )

    assertTrue(contactToday.isToday(today))
    assertEquals(0, contactToday.daysUntil(today))
    assertEquals(26, contactToday.turningAge(today))

    val contactNextWeek = BirthdayContact(
      name = "Lucas",
      birthMonth = 10,
      birthDay = 18,
      birthYear = 1996,
      relationship = "Friend"
    )

    assertEquals(3, contactNextWeek.daysUntil(today))
    assertEquals("Libra", contactNextWeek.zodiacSign().first)
  }
}
