package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BirthdayContact
import com.example.ui.BirthdayViewModel
import com.example.ui.components.CakeIcon
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DarkText
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPinkContainer
import com.example.ui.theme.SoftPinkBackground
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditBirthdayScreen(
  viewModel: BirthdayViewModel,
  birthdayId: Long?,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var name by remember { mutableStateOf("") }
  var nameError by remember { mutableStateOf(false) }

  val today = LocalDate.now()
  var birthMonth by remember { mutableIntStateOf(today.monthValue) }
  var birthDay by remember { mutableIntStateOf(today.dayOfMonth) }
  var hasBirthYear by remember { mutableStateOf(true) }
  var birthYearText by remember { mutableStateOf((today.year - 25).toString()) }

  var relationship by remember { mutableStateOf("Friend") }
  var phoneNumber by remember { mutableStateOf("") }
  var photoUri by remember { mutableStateOf<String?>(null) }
  var avatarColorHex by remember { mutableStateOf("#EC4899") }
  var reminderLeadDays by remember { mutableIntStateOf(1) }
  var giftIdeas by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  // Load existing contact if editing
  LaunchedEffect(birthdayId) {
    if (birthdayId != null && birthdayId > 0) {
      val contact = viewModel.allBirthdays.value.find { it.id == birthdayId }
      if (contact != null) {
        name = contact.name
        birthMonth = contact.birthMonth
        birthDay = contact.birthDay
        if (contact.birthYear != null) {
          hasBirthYear = true
          birthYearText = contact.birthYear.toString()
        } else {
          hasBirthYear = false
          birthYearText = ""
        }
        relationship = contact.relationship
        phoneNumber = contact.phoneNumber
        photoUri = contact.photoUri
        avatarColorHex = contact.avatarColorHex
        reminderLeadDays = contact.reminderLeadDays
        giftIdeas = contact.giftIdeas
        notes = contact.notes
      }
    }
  }

  // Photo picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      photoUri = uri.toString()
    }
  }

  val relationships = listOf("Friend", "Family", "Partner", "Colleague", "Other")
  val reminderOptions = listOf(
    0 to "On the day",
    1 to "1 day before",
    3 to "3 days before",
    7 to "1 week before",
    14 to "2 weeks before"
  )

  val avatarColors = listOf(
    "#EC4899", "#FBBF24", "#38BDF8", "#A855F7", "#34D399", "#FB7185", "#FB923C", "#6366F1"
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = SoftPinkBackground,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (birthdayId == null || birthdayId <= 0) "Add Birthday 🎂" else "Edit Birthday",
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontSize = 20.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back")) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = DarkText
            )
          }
        },
        actions = {
          if (birthdayId != null && birthdayId > 0) {
            IconButton(
              onClick = {
                viewModel.deleteBirthday(birthdayId) {
                  onNavigateBack()
                }
              },
              modifier = Modifier.testTag("btn_delete_birthday")
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFEF4444)
              )
            }
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
      contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

      // Avatar & Photo Picker Card
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
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(2.5.dp, PrimaryPink, CircleShape)
                .clickable {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
                .testTag("btn_photo_upload"),
              contentAlignment = Alignment.Center
            ) {
              if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                  model = photoUri,
                  contentDescription = "Selected photo",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                val parsedColor = try {
                  Color(android.graphics.Color.parseColor(avatarColorHex))
                } catch (_: Exception) {
                  PrimaryPink
                }
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(parsedColor),
                  contentAlignment = Alignment.Center
                ) {
                  val initials = name.trim().split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .map { it.first().uppercaseChar() }
                    .joinToString("")
                    .ifEmpty { "🎂" }
                  Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                  )
                }
              }

              // Camera icon badge
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(AccentGold)
                  .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.CameraAlt,
                  contentDescription = "Pick Photo",
                  tint = Color(0xFF78350F),
                  modifier = Modifier.size(15.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (photoUri != null) "Tap photo to change" else "Upload photo or pick avatar color",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Avatar Color Palette
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              avatarColors.forEach { hex ->
                val c = Color(android.graphics.Color.parseColor(hex))
                val isSelected = avatarColorHex.equals(hex, ignoreCase = true)
                Box(
                  modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(c)
                    .clickable { avatarColorHex = hex }
                    .border(
                      width = if (isSelected) 2.5.dp else 0.dp,
                      color = if (isSelected) DarkText else Color.Transparent,
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Icon(
                      Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Name & Phone Input
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            OutlinedTextField(
              value = name,
              onValueChange = {
                name = it
                if (it.isNotBlank()) nameError = false
              },
              label = { Text("Full Name *") },
              placeholder = { Text("e.g. Maya Lin") },
              isError = nameError,
              supportingText = {
                if (nameError) Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_name"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink
              )
            )

            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { phoneNumber = it },
              label = { Text("Phone Number (for quick call / SMS)") },
              placeholder = { Text("+1 (555) 123-4567") },
              leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryPink)
              },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_phone"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink
              )
            )
          }
        }
      }

      // Date of Birth Card
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CakeIcon(size = 20.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Date of Birth", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
            }

            // Month and Day pickers
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Month Selector
              var monthExpanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                expanded = monthExpanded,
                onExpandedChange = { monthExpanded = !monthExpanded },
                modifier = Modifier.weight(1.4f)
              ) {
                OutlinedTextField(
                  value = Month.of(birthMonth).getDisplayName(TextStyle.FULL, Locale.getDefault()),
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("Month") },
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    focusedLabelColor = PrimaryPink
                  )
                )
                ExposedDropdownMenu(
                  expanded = monthExpanded,
                  onDismissRequest = { monthExpanded = false }
                ) {
                  (1..12).forEach { m ->
                    DropdownMenuItem(
                      text = { Text(Month.of(m).getDisplayName(TextStyle.FULL, Locale.getDefault())) },
                      onClick = {
                        birthMonth = m
                        monthExpanded = false
                      }
                    )
                  }
                }
              }

              // Day Selector
              var dayExpanded by remember { mutableStateOf(false) }
              val maxDays = Month.of(birthMonth).length(false)
              ExposedDropdownMenuBox(
                expanded = dayExpanded,
                onExpandedChange = { dayExpanded = !dayExpanded },
                modifier = Modifier.weight(1f)
              ) {
                OutlinedTextField(
                  value = birthDay.toString(),
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("Day") },
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                  modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    focusedLabelColor = PrimaryPink
                  )
                )
                ExposedDropdownMenu(
                  expanded = dayExpanded,
                  onDismissRequest = { dayExpanded = false }
                ) {
                  (1..maxDays).forEach { d ->
                    DropdownMenuItem(
                      text = { Text(d.toString()) },
                      onClick = {
                        birthDay = d
                        dayExpanded = false
                      }
                    )
                  }
                }
              }
            }

            // Birth Year Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Include Birth Year", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                  "Enables exact age & milestone countdowns",
                  fontSize = 11.sp,
                  color = Color(0xFF64748B)
                )
              }
              Switch(
                checked = hasBirthYear,
                onCheckedChange = { hasBirthYear = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = PrimaryPink
                )
              )
            }

            if (hasBirthYear) {
              OutlinedTextField(
                value = birthYearText,
                onValueChange = { birthYearText = it.filter { ch -> ch.isDigit() }.take(4) },
                label = { Text("Birth Year") },
                placeholder = { Text("e.g. 1998") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_year"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryPink,
                  focusedLabelColor = PrimaryPink
                )
              )
            }
          }
        }
      }

      // Relationship Category
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("Relationship Category", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              relationships.forEach { rel ->
                val isSelected = relationship.equals(rel, ignoreCase = true)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) PrimaryPink else Color(0xFFF1F5F9))
                    .clickable { relationship = rel }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = rel,
                    color = if (isSelected) Color.White else DarkText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                  )
                }
              }
            }
          }
        }
      }

      // Reminder Lead-Time Selector
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔔", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Reminder Lead-Time", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
            }

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              reminderOptions.forEach { (days, label) ->
                val isSelected = reminderLeadDays == days
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) AccentGold else Color(0xFFF1F5F9))
                    .border(
                      width = if (isSelected) 1.dp else 0.dp,
                      color = if (isSelected) Color(0xFFD97706) else Color.Transparent,
                      shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { reminderLeadDays = days }
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

      // Notes & Gift Ideas
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            OutlinedTextField(
              value = giftIdeas,
              onValueChange = { giftIdeas = it },
              label = { Text("🎁 Gift Ideas & Wishlist") },
              placeholder = { Text("e.g. Polaroid film, Lavender scented candle, books...") },
              minLines = 2,
              maxLines = 4,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_gift_ideas"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink
              )
            )

            OutlinedTextField(
              value = notes,
              onValueChange = { notes = it },
              label = { Text("📝 Notes & Memories") },
              placeholder = { Text("e.g. Loves matcha lattes, favorite dessert is strawberry shortcake...") },
              minLines = 2,
              maxLines = 4,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_notes"),
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                focusedLabelColor = PrimaryPink
              )
            )
          }
        }
      }

      // Save Button
      item {
        Button(
          onClick = {
            if (name.isBlank()) {
              nameError = true
              return@Button
            }
            val parsedYear = if (hasBirthYear) birthYearText.toIntOrNull() else null
            viewModel.saveBirthday(
              id = birthdayId ?: 0L,
              name = name,
              birthMonth = birthMonth,
              birthDay = birthDay,
              birthYear = parsedYear,
              relationship = relationship,
              phoneNumber = phoneNumber,
              photoUri = photoUri,
              avatarColorHex = avatarColorHex,
              reminderLeadDays = reminderLeadDays,
              giftIdeas = giftIdeas,
              notes = notes
            ) {
              onNavigateBack()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("btn_save_birthday"),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text(
            text = if (birthdayId == null || birthdayId <= 0) "Save Birthday 🎉" else "Update Changes ✨",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
