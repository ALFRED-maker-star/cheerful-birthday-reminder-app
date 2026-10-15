package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BirthdayContact
import com.example.ui.BirthdayViewModel
import com.example.ui.components.BalloonIcon
import com.example.ui.components.CakeIcon
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.ContactAvatar
import com.example.ui.components.DaysUntilBadge
import com.example.ui.components.GiftIcon
import com.example.ui.components.RelationshipTag
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldContainer
import com.example.ui.theme.DarkText
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPinkDark
import com.example.ui.theme.SoftPinkBackground
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
  viewModel: BirthdayViewModel,
  onNavigateToDetail: (Long) -> Unit,
  onNavigateToAdd: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allBirthdays by viewModel.allBirthdays.collectAsState()
  val today = LocalDate.now()

  // Calculate Today's Birthdays
  val todayBirthdays = allBirthdays.filter { it.isToday(today) }

  // Upcoming This Month
  val upcomingThisMonth = allBirthdays
    .filter { it.isThisMonth(today) && !it.isToday(today) }
    .sortedBy { it.daysUntil(today) }

  // Upcoming Beyond this month
  val upcomingLater = allBirthdays
    .filter { !it.isThisMonth(today) && !it.isToday(today) }
    .sortedBy { it.daysUntil(today) }

  val formattedToday = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

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
          .testTag("fab_add_birthday")
          .shadow(6.dp, CircleShape)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Birthday", modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // Top Header
      item {
        HomeHeaderSection(
          formattedToday = formattedToday,
          totalCount = allBirthdays.size,
          monthCount = allBirthdays.count { it.isThisMonth(today) },
          todayCount = todayBirthdays.size
        )
      }

      // 1. Today's Birthdays Highlighted Banner (if any)
      if (todayBirthdays.isNotEmpty()) {
        item {
          TodayCelebrationBanner(
            todayContacts = todayBirthdays,
            onContactClick = onNavigateToDetail,
            onCallClick = { phone ->
              if (phone.isNotBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(intent)
              }
            },
            onMessageClick = { contact ->
              val turning = contact.turningAge(today)?.let { " $it" } ?: ""
              val msg = "Happy$turning Birthday, ${contact.name}! 🎂🎉 Wishing you an amazing year ahead filled with joy!"
              val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${contact.phoneNumber.ifBlank { "" }}")
                putExtra("sms_body", msg)
              }
              try {
                context.startActivity(smsIntent)
              } catch (_: Exception) {
                // Fallback to share
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, msg)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Send Birthday Wish"))
              }
            }
          )
        }
      }

      // 2. "Upcoming This Month" Section
      item {
        SectionTitleHeader(
          title = "Upcoming This Month",
          subtitle = "${upcomingThisMonth.size} birthdays to celebrate",
          icon = { BalloonIcon(color = AccentGold, size = 22.dp) }
        )
      }

      if (upcomingThisMonth.isEmpty()) {
        item {
          EmptyUpcomingCard(
            text = "No more birthdays this month! Check out later upcoming dates or add new friends.",
            onAddClick = onNavigateToAdd
          )
        }
      } else {
        items(upcomingThisMonth, key = { it.id }) { contact ->
          BirthdayCardItem(
            contact = contact,
            today = today,
            onClick = { onNavigateToDetail(contact.id) }
          )
        }
      }

      // 3. Later Upcoming (Next Months)
      if (upcomingLater.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(16.dp))
          SectionTitleHeader(
            title = "Coming Up Next",
            subtitle = "${upcomingLater.size} celebrations ahead",
            icon = { GiftIcon(size = 20.dp) }
          )
        }

        items(upcomingLater, key = { it.id }) { contact ->
          BirthdayCardItem(
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
private fun HomeHeaderSection(
  formattedToday: String,
  totalCount: Int,
  monthCount: Int,
  todayCount: Int
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = formattedToday.uppercase(),
          color = PrimaryPink,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "Celebrate 🎈",
          color = DarkText,
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }

      // Cute Cake badge
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(0xFFFCE7F3)),
        contentAlignment = Alignment.Center
      ) {
        CakeIcon(size = 28.dp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Quick Stats Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatCard(
        label = "Today",
        count = todayCount,
        highlight = todayCount > 0,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        label = "This Month",
        count = monthCount,
        highlight = false,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        label = "Total Tracked",
        count = totalCount,
        highlight = false,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun StatCard(
  label: String,
  count: Int,
  highlight: Boolean,
  modifier: Modifier = Modifier
) {
  val bg = if (highlight) AccentGoldContainer else Color.White
  val border = if (highlight) AccentGold else Color(0xFFFFE4E6)
  val textColor = if (highlight) Color(0xFF78350F) else DarkText

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = bg),
    border = androidx.compose.foundation.BorderStroke(1.dp, border)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (highlight) Color(0xFFB45309) else PrimaryPink
      )
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = textColor
      )
    }
  }
}

@Composable
private fun TodayCelebrationBanner(
  todayContacts: List<BirthdayContact>,
  onContactClick: (Long) -> Unit,
  onCallClick: (String) -> Unit,
  onMessageClick: (BirthdayContact) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp)
      .shadow(8.dp, RoundedCornerShape(20.dp))
      .testTag("banner_today_birthdays"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.linearGradient(
            colors = listOf(PrimaryPink, PrimaryPinkDark, Color(0xFF9D174D))
          )
        )
    ) {
      // Confetti animated overlay on top of banner
      ConfettiOverlay(
        modifier = Modifier.matchParentSize(),
        particleCount = 24
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎉", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "TODAY'S CELEBRATION!",
              color = AccentGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(AccentGold)
              .padding(horizontal = 10.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Today! 🎂",
              color = Color(0xFF78350F),
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        todayContacts.forEach { contact ->
          val turning = contact.turningAge()?.let { "turns $it today!" } ?: "celebrating today!"

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White.copy(alpha = 0.15f))
              .clickable { onContactClick(contact.id) }
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            ContactAvatar(
              contact = contact,
              size = 54.dp,
              showCelebrationBorder = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = contact.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "🎂 ${contact.relationship} • $turning",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quick Action buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { onMessageClick(contact) },
              modifier = Modifier
                .weight(1f)
                .testTag("btn_send_wish_${contact.id}"),
              colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(vertical = 10.dp)
            ) {
              Icon(
                Icons.Default.Send,
                contentDescription = "Send Wish",
                tint = Color(0xFF78350F),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Send Wish", color = Color(0xFF78350F), fontWeight = FontWeight.Bold)
            }

            if (contact.phoneNumber.isNotBlank()) {
              OutlinedButton(
                onClick = { onCallClick(contact.phoneNumber) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
              ) {
                Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Call", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionTitleHeader(
  title: String,
  subtitle: String,
  icon: @Composable () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      icon()
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          color = DarkText,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = subtitle,
          color = Color(0xFF64748B),
          fontSize = 12.sp
        )
      }
    }
  }
}

@Composable
fun BirthdayCardItem(
  contact: BirthdayContact,
  today: LocalDate,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val daysUntil = contact.daysUntil(today)
  val turningAge = contact.turningAge(today)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("birthday_card_${contact.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ContactAvatar(
        contact = contact,
        size = 50.dp,
        showCelebrationBorder = daysUntil <= 7
      )

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = contact.name,
            style = MaterialTheme.typography.titleMedium,
            color = DarkText,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          RelationshipTag(relationship = contact.relationship)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "📅 ${contact.formattedBirthday(includeYear = false)}",
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
          if (turningAge != null) {
            Text(
              text = " • Turning $turningAge",
              color = PrimaryPink,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      DaysUntilBadge(daysUntil = daysUntil)
    }
  }
}

@Composable
private fun EmptyUpcomingCard(
  text: String,
  onAddClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 10.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(Color(0xFFFFF1F8)),
        contentAlignment = Alignment.Center
      ) {
        BalloonIcon(size = 32.dp, color = PrimaryPink)
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFF64748B),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(12.dp))
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Add a Birthday", fontWeight = FontWeight.Bold)
      }
    }
  }
}
