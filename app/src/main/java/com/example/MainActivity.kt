package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoogleDriveSyncDialog
import com.example.ui.components.HydrationDialog
import com.example.ui.components.MiniMusicBar
import com.example.ui.components.OfflineSyncBanner
import com.example.ui.components.SocialShareSheet
import com.example.ui.components.VoiceCommandDialog
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DietScreen
import com.example.ui.screens.MusicScreen
import com.example.ui.screens.RoutinesScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GymTab
import com.example.ui.viewmodel.GymViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GymViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()

            val routines by viewModel.routines.collectAsState()
            val sessions by viewModel.sessions.collectAsState()
            val meals by viewModel.meals.collectAsState()
            val socialPosts by viewModel.socialPosts.collectAsState()
            val bodyCompositionLogs by viewModel.bodyCompositionLogs.collectAsState()
            val latestBodyLog by viewModel.latestBodyLog.collectAsState()
            val userWeightKg by viewModel.userWeightKg.collectAsState()
            val userHeightCm by viewModel.userHeightCm.collectAsState()

            val isVegDiet by viewModel.isVegDiet.collectAsState()
            val dietGoal by viewModel.dietGoal.collectAsState()
            val waterIntakeMl by viewModel.waterIntakeMl.collectAsState()
            val targetWaterMl by viewModel.targetWaterMl.collectAsState()

            val isSyncing by viewModel.isSyncing.collectAsState()
            val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()
            val syncStatusMessage by viewModel.syncStatusMessage.collectAsState()

            val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()
            val lastVoiceAction by viewModel.lastVoiceAction.collectAsState()
            val showHydrationDialog by viewModel.showHydrationDialog.collectAsState()
            val showDriveDialog by viewModel.showDriveDialog.collectAsState()
            val driveUser by viewModel.driveUser.collectAsState()
            val lastDriveBackupTime by viewModel.lastDriveBackupTime.collectAsState()
            val isWebSecurityVerified by viewModel.isWebSecurityVerified.collectAsState()
            val shareSummary by viewModel.shareWorkoutSummary.collectAsState()

            val isWorkoutActive = viewModel.activeRoutine.collectAsState().value != null

            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(GymElectricLime),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "IRONPULSE",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 1.2.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            navigationIcon = {
                                // Hydration quick action button
                                IconButton(
                                    onClick = { viewModel.openHydrationDialog() },
                                    modifier = Modifier.testTag("appbar_hydration_button")
                                ) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = GymNeonCyan,
                                                contentColor = Color.Black
                                            ) {
                                                Text(
                                                    text = "${waterIntakeMl / 1000}L",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = "Hydration Tracker",
                                            tint = GymNeonCyan
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Google Drive Cloud Storage Quick Action
                                IconButton(
                                    onClick = { viewModel.openDriveDialog() },
                                    modifier = Modifier.testTag("appbar_drive_storage_button")
                                ) {
                                    Icon(
                                        imageVector = if (driveUser != null) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                        contentDescription = "Google Drive Cloud Storage",
                                        tint = if (driveUser != null) GymElectricLime else GymNeonCyan
                                    )
                                }

                                // Dark mode toggle for night gym sessions
                                IconButton(
                                    onClick = { viewModel.toggleDarkMode() },
                                    modifier = Modifier.testTag("appbar_dark_mode_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = if (isDarkMode) "Switch to Day Mode" else "Switch to Night Gym Mode",
                                        tint = if (isDarkMode) GymElectricAmber else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Persistent Mini Music Bar (shows on all tabs except Music tab)
                            if (currentTab != GymTab.MUSIC) {
                                MiniMusicBar(
                                    musicPlayer = viewModel.musicPlayer,
                                    onOpenFullMusic = { viewModel.selectTab(GymTab.MUSIC) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // 6-Tab M3 Navigation Bar
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("main_bottom_nav_bar")
                            ) {
                                // 1. Routines
                                NavigationBarItem(
                                    selected = currentTab == GymTab.ROUTINES,
                                    onClick = { viewModel.selectTab(GymTab.ROUTINES) },
                                    icon = {
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = "Routines",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Workouts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_routines")
                                )

                                // 2. Active Workout / Training
                                NavigationBarItem(
                                    selected = currentTab == GymTab.ACTIVE_WORKOUT,
                                    onClick = { viewModel.selectTab(GymTab.ACTIVE_WORKOUT) },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (isWorkoutActive) {
                                                    Badge(containerColor = GymElectricLime)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Timer,
                                                contentDescription = "Training",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    },
                                    label = { Text("Training", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_training")
                                )

                                // 3. Diet
                                NavigationBarItem(
                                    selected = currentTab == GymTab.DIET,
                                    onClick = { viewModel.selectTab(GymTab.DIET) },
                                    icon = {
                                        Icon(
                                            Icons.Default.Restaurant,
                                            contentDescription = "Diet",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Diet", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_diet")
                                )

                                // 4. Dashboard
                                NavigationBarItem(
                                    selected = currentTab == GymTab.DASHBOARD,
                                    onClick = { viewModel.selectTab(GymTab.DASHBOARD) },
                                    icon = {
                                        Icon(
                                            Icons.Default.TrendingUp,
                                            contentDescription = "Dashboard",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_dashboard")
                                )

                                // 5. Music
                                NavigationBarItem(
                                    selected = currentTab == GymTab.MUSIC,
                                    onClick = { viewModel.selectTab(GymTab.MUSIC) },
                                    icon = {
                                        Icon(
                                            Icons.Default.MusicNote,
                                            contentDescription = "Music",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Music", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_music")
                                )

                                // 6. Social
                                NavigationBarItem(
                                    selected = currentTab == GymTab.SOCIAL,
                                    onClick = { viewModel.selectTab(GymTab.SOCIAL) },
                                    icon = {
                                        Icon(
                                            Icons.Default.People,
                                            contentDescription = "Community",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Social", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = navItemColors(),
                                    modifier = Modifier.testTag("nav_item_social")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Offline Synchronization Status Banner
                        OfflineSyncBanner(
                            isSyncing = isSyncing,
                            pendingSyncCount = pendingSyncCount,
                            syncMessage = syncStatusMessage,
                            onTriggerSync = { viewModel.syncOfflineData() }
                        )

                        // Main Dynamic Tab Content
                        Box(modifier = Modifier.fillMaxSize()) {
                            Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
                                when (tab) {
                                    GymTab.ROUTINES -> {
                                        RoutinesScreen(
                                            routines = routines,
                                            onStartWorkout = { routine -> viewModel.startWorkout(routine) },
                                            onCreateCustomRoutine = { name, split, desc, lvl, musc, exs ->
                                                viewModel.createCustomRoutine(name, split, desc, lvl, musc, exs)
                                            }
                                        )
                                    }
                                    GymTab.ACTIVE_WORKOUT -> {
                                        ActiveWorkoutScreen(
                                            viewModel = viewModel,
                                            onOpenMusicTab = { viewModel.selectTab(GymTab.MUSIC) }
                                        )
                                    }
                                    GymTab.DIET -> {
                                        DietScreen(
                                            meals = meals,
                                            isVegPreference = isVegDiet,
                                            dietGoal = dietGoal,
                                            userWeightKg = userWeightKg,
                                            userHeightCm = userHeightCm,
                                            onUpdateWeightAndHeight = { w, h ->
                                                viewModel.updateWeightAndHeight(w, h)
                                            },
                                            onToggleVegPreference = { isVeg -> viewModel.setDietVegPreference(isVeg) },
                                            onSelectGoal = { goal -> viewModel.setDietGoal(goal) },
                                            onToggleMeal = { meal -> viewModel.toggleMealLogged(meal) },
                                            onDeleteMeal = { id -> viewModel.deleteMeal(id) },
                                            onAddCustomMeal = { name, isVeg, cal, pro, carb, fat, fib, type ->
                                                viewModel.addCustomMeal(name, isVeg, cal, pro, carb, fat, fib, type)
                                            }
                                        )
                                    }
                                    GymTab.DASHBOARD -> {
                                        DashboardScreen(
                                            sessions = sessions,
                                            bodyLogs = bodyCompositionLogs,
                                            latestBody = latestBodyLog,
                                            onLogMeasurement = { wt, fat, musc, ch, arm, wst, notes ->
                                                viewModel.recordBodyComposition(wt, fat, musc, ch, arm, wst, notes)
                                            },
                                            onOpenDriveStorage = { viewModel.openDriveDialog() },
                                            isDriveConnected = driveUser != null
                                        )
                                    }
                                    GymTab.MUSIC -> {
                                        MusicScreen(
                                            musicPlayer = viewModel.musicPlayer
                                        )
                                    }
                                    GymTab.SOCIAL -> {
                                        SocialScreen(
                                            posts = socialPosts,
                                            onLikePost = { post -> viewModel.likeSocialPost(post) },
                                            onAddPost = { author, authorId, caption, vol, sets, pr ->
                                                viewModel.createSocialPost(author, authorId, caption, vol, sets, pr)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Hands-Free Voice Command Dialog
                    if (showVoiceDialog) {
                        VoiceCommandDialog(
                            onDismiss = { viewModel.closeVoiceDialog() },
                            onExecuteCommand = { cmd -> viewModel.handleVoiceCommand(cmd) },
                            lastAction = lastVoiceAction
                        )
                    }

                    // Hydration Interval Tracker Dialog
                    if (showHydrationDialog) {
                        HydrationDialog(
                            currentMl = waterIntakeMl,
                            targetMl = targetWaterMl,
                            onAddMl = { ml -> viewModel.logWater(ml) },
                            onReset = { viewModel.resetWater() },
                            onTestAlert = { viewModel.triggerHydrationAlert() },
                            onDismiss = { viewModel.closeHydrationDialog() }
                        )
                    }

                    // Google Drive Storage & Web Security Dialog
                    if (showDriveDialog) {
                        GoogleDriveSyncDialog(
                            currentUser = driveUser,
                            lastBackupTimestamp = lastDriveBackupTime,
                            isSyncing = isSyncing,
                            syncMessage = syncStatusMessage,
                            isWebSecurityVerified = isWebSecurityVerified,
                            onSignIn = { email, name -> viewModel.signInGoogleDrive(email, name) },
                            onSignOut = { viewModel.signOutGoogleDrive() },
                            onBackupNow = { viewModel.backupToGoogleDrive() },
                            onVerifySecurity = { viewModel.verifyDriveWebSecurity() },
                            onDismiss = { viewModel.closeDriveDialog() }
                        )
                    }

                    // Social Share Sheet when workout is finished
                    shareSummary?.let { summary ->
                        SocialShareSheet(
                            summary = summary,
                            onDismiss = { viewModel.dismissShareSummary() },
                            onPublishToCommunity = { caption ->
                                viewModel.publishWorkoutToCommunity(caption)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun navItemColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.Black,
        selectedTextColor = GymElectricLime,
        indicatorColor = GymElectricLime,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
