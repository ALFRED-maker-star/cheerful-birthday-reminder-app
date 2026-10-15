package com.example.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BirthdayViewModel
import com.example.ui.components.BalloonIcon
import com.example.ui.components.CakeIcon
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkText
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SoftPinkBackground
import com.example.util.NotificationHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
  viewModel: BirthdayViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
  val defaultReminderLeadDays by viewModel.defaultReminderLeadDays.collectAsState()

  var showResetConfirmDialog by remember { mutableStateOf(false) }

  // Permission launcher for Android 13+
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.setNotificationsEnabled(true)
      Toast.makeText(context, "Notifications enabled! 🎂", Toast.LENGTH_SHORT).show()
    } else {
      viewModel.setNotificationsEnabled(false)
      Toast.makeText(context, "Notification permission was denied", Toast.LENGTH_SHORT).show()
    }
  }

  val reminderOptions = listOf(
    0 to "On the day",
    1 to "1 day before",
    3 to "3 days before",
    7 to "1 week before",
    14 to "2 weeks before"
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = SoftPinkBackground
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      item {
        Column {
          Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
          )
          Text(
            text = "Preferences and reminder alerts",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      // Notifications Toggle Card
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
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1F8)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = PrimaryPink,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Birthday Notifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkText
                  )
                  Text(
                    text = "Get countdown alerts before birthdays",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                  )
                }
              }

              Switch(
                checked = notificationsEnabled,
                onCheckedChange = { isChecked ->
                  if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !NotificationHelper.hasNotificationPermission(context)) {
                      notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                      viewModel.setNotificationsEnabled(true)
                    }
                  } else {
                    viewModel.setNotificationsEnabled(false)
                  }
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = PrimaryPink
                ),
                modifier = Modifier.testTag("switch_notifications")
              )
            }

            if (notificationsEnabled) {
              Spacer(modifier = Modifier.height(14.dp))
              OutlinedButton(
                onClick = {
                  viewModel.sendTestNotification(context)
                  Toast.makeText(context, "Test notification sent! Check notification tray 🎈", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_test_notification"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPink)
              ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Test Reminder Notification", color = PrimaryPink, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      // Default Reminder Lead-Time
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
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.Timer,
                  contentDescription = null,
                  tint = Color(0xFFB45309),
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Default Reminder Lead-Time",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = DarkText
                )
                Text(
                  text = "Initial suggestion when adding new birthdays",
                  fontSize = 12.sp,
                  color = Color(0xFF64748B)
                )
              }
            }

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              reminderOptions.forEach { (days, label) ->
                val isSelected = defaultReminderLeadDays == days
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) AccentGold else Color(0xFFF1F5F9))
                    .clickable { viewModel.setDefaultReminderLeadDays(days) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = label,
                    color = if (isSelected) Color(0xFF78350F) else DarkText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }
        }
      }

      // Sample Data Management
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
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.Refresh,
                  contentDescription = null,
                  tint = Color(0xFF0284C7),
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Sample Data", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                Text("Restore cheerful starter birthdays & demo contacts", fontSize = 12.sp, color = Color(0xFF64748B))
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { showResetConfirmDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_restore_sample_data")
            ) {
              Text("Reset to Sample Birthdays", color = DarkText, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      // About App
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
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF1F8)),
              contentAlignment = Alignment.Center
            ) {
              CakeIcon(size = 36.dp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Celebrate", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text("Version 1.0 • Never miss a milestone 🎈", fontSize = 12.sp, color = Color(0xFF64748B))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              BalloonIcon(size = 20.dp, color = PrimaryPink)
              Text("Designed to make every birthday memorable", fontSize = 12.sp, color = Color(0xFF94A3B8))
              BalloonIcon(size = 20.dp, color = AccentGold)
            }
          }
        }
      }
    }
  }

  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Restore Sample Birthdays?") },
      text = { Text("This will reset your contacts to the cheerful sample birthday roster (including Maya's birthday today!).") },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            viewModel.resetToSampleData()
            Toast.makeText(context, "Sample birthdays restored! 🎉", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
        ) {
          Text("Restore", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
