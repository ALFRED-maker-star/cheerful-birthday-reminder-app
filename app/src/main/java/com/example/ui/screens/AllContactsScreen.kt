package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BirthdayContact
import com.example.ui.BirthdayViewModel
import com.example.ui.components.BalloonIcon
import com.example.ui.components.ContactAvatar
import com.example.ui.components.DaysUntilBadge
import com.example.ui.components.RelationshipTag
import com.example.ui.theme.DarkText
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.SoftPinkBackground
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun AllContactsScreen(
  viewModel: BirthdayViewModel,
  onNavigateToDetail: (Long) -> Unit,
  onNavigateToAdd: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredContacts by viewModel.filteredContacts.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val today = LocalDate.now()

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val categories = listOf("All", "Family", "Friend", "Partner", "Colleague", "Other")

  // Group contacts by first letter
  val groupedContacts = filteredContacts.groupBy { contact ->
    contact.name.firstOrNull()?.uppercaseChar() ?: '#'
  }.toSortedMap()

  // Alphabet index list
  val alphabet = ('A'..'Z').toList()

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
          .testTag("fab_add_contact")
          .shadow(6.dp, CircleShape)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Contact")
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
      ) {
        Text(
          text = "All Contacts",
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold,
          color = DarkText
        )
        Text(
          text = "${filteredContacts.size} people in your celebration circle",
          fontSize = 13.sp,
          color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search by name, note, or relationship...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPink)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color(0xFF94A3B8))
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_search_contacts"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryPink,
            unfocusedBorderColor = Color(0xFFFFE4E6),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Relationship Filter Pills
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          items(categories) { cat ->
            val isSelected = (selectedCategory ?: "All").equals(cat, ignoreCase = true)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) PrimaryPink else Color.White)
                .clickable { viewModel.setCategoryFilter(if (cat == "All") null else cat) }
                .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else DarkText
              )
            }
          }
        }
      }

      // Main list with A-Z side index
      Box(modifier = Modifier.fillMaxSize()) {
        if (groupedContacts.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              BalloonIcon(size = 48.dp, color = PrimaryPink)
              Spacer(modifier = Modifier.height(12.dp))
              Text("No contacts found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
              Text("Try searching for someone else or tap + to add", fontSize = 13.sp, color = Color(0xFF64748B))
            }
          }
        } else {
          LazyColumn(
            state = listState,
            modifier = Modifier
              .fillMaxSize()
              .padding(end = 26.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 8.dp, bottom = 96.dp)
          ) {
            groupedContacts.forEach { (letter, contacts) ->
              item(key = "header_$letter") {
                Text(
                  text = letter.toString(),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = PrimaryPink,
                  modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
                )
              }

              items(contacts, key = { it.id }) { contact ->
                ContactListItem(
                  contact = contact,
                  today = today,
                  onClick = { onNavigateToDetail(contact.id) }
                )
              }
            }
          }

          // Alphabet Letter Index Rail
          Column(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .fillMaxHeight()
              .padding(end = 4.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            alphabet.forEach { letter ->
              val hasEntries = groupedContacts.containsKey(letter)
              Text(
                text = letter.toString(),
                fontSize = 10.sp,
                fontWeight = if (hasEntries) FontWeight.Bold else FontWeight.Normal,
                color = if (hasEntries) PrimaryPink else Color(0xFFCBD5E1),
                modifier = Modifier
                  .clickable(enabled = hasEntries) {
                    coroutineScope.launch {
                      var targetIndex = 0
                      for ((l, cList) in groupedContacts) {
                        if (l == letter) break
                        targetIndex += (1 + cList.size)
                      }
                      listState.animateScrollToItem(targetIndex)
                    }
                  }
                  .padding(vertical = 1.dp, horizontal = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ContactListItem(
  contact: BirthdayContact,
  today: LocalDate,
  onClick: () -> Unit
) {
  val days = contact.daysUntil(today)
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("contact_item_${contact.id}"),
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
      ContactAvatar(contact = contact, size = 44.dp)

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = contact.name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = DarkText
          )
          Spacer(modifier = Modifier.width(6.dp))
          RelationshipTag(relationship = contact.relationship)
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
