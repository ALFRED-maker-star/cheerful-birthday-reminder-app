package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BirthdayContact
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryPink

@Composable
fun ContactAvatar(
  contact: BirthdayContact,
  size: Dp = 48.dp,
  showCelebrationBorder: Boolean = false,
  modifier: Modifier = Modifier
) {
  val initials = contact.name.trim().split(" ")
    .filter { it.isNotEmpty() }
    .take(2)
    .map { it.first().uppercaseChar() }
    .joinToString("")
    .ifEmpty { "🎂" }

  val baseColor = try {
    Color(android.graphics.Color.parseColor(contact.avatarColorHex))
  } catch (_: Exception) {
    PrimaryPink
  }

  val borderModifier = if (showCelebrationBorder) {
    Modifier.border(
      width = 2.5.dp,
      brush = Brush.sweepGradient(listOf(PrimaryPink, AccentGold, PrimaryPink)),
      shape = CircleShape
    )
  } else {
    Modifier.border(
      width = 1.dp,
      color = Color.White.copy(alpha = 0.8f),
      shape = CircleShape
    )
  }

  Box(
    modifier = modifier
      .size(size)
      .then(borderModifier)
      .clip(CircleShape),
    contentAlignment = Alignment.Center
  ) {
    if (!contact.photoUri.isNullOrBlank()) {
      AsyncImage(
        model = contact.photoUri,
        contentDescription = "${contact.name} avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(size)
          .clip(CircleShape)
      )
    } else {
      Box(
        modifier = Modifier
          .size(size)
          .background(
            brush = Brush.verticalGradient(
              listOf(baseColor, baseColor.copy(alpha = 0.85f))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        val fontSize = (size.value * 0.38f).sp
        Text(
          text = initials,
          color = Color.White,
          fontSize = fontSize,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )
      }
    }
  }
}
