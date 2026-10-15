package com.example.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AddEditBirthdayScreen
import com.example.ui.screens.AllContactsScreen
import com.example.ui.screens.CalendarViewScreen
import com.example.ui.screens.ContactDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryPinkContainer
import com.example.ui.theme.SoftPinkBackground

sealed class Screen(val route: String, val title: String) {
  object Home : Screen("home", "Home")
  object Calendar : Screen("calendar", "Calendar")
  object Contacts : Screen("contacts", "Contacts")
  object Settings : Screen("settings", "Settings")
  object Detail : Screen("detail/{contactId}", "Detail") {
    fun createRoute(contactId: Long) = "detail/$contactId"
  }
  object AddEdit : Screen("add_edit?birthdayId={birthdayId}", "Add / Edit") {
    fun createRoute(birthdayId: Long? = null) = if (birthdayId != null) "add_edit?birthdayId=$birthdayId" else "add_edit"
  }
}

data class BottomNavItem(
  val screen: Screen,
  val label: String,
  val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CelebrateApp(
  viewModel: BirthdayViewModel = viewModel()
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val bottomNavItems = listOf(
    BottomNavItem(
      screen = Screen.Home,
      label = "Home",
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
      screen = Screen.Calendar,
      label = "Calendar",
      selectedIcon = Icons.Filled.CalendarMonth,
      unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
      screen = Screen.Contacts,
      label = "Contacts",
      selectedIcon = Icons.Filled.People,
      unselectedIcon = Icons.Outlined.People
    ),
    BottomNavItem(
      screen = Screen.Settings,
      label = "Settings",
      selectedIcon = Icons.Filled.Settings,
      unselectedIcon = Icons.Outlined.Settings
    )
  )

  val showBottomBar = currentRoute in listOf(
    Screen.Home.route,
    Screen.Calendar.route,
    Screen.Contacts.route,
    Screen.Settings.route
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = SoftPinkBackground,
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          containerColor = Color.White,
          tonalElevation = 6.dp
        ) {
          bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
              icon = {
                Icon(
                  if (isSelected) item.selectedIcon else item.unselectedIcon,
                  contentDescription = item.label
                )
              },
              label = {
                Text(
                  text = item.label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              selected = isSelected,
              onClick = {
                if (currentRoute != item.screen.route) {
                  navController.navigate(item.screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPink,
                selectedTextColor = PrimaryPink,
                indicatorColor = PrimaryPinkContainer,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
              ),
              modifier = Modifier.testTag("nav_tab_${item.label.lowercase()}")
            )
          }
        }
      }
    }
  ) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      enterTransition = { fadeIn() },
      exitTransition = { fadeOut() }
    ) {
      // 1. Home Screen
      composable(Screen.Home.route) {
        HomeScreen(
          viewModel = viewModel,
          onNavigateToDetail = { contactId ->
            navController.navigate(Screen.Detail.createRoute(contactId))
          },
          onNavigateToAdd = {
            navController.navigate(Screen.AddEdit.createRoute(null))
          }
        )
      }

      // 2. Calendar Screen
      composable(Screen.Calendar.route) {
        CalendarViewScreen(
          viewModel = viewModel,
          onNavigateToDetail = { contactId ->
            navController.navigate(Screen.Detail.createRoute(contactId))
          },
          onNavigateToAdd = {
            navController.navigate(Screen.AddEdit.createRoute(null))
          }
        )
      }

      // 3. Contacts Screen
      composable(Screen.Contacts.route) {
        AllContactsScreen(
          viewModel = viewModel,
          onNavigateToDetail = { contactId ->
            navController.navigate(Screen.Detail.createRoute(contactId))
          },
          onNavigateToAdd = {
            navController.navigate(Screen.AddEdit.createRoute(null))
          }
        )
      }

      // 4. Settings Screen
      composable(Screen.Settings.route) {
        SettingsScreen(viewModel = viewModel)
      }

      // 5. Contact Detail Screen
      composable(
        route = Screen.Detail.route,
        arguments = listOf(navArgument("contactId") { type = NavType.LongType })
      ) { backStackEntry ->
        val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0L
        ContactDetailScreen(
          viewModel = viewModel,
          contactId = contactId,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToEdit = { id ->
            navController.navigate(Screen.AddEdit.createRoute(id))
          }
        )
      }

      // 6. Add / Edit Birthday Screen
      composable(
        route = Screen.AddEdit.route,
        arguments = listOf(
          navArgument("birthdayId") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
          }
        )
      ) { backStackEntry ->
        val bdayIdStr = backStackEntry.arguments?.getString("birthdayId")
        val bdayId = bdayIdStr?.toLongOrNull()
        AddEditBirthdayScreen(
          viewModel = viewModel,
          birthdayId = bdayId,
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }
}
