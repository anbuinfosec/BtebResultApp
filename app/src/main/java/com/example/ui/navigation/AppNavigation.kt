package com.example.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.ui.components.DonationDialog
import com.example.ui.components.UpdateDialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AboutDeveloperScreen
import com.example.ui.screens.BooklistScreen
import com.example.ui.screens.CgpaCalculatorScreen
import com.example.ui.screens.ContactSupportScreen
import com.example.ui.screens.GroupSearchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IndividualResultScreen
import com.example.ui.screens.InstituteDirectoryScreen
import com.example.ui.screens.InstituteResultsScreen
import com.example.ui.screens.RoutineExplorerScreen
import com.example.ui.screens.SavedRollsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.util.UpdateManager
import com.example.viewmodel.BtebViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModel: BtebViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSplash = currentRoute == Screen.Splash.route
    val isHome = currentRoute == Screen.Home.route
    var showDonationDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    val updateInfo by viewModel.updateInfoState.collectAsStateWithLifecycle()
    var showNavUpdateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ── Back-press logic ─────────────────────────────────────────────────────
    // Priority 1: drawer open → close it
    // Priority 2: on Home → show exit confirmation dialog
    // Priority 3: all other screens → normal back navigation (handled by NavHost)
    BackHandler(enabled = drawerState.isOpen || isHome) {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            isHome             -> showExitDialog = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isSplash,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bteb_logo),
                            contentDescription = "BTEB Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = "BTEB Portal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "v${UpdateManager.CURRENT_VERSION} • Polytechnic",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerItem(
                        label = "Home",
                        icon = Icons.Default.Home,
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )

                    DrawerItem(
                        label = "Group / Batch Search",
                        icon = Icons.Default.Group,
                        selected = currentRoute == Screen.GroupSearch.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.GroupSearch.route)
                        }
                    )

                    DrawerItem(
                        label = "Offline Booklists",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        selected = currentRoute == Screen.Booklists.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Booklists.route)
                        }
                    )

                    DrawerItem(
                        label = "Exam Routine Explorer",
                        icon = Icons.Default.CalendarMonth,
                        selected = currentRoute == Screen.RoutineExplorer.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.RoutineExplorer.route)
                        }
                    )

                    DrawerItem(
                        label = "Polytechnic Directory",
                        icon = Icons.Default.School,
                        selected = currentRoute == Screen.InstituteDirectory.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.InstituteDirectory.route)
                        }
                    )

                    DrawerItem(
                        label = "CGPA Calculator",
                        icon = Icons.Default.Calculate,
                        selected = currentRoute == Screen.CgpaCalculator.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.CgpaCalculator.route)
                        }
                    )

                    DrawerItem(
                        label = "Board Statistics",
                        icon = Icons.Default.BarChart,
                        selected = currentRoute == Screen.Statistics.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Statistics.route)
                        }
                    )

                    DrawerItem(
                        label = "Saved Bookmarks",
                        icon = Icons.Default.Bookmarks,
                        selected = currentRoute == Screen.SavedRolls.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.SavedRolls.route)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerItem(
                        label = "About Developer",
                        icon = Icons.Default.Info,
                        selected = currentRoute == Screen.AboutDeveloper.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.AboutDeveloper.route)
                        }
                    )

                    DrawerItem(
                        label = "Contact & Support",
                        icon = Icons.AutoMirrored.Filled.ContactSupport,
                        selected = currentRoute == Screen.ContactSupport.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.ContactSupport.route)
                        }
                    )

                    DrawerItem(
                        label = if (updateInfo?.isUpdateAvailable == true) "Update Available (v${updateInfo?.latestVersion})" else "Check for Updates",
                        icon = Icons.Default.SystemUpdate,
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.checkForUpdates()
                            if (updateInfo?.isUpdateAvailable == true) {
                                showNavUpdateDialog = true
                            } else {
                                Toast.makeText(
                                    context,
                                    "Checking GitHub... You have the latest version (v${UpdateManager.CURRENT_VERSION})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                    DrawerItem(
                        label = "Support Me ☕",
                        icon = Icons.Default.Favorite,
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            showDonationDialog = true
                        }
                    )
                }
            }
        }
    ) {
        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(
                        text = "Exit App?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to exit BTEB Portal?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    androidx.compose.material3.Button(
                        onClick = { (context as? Activity)?.finish() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5F5F))
                    ) {
                        Text("Exit", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Stay")
                    }
                },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            )
        }

        if (showNavUpdateDialog && updateInfo != null) {
            UpdateDialog(
                updateInfo = updateInfo!!,
                onDismiss = { showNavUpdateDialog = false }
            )
        }

        if (showDonationDialog) {
            DonationDialog(onDismiss = { showDonationDialog = false })
        }
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToResult = { roll, exam ->
                        navController.navigate(Screen.IndividualResult.createRoute(roll, exam))
                    },
                    onNavigateToGroupSearch = { navController.navigate(Screen.GroupSearch.route) },
                    onNavigateToBooklists = { navController.navigate(Screen.Booklists.route) },
                    onNavigateToRoutine = { navController.navigate(Screen.RoutineExplorer.route) },
                    onNavigateToInstitutes = { navController.navigate(Screen.InstituteDirectory.route) },
                    onNavigateToCgpa = { navController.navigate(Screen.CgpaCalculator.route) },
                    onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                    onNavigateToSaved = { navController.navigate(Screen.SavedRolls.route) },
                    onNavigateToAbout = { navController.navigate(Screen.AboutDeveloper.route) },
                    onNavigateToContact = { navController.navigate(Screen.ContactSupport.route) },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(
                route = Screen.IndividualResult.route,
                arguments = listOf(
                    navArgument("roll") { type = NavType.StringType },
                    navArgument("exam") {
                        type = NavType.StringType
                        defaultValue = "DIPLOMA IN ENGINEERING"
                    }
                )
            ) { backStackEntry ->
                val roll = backStackEntry.arguments?.getString("roll") ?: ""
                val exam = backStackEntry.arguments?.getString("exam") ?: "DIPLOMA IN ENGINEERING"
                IndividualResultScreen(
                    roll = roll,
                    exam = exam,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.GroupSearch.route) {
                GroupSearchScreen(
                    viewModel = viewModel,
                    onNavigateToIndividual = { roll, exam ->
                        navController.navigate(Screen.IndividualResult.createRoute(roll, exam))
                    },
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.Booklists.route) {
                BooklistScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.RoutineExplorer.route) {
                RoutineExplorerScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.InstituteDirectory.route) {
                InstituteDirectoryScreen(
                    viewModel = viewModel,
                    onNavigateToInstituteLeaderboard = { code, name ->
                        navController.navigate(Screen.InstituteResults.createRoute(code, name))
                    },
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(
                route = Screen.InstituteResults.route,
                arguments = listOf(
                    navArgument("code") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val code = backStackEntry.arguments?.getString("code") ?: ""
                val name = backStackEntry.arguments?.getString("name") ?: ""
                InstituteResultsScreen(
                    instituteCode = code,
                    instituteName = android.net.Uri.decode(name),
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToIndividual = { roll, exam ->
                        navController.navigate(Screen.IndividualResult.createRoute(roll, exam))
                    }
                )
            }

            composable(Screen.CgpaCalculator.route) {
                CgpaCalculatorScreen(
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.SavedRolls.route) {
                SavedRollsScreen(
                    viewModel = viewModel,
                    onNavigateToResult = { roll, exam ->
                        navController.navigate(Screen.IndividualResult.createRoute(roll, exam))
                    },
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.AboutDeveloper.route) {
                AboutDeveloperScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.ContactSupport.route) {
                ContactSupportScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}
