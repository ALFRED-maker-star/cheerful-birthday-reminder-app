package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CelebrateDatabase
import com.example.data.BirthdayRepository
import com.example.data.model.BirthdayContact
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class BirthdayViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BirthdayRepository

  init {
    val database = CelebrateDatabase.getDatabase(application)
    repository = BirthdayRepository(database.birthdayDao())
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
    NotificationHelper.createNotificationChannel(application)
  }

  val allBirthdays: StateFlow<List<BirthdayContact>> = repository.allBirthdays
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Search & Filter state for All Contacts
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow<String?>("All")
  val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

  // Calendar State
  private val _currentCalendarMonth = MutableStateFlow(YearMonth.now())
  val currentCalendarMonth: StateFlow<YearMonth> = _currentCalendarMonth.asStateFlow()

  private val _selectedCalendarDate = MutableStateFlow<LocalDate?>(LocalDate.now())
  val selectedCalendarDate: StateFlow<LocalDate?> = _selectedCalendarDate.asStateFlow()

  // Settings State (stored in SharedPreferences)
  private val prefs = application.getSharedPreferences("celebrate_prefs", Context.MODE_PRIVATE)

  private val _notificationsEnabled = MutableStateFlow(
    prefs.getBoolean("notifications_enabled", true)
  )
  val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

  private val _defaultReminderLeadDays = MutableStateFlow(
    prefs.getInt("default_reminder_days", 1)
  )
  val defaultReminderLeadDays: StateFlow<Int> = _defaultReminderLeadDays.asStateFlow()

  // Derived filtered contacts
  val filteredContacts: StateFlow<List<BirthdayContact>> = combine(
    allBirthdays,
    _searchQuery,
    _selectedCategory
  ) { list, query, category ->
    val q = query.trim().lowercase()
    list.filter { contact ->
      val matchesQuery = q.isEmpty() ||
          contact.name.lowercase().contains(q) ||
          contact.relationship.lowercase().contains(q) ||
          contact.notes.lowercase().contains(q) ||
          contact.giftIdeas.lowercase().contains(q)

      val matchesCategory = category == null || category == "All" ||
          contact.relationship.equals(category, ignoreCase = true)

      matchesQuery && matchesCategory
    }.sortedBy { it.name.lowercase() }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCategoryFilter(category: String?) {
    _selectedCategory.value = category
  }

  fun setCalendarMonth(yearMonth: YearMonth) {
    _currentCalendarMonth.value = yearMonth
  }

  fun nextCalendarMonth() {
    _currentCalendarMonth.value = _currentCalendarMonth.value.plusMonths(1)
  }

  fun prevCalendarMonth() {
    _currentCalendarMonth.value = _currentCalendarMonth.value.minusMonths(1)
  }

  fun selectCalendarDate(date: LocalDate?) {
    _selectedCalendarDate.value = date
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    _notificationsEnabled.value = enabled
    prefs.edit().putBoolean("notifications_enabled", enabled).apply()
  }

  fun setDefaultReminderLeadDays(days: Int) {
    _defaultReminderLeadDays.value = days
    prefs.edit().putInt("default_reminder_days", days).apply()
  }

  fun sendTestNotification(context: Context) {
    NotificationHelper.sendTestNotification(context)
  }

  fun saveBirthday(
    id: Long = 0,
    name: String,
    birthMonth: Int,
    birthDay: Int,
    birthYear: Int?,
    relationship: String,
    phoneNumber: String,
    photoUri: String?,
    avatarColorHex: String,
    reminderLeadDays: Int,
    giftIdeas: String,
    notes: String,
    onComplete: (Long) -> Unit
  ) {
    viewModelScope.launch {
      val contact = BirthdayContact(
        id = id,
        name = name.trim(),
        birthMonth = birthMonth,
        birthDay = birthDay,
        birthYear = birthYear,
        relationship = relationship,
        phoneNumber = phoneNumber.trim(),
        photoUri = photoUri,
        avatarColorHex = avatarColorHex,
        reminderLeadDays = reminderLeadDays,
        giftIdeas = giftIdeas.trim(),
        notes = notes.trim()
      )
      val resultId = if (id == 0L) {
        repository.insertBirthday(contact)
      } else {
        repository.updateBirthday(contact)
        id
      }
      onComplete(resultId)
    }
  }

  fun deleteBirthday(id: Long, onComplete: () -> Unit = {}) {
    viewModelScope.launch {
      repository.deleteById(id)
      onComplete()
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      repository.resetToSampleData()
    }
  }
}
