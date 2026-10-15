package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.PrimaryPinkContainer
import com.example.ui.theme.SoftPinkBackground
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
  viewModel: BirthdayViewModel,
  contactId: Long,
  onNavigateBack: () -> Unit,
  onNavigateToEdit: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allBirthdays by viewModel.allBirthdays.collectAsState()
  val contact = allBirthdays.find { it.id == contactId }

  var showDeleteDialog by remember { mutableStateOf(false) }
  var showWishTemplatesDialog by remember { mutableStateOf(false) }

  val today = LocalDate.now()

  if (contact == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(SoftPinkBackground),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Contact not found", color = DarkText, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onNavigateBack) {
          Text("Go Back")
        }
      }
    }
    return
  }

  val isToday = contact.isToday(today)
  val daysUntil = contact.daysUntil(today)
  val turningAge = contact.turningAge(today)
  val currentAge = contact.currentAge(today)
  val (zodiacName, zodiacEmoji) = contact.zodiacSign()

  val wishTemplates = listOf(
    "Happy Birthday, ${contact.name}! 🎂🎉 Wishing you a wonderful day filled with laughter and love!",
    "Happy Birthday! 🎈 May this upcoming year bring you endless happiness, good health, and success!",
    "Wishing the happiest of birthdays to an amazing friend! 🎁 Hope you celebrate in high style!",
    "Cheers to another fantastic year around the sun! 🥂 Happy Birthday, ${contact.name}!"
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = SoftPinkBackground,
    topBar = {
      TopAppBar(
        title = { Text(contact.name, fontWeight = FontWeight.Bold, color = DarkText) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_detail_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkText)
          }
        },
        actions = {
          IconButton(
            onClick = { onNavigateToEdit(contact.id) },
            modifier = Modifier.testTag("btn_detail_edit")
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryPink)
          }
          IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.testTag("btn_detail_delete")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftPinkBackground)
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // Large Avatar and Profile Spotlight
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.fillMaxWidth()) {
            if (isToday) {
              ConfettiOverlay(
                modifier = Modifier.matchParentSize(),
                particleCount = 18
              )
            }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              ContactAvatar(
                contact = contact,
                size = 100.dp,
                showCelebrationBorder = isToday || daysUntil <= 7
              )

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = contact.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
              )

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                RelationshipTag(relationship = contact.relationship)

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "$zodiacEmoji $zodiacName",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Birthday Spotlight Banner
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(18.dp))
                  .background(
                    if (isToday) {
                      Brush.horizontalGradient(listOf(PrimaryPink, AccentGold))
                    } else {
                      Brush.horizontalGradient(listOf(Color(0xFFFFF1F8), Color(0xFFFEF3C7)))
                    }
                  )
                  .padding(16.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "📅 ${contact.formattedBirthday(includeYear = true)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.White else DarkText
                  )

                  Spacer(modifier = Modifier.height(4.dp))

                  if (turningAge != null) {
                    Text(
                      text = if (isToday) "Turning $turningAge Today! 🎂" else "Turning $turningAge • In $daysUntil days",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isToday) Color.White else PrimaryPink
                    )
                  } else {
                    Text(
                      text = if (isToday) "Celebrating Today! 🎉" else "Birthday in $daysUntil days",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isToday) Color.White else PrimaryPink
                    )
                  }

                  if (currentAge != null && !isToday) {
                    Text(
                      text = "Currently $currentAge years old",
                      fontSize = 12.sp,
                      color = Color(0xFF64748B)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Quick Actions Row (Call, Message, Share Wish)
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Call Action
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable {
                  if (contact.phoneNumber.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                    context.startActivity(intent)
                  }
                }
                .testTag("action_call")
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(PrimaryPinkContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = PrimaryPink, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Call", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            }

            // Message Action
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { showWishTemplatesDialog = true }
                .testTag("action_message")
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(AccentGoldContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Message", tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Wish", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            }

            // Share Card Action
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable {
                  val shareText = "Celebrating ${contact.name}'s Birthday on ${contact.formattedBirthday(false)}! 🎉🎂"
                  val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                  }
                  context.startActivity(Intent.createChooser(intent, "Share Birthday"))
                }
                .testTag("action_share")
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF0284C7), modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            }
          }
        }
      }

      // Gift Ideas Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                GiftIcon(size = 20.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gift Ideas & Wishlist", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
              }
              IconButton(onClick = { onNavigateToEdit(contact.id) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit gift ideas", tint = PrimaryPink, modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (contact.giftIdeas.isNotBlank()) {
              Text(
                text = contact.giftIdeas,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 22.sp
              )
            } else {
              Text(
                text = "No gift ideas saved yet. Tap edit to jot down wishlist items or surprise ideas!",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }
        }
      }

      // Notes & Memories Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("📝", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Notes & Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (contact.notes.isNotBlank()) {
              Text(
                text = contact.notes,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 22.sp
              )
            } else {
              Text(
                text = "No personal notes added yet.",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
              )
            }

            if (contact.phoneNumber.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(contact.phoneNumber, fontSize = 13.sp, color = Color(0xFF64748B))
              }
            }
          }
        }
      }

      // Reminder Setting Indicator
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = AccentGold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              val reminderText = when (contact.reminderLeadDays) {
                0 -> "On the birthday"
                1 -> "1 day before"
                3 -> "3 days before"
                7 -> "1 week before"
                14 -> "2 weeks before"
                else -> "${contact.reminderLeadDays} days before"
              }
              Text("Reminder Set", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
              Text("Notifies you $reminderText at 9:00 AM", fontSize = 12.sp, color = Color(0xFF64748B))
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete Birthday?") },
      text = { Text("Are you sure you want to remove ${contact.name}'s birthday from Celebrate?") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            viewModel.deleteBirthday(contact.id) {
              onNavigateBack()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Message Wishes Dialog
  if (showWishTemplatesDialog) {
    AlertDialog(
      onDismissRequest = { showWishTemplatesDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CakeIcon(size = 20.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Send Birthday Wish")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Choose a cheerful template to send via SMS or WhatsApp:", fontSize = 13.sp, color = Color(0xFF64748B))
          wishTemplates.forEach { template ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  showWishTemplatesDialog = false
                  val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${contact.phoneNumber.ifBlank { "" }}")
                    putExtra("sms_body", template)
                  }
                  try {
                    context.startActivity(smsIntent)
                  } catch (_: Exception) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "text/plain"
                      putExtra(Intent.EXTRA_TEXT, template)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Send Wish"))
                  }
                },
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F8))
            ) {
              Text(
                text = template,
                modifier = Modifier.padding(12.dp),
                fontSize = 13.sp,
                color = DarkText
              )
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showWishTemplatesDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}
