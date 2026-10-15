package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@Entity(tableName = "birthday_contacts")
data class BirthdayContact(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val birthMonth: Int, // 1 - 12
  val birthDay: Int,   // 1 - 31
  val birthYear: Int? = null, // e.g. 1996 or null
  val relationship: String = "Friend", // Family, Friend, Partner, Colleague, Other
  val phoneNumber: String = "",
  val photoUri: String? = null,
  val avatarColorHex: String = "#EC4899",
  val reminderLeadDays: Int = 1, // 0 = On the day, 1, 3, 7, 14
  val giftIdeas: String = "",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
) {
  /**
   * Calculates the next occurrence of this contact's birthday from referenceDate.
   */
  fun getNextBirthday(referenceDate: LocalDate = LocalDate.now()): LocalDate {
    val currentYear = referenceDate.year
    // Handle leap day (Feb 29) on non-leap years
    val validDay = if (birthMonth == 2 && birthDay == 29 && !java.time.Year.isLeap(currentYear.toLong())) {
      28
    } else {
      birthDay
    }
    
    val thisYearBirthday = try {
      LocalDate.of(currentYear, birthMonth, validDay)
    } catch (_: Exception) {
      LocalDate.of(currentYear, birthMonth, 28)
    }

    return if (!thisYearBirthday.isBefore(referenceDate)) {
      thisYearBirthday
    } else {
      val nextYear = currentYear + 1
      val validNextDay = if (birthMonth == 2 && birthDay == 29 && !java.time.Year.isLeap(nextYear.toLong())) {
        28
      } else {
        birthDay
      }
      try {
        LocalDate.of(nextYear, birthMonth, validNextDay)
      } catch (_: Exception) {
        LocalDate.of(nextYear, birthMonth, 28)
      }
    }
  }

  fun daysUntil(referenceDate: LocalDate = LocalDate.now()): Int {
    val nextBday = getNextBirthday(referenceDate)
    return ChronoUnit.DAYS.between(referenceDate, nextBday).toInt()
  }

  fun isToday(referenceDate: LocalDate = LocalDate.now()): Boolean {
    return daysUntil(referenceDate) == 0
  }

  fun isTomorrow(referenceDate: LocalDate = LocalDate.now()): Boolean {
    return daysUntil(referenceDate) == 1
  }

  fun isThisMonth(referenceDate: LocalDate = LocalDate.now()): Boolean {
    val nextBday = getNextBirthday(referenceDate)
    return nextBday.monthValue == referenceDate.monthValue && nextBday.year == referenceDate.year
  }

  /**
   * Calculates the age this person will be turning on their next birthday.
   */
  fun turningAge(referenceDate: LocalDate = LocalDate.now()): Int? {
    if (birthYear == null) return null
    val nextBday = getNextBirthday(referenceDate)
    return nextBday.year - birthYear
  }

  /**
   * Current age right now.
   */
  fun currentAge(referenceDate: LocalDate = LocalDate.now()): Int? {
    if (birthYear == null) return null
    var age = referenceDate.year - birthYear
    if (referenceDate.monthValue < birthMonth || 
        (referenceDate.monthValue == birthMonth && referenceDate.dayOfMonth < birthDay)) {
      age -= 1
    }
    return age.coerceAtLeast(0)
  }

  fun formattedBirthday(includeYear: Boolean = true): String {
    val monthName = Month.of(birthMonth).getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return if (includeYear && birthYear != null) {
      "$monthName $birthDay, $birthYear"
    } else {
      "$monthName $birthDay"
    }
  }

  fun zodiacSign(): Pair<String, String> {
    return when (birthMonth) {
      1 -> if (birthDay <= 19) "Capricorn" to "♑" else "Aquarius" to "♒"
      2 -> if (birthDay <= 18) "Aquarius" to "♒" else "Pisces" to "♓"
      3 -> if (birthDay <= 20) "Pisces" to "♓" else "Aries" to "♈"
      4 -> if (birthDay <= 19) "Aries" to "♈" else "Taurus" to "♉"
      5 -> if (birthDay <= 20) "Taurus" to "♉" else "Gemini" to "♊"
      6 -> if (birthDay <= 20) "Gemini" to "♊" else "Cancer" to "♋"
      7 -> if (birthDay <= 22) "Cancer" to "♋" else "Leo" to "♌"
      8 -> if (birthDay <= 22) "Leo" to "♌" else "Virgo" to "♍"
      9 -> if (birthDay <= 22) "Virgo" to "♍" else "Libra" to "♎"
      10 -> if (birthDay <= 22) "Libra" to "♎" else "Scorpio" to "♏"
      11 -> if (birthDay <= 21) "Scorpio" to "♏" else "Sagittarius" to "♐"
      12 -> if (birthDay <= 21) "Sagittarius" to "♐" else "Capricorn" to "♑"
      else -> "Capricorn" to "♑"
    }
  }
}
