package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldContainer
import com.example.ui.theme.OnAccentGoldContainer
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPinkContainer

@Composable
fun RelationshipTag(
  relationship: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (relationship.lowercase()) {
    "family" -> Color(0xFFFFE4E6) to Color(0xFFBE123C)
    "friend" -> Color(0xFFFEF3C7) to Color(0xFFB45309)
    "partner" -> Color(0xFFFCE7F3) to Color(0xFF9D174D)
    "colleague", "work" -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
    else -> Color(0xFFF3E8FF) to Color(0xFF6B21A8)
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = relationship,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun DaysUntilBadge(
  daysUntil: Int,
  modifier: Modifier = Modifier
) {
  when {
    daysUntil == 0 -> {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
          .clip(RoundedCornerShape(16.dp))
          .background(AccentGold)
          .border(1.dp, Color(0xFFD97706), RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "TODAY! 🎂",
          color = Color(0xFF78350F),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
    daysUntil == 1 -> {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
          .clip(RoundedCornerShape(16.dp))
          .background(PrimaryPinkContainer)
          .border(1.dp, PrimaryPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Tomorrow 🎈",
          color = PrimaryPink,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
    daysUntil <= 7 -> {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
          .clip(RoundedCornerShape(16.dp))
          .background(AccentGoldContainer)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "In $daysUntil days 🎁",
          color = OnAccentGoldContainer,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
    else -> {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFF1F5F9))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "In $daysUntil days",
          color = Color(0xFF475569),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
