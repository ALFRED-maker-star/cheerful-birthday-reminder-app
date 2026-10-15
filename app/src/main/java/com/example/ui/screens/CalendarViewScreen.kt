package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BirthdayContact
import com.example.ui.BirthdayViewModel
import com.example.ui.components.BalloonIcon
import com.example.ui.components.CakeIcon
import com.example.ui.components.ContactAvatar
import com.example.ui.components.DaysUntilBadge
import com.example.ui.components.RelationshipTag
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkText
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPinkContainer
import com.example.ui.theme.SoftPinkBackground
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarViewScreen(
  viewModel: BirthdayViewModel,
  onNavigateToDetail: (Long) -> Unit,
  onNavigateToAdd: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allBirthdays by viewModel.allBirthdays.collectAsState()
  val currentMonth by viewModel.currentCalendarMonth.collectAsState()
  val selectedDate by viewModel.selectedCalendarDate.collectAsState()
  val today = LocalDate.now()

  // Birthdays in current visible month
  val monthBirthdays = allBirthdays.filter { it.birthMonth == currentMonth.monthValue }
    .sortedBy { it.birthDay }

  // Birthdays on selected date
  val selectedDateBirthdays = selectedDate?.let { date ->
    allBirthdays.filter { it.birthMonth == date.monthValue && it.birthDay == date.dayOfMonth }
  } ?: emptyList()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = SoftPinkBackground,
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNavigateToAdd,
        containerColor = PrimaryPink,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .testTag("fab_calendar_add")
          .shadow(6.dp, CircleShape)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Birthday")
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // Month Navigator Header
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
        ) {
          Text(
            text = "Birthday Calendar 📅",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
          )
          Text(
            text = "${monthBirthdays.size} birthdays in ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Calendar Card
          Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // Month & Year header with arrows
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                IconButton(
                  onClick = { viewModel.prevCalendarMonth() },
                  modifier = Modifier.testTag("btn_prev_month")
                ) {
                  Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month", tint = PrimaryPink)
                }

                Text(
                  text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp,
                  color = DarkText
                )

                IconButton(
                  onClick = { viewModel.nextCalendarMonth() },
                  modifier = Modifier.testTag("btn_next_month")
                ) {
                  Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month", tint = PrimaryPink)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Weekdays Header
              Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                daysOfWeek.forEach { dayName ->
                  Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Calendar Month Grid
              CalendarMonthGrid(
                currentMonth = currentMonth,
                today = today,
                selectedDate = selectedDate,
                birthdays = allBirthdays,
                onDateSelected = { date -> viewModel.selectCalendarDate(date) }
              )
            }
          }
        }
      }

      // Selected Day Details Header
      item {
        val dateLabel = selectedDate?.let {
          it.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d"))
        } ?: "Selected Day"

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            CakeIcon(size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Birthdays on $dateLabel",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = DarkText
            )
          }

          if (selectedDateBirthdays.isNotEmpty()) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AccentGold)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${selectedDateBirthdays.size} 🎉",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F)
              )
            }
          }
        }
      }

      // Birthdays on Selected Date
      if (selectedDateBirthdays.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              BalloonIcon(size = 28.dp, color = Color(0xFFCBD5E1))
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "No birthdays recorded on this date.",
                color = Color(0xFF64748B),
                fontSize = 13.sp
              )
            }
          }
        }
      } else {
        items(selectedDateBirthdays, key = { it.id }) { contact ->
          CalendarContactCard(
            contact = contact,
            today = today,
            onClick = { onNavigateToDetail(contact.id) }
          )
        }
      }

      // All birthdays in this month list
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "All in ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "(${monthBirthdays.size})",
            fontSize = 14.sp,
            color = PrimaryPink,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (monthBirthdays.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
          ) {
            Text(
              text = "No birthdays in this month yet.",
              color = Color(0xFF94A3B8),
              fontSize = 13.sp,
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      } else {
        items(monthBirthdays, key = { "month_${it.id}" }) { contact ->
          CalendarContactCard(
            contact = contact,
            today = today,
            onClick = { onNavigateToDetail(contact.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun CalendarMonthGrid(
  currentMonth: YearMonth,
  today: LocalDate,
  selectedDate: LocalDate?,
  birthdays: List<BirthdayContact>,
  onDateSelected: (LocalDate) -> Unit
) {
  val firstDayOfMonth = currentMonth.atDay(1)
  val daysInMonth = currentMonth.lengthOfMonth()

  // Day of week offset (Sunday = 0, Monday = 1, etc.)
  val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7

  val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7

  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    for (row in 0 until (totalCells / 7)) {
      Row(modifier = Modifier.fillMaxWidth()) {
        for (col in 0 until 7) {
          val cellIndex = row * 7 + col
          val dayNumber = cellIndex - dayOfWeekOffset + 1

          if (dayNumber in 1..daysInMonth) {
            val date = currentMonth.atDay(dayNumber)
            val isCurrentDay = date == today
            val isSelected = date == selectedDate
            val dayBirthdays = birthdays.filter {
              it.birthMonth == date.monthValue && it.birthDay == date.dayOfMonth
            }
            val hasBirthday = dayBirthdays.isNotEmpty()

            Box(
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  when {
                    isSelected -> PrimaryPinkContainer
                    isCurrentDay -> Color(0xFFFEF3C7)
                    else -> Color.Transparent
                  }
                )
                .border(
                  width = if (isSelected) 1.5.dp else if (isCurrentDay) 1.dp else 0.dp,
                  color = if (isSelected) PrimaryPink else if (isCurrentDay) AccentGold else Color.Transparent,
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable { onDateSelected(date) },
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(
                  text = dayNumber.toString(),
                  fontSize = 13.sp,
                  fontWeight = if (isCurrentDay || isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = when {
                    isSelected -> PrimaryPink
                    isCurrentDay -> Color(0xFFB45309)
                    else -> DarkText
                  }
                )

                if (hasBirthday) {
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 2.dp)
                  ) {
                    dayBirthdays.take(2).forEach {
                      Box(
                        modifier = Modifier
                          .size(5.dp)
                          .clip(CircleShape)
                          .background(PrimaryPink)
                      )
                    }
                    if (dayBirthdays.size > 2) {
                      Box(
                        modifier = Modifier
                          .size(5.dp)
                          .clip(CircleShape)
                          .background(AccentGold)
                      )
                    }
                  }
                }
              }
            }
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
private fun CalendarContactCard(
  contact: BirthdayContact,
  today: LocalDate,
  onClick: () -> Unit
) {
  val days = contact.daysUntil(today)
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ContactAvatar(contact = contact, size = 42.dp)
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
          Spacer(modifier = Modifier.width(6.dp))
          RelationshipTag(contact.relationship)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "🎂 ${contact.formattedBirthday(includeYear = false)}",
          fontSize = 12.sp,
          color = Color(0xFF64748B)
        )
      }
      DaysUntilBadge(daysUntil = days)
    }
  }
}
