package com.example.data

import com.example.data.dao.BirthdayDao
import com.example.data.model.BirthdayContact
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class BirthdayRepository(private val birthdayDao: BirthdayDao) {

  val allBirthdays: Flow<List<BirthdayContact>> = birthdayDao.getAllBirthdays()

  suspend fun getBirthdayById(id: Long): BirthdayContact? {
    return birthdayDao.getBirthdayById(id)
  }

  suspend fun insertBirthday(contact: BirthdayContact): Long {
    return birthdayDao.insertBirthday(contact)
  }

  suspend fun updateBirthday(contact: BirthdayContact) {
    birthdayDao.updateBirthday(contact)
  }

  suspend fun deleteBirthday(contact: BirthdayContact) {
    birthdayDao.deleteBirthday(contact)
  }

  suspend fun deleteById(id: Long) {
    birthdayDao.deleteById(id)
  }

  suspend fun seedInitialDataIfEmpty() {
    val count = birthdayDao.getCount()
    if (count == 0) {
      seedDefaultBirthdays()
    }
  }

  suspend fun resetToSampleData() {
    birthdayDao.deleteAll()
    seedDefaultBirthdays()
  }

  private suspend fun seedDefaultBirthdays() {
    val today = LocalDate.now()
    
    // Relative dates for vibrant demo on any day
    val todayBday = today
    val in3Days = today.plusDays(3)
    val in8Days = today.plusDays(8)
    val in18Days = today.plusDays(18)
    val nextMonthBday = today.plusDays(32)
    val pastBday = today.minusDays(14)

    val sampleList = listOf(
      BirthdayContact(
        name = "Maya Lin",
        birthMonth = todayBday.monthValue,
        birthDay = todayBday.dayOfMonth,
        birthYear = today.year - 26,
        relationship = "Partner",
        phoneNumber = "+1 (555) 234-5678",
        avatarColorHex = "#EC4899",
        reminderLeadDays = 1,
        giftIdeas = "• Instax camera film pack\n• Lavender aromatherapy candle\n• Matcha ceremony bowl set",
        notes = "Loves pistachio pastries & vintage records! Planning surprise birthday dinner."
      ),
      BirthdayContact(
        name = "Lucas Miller",
        birthMonth = in3Days.monthValue,
        birthDay = in3Days.dayOfMonth,
        birthYear = today.year - 30,
        relationship = "Friend",
        phoneNumber = "+1 (555) 345-6789",
        avatarColorHex = "#FBBF24",
        reminderLeadDays = 3,
        giftIdeas = "• Specialty espresso beans\n• Mechanical keyboard keycaps",
        notes = "Throwing a rooftop BBQ this weekend. Prefers iced pour-overs."
      ),
      BirthdayContact(
        name = "Emma Watson",
        birthMonth = in8Days.monthValue,
        birthDay = in8Days.dayOfMonth,
        birthYear = today.year - 24,
        relationship = "Family",
        phoneNumber = "+1 (555) 890-1234",
        avatarColorHex = "#A855F7",
        reminderLeadDays = 7,
        giftIdeas = "• Ceramic watercolor paint palette\n• Cozy oversized fleece blanket",
        notes = "Sister's birthday! Coordinate family dinner."
      ),
      BirthdayContact(
        name = "Sophia Chen",
        birthMonth = in18Days.monthValue,
        birthDay = in18Days.dayOfMonth,
        birthYear = today.year - 29,
        relationship = "Colleague",
        phoneNumber = "+1 (555) 456-7890",
        avatarColorHex = "#38BDF8",
        reminderLeadDays = 1,
        giftIdeas = "• Gourmet chocolate box\n• Desk succulent in pastel pot",
        notes = "Office team card signing & afternoon cupcake celebration."
      ),
      BirthdayContact(
        name = "David Ross",
        birthMonth = nextMonthBday.monthValue,
        birthDay = nextMonthBday.dayOfMonth,
        birthYear = today.year - 33,
        relationship = "Friend",
        phoneNumber = "+1 (555) 567-8901",
        avatarColorHex = "#34D399",
        reminderLeadDays = 3,
        giftIdeas = "• Board game (Catan expansion)\n• Craft beer flight set",
        notes = "Game night enthusiast."
      ),
      BirthdayContact(
        name = "Grandma Elena",
        birthMonth = pastBday.monthValue,
        birthDay = pastBday.dayOfMonth,
        birthYear = today.year - 78,
        relationship = "Family",
        phoneNumber = "+1 (555) 678-9012",
        avatarColorHex = "#FB7185",
        reminderLeadDays = 7,
        giftIdeas = "• Framed family photo album\n• Premium Earl Grey tea assortment",
        notes = "Always enjoys a handwritten card and long morning phone call."
      )
    )

    birthdayDao.insertAll(sampleList)
  }
}
