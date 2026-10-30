package com.example.ui.main.jetpack

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.ui.main.database.UserDatabase
import com.example.ui.main.model.UserActivityLog
import com.example.ui.main.model.UserTimer
import com.example.ui.main.viewmodel.LoginViewModel
import com.example.ui.main.viewmodel.ProfileDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
}

fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

@Composable
fun CheckInUi(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()
    val selectedItem = viewModel.selectedItem

    // Intercept back gesture when on Tab 1 or Tab 2, heading back to Tab 0 (Home/TimerUi)
    BackHandler(enabled = selectedItem.intValue != 0) {
        selectedItem.intValue = 0
    }

    Theme {
        val systemDark = isSystemInDarkTheme()
        val isDarkTheme = when (AppThemeState.themeMode.value) {
            "Light" -> false
            "Dark" -> true
            else -> systemDark
        }

        Scaffold(
            modifier = modifier,
            bottomBar = {
                BottomNavBar(
                    selectedItem = selectedItem.intValue,
                    onItemSelected = { selectedItem.intValue = it },
                    isDarkTheme = isDarkTheme
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedItem.intValue) {
                    0 -> CheckInContent(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            val newMode = if (isDarkTheme) "Light" else "Dark"
                            AppThemeState.updateThemeMode(context, newMode)
                        }
                    )
                    1 -> UserTasks()
                    2 -> UserProfile()
                }
            }
        }
    }
}

@Composable
fun CheckInContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    onThemeToggle: () -> Unit = {}
) {
    val backstack = remember { mutableStateListOf<NavKey>(CheckIn) }
    val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
        NavDisplay(
            backStack = backstack,
            onBack = {
                if (backstack.size > 1) {
                    backstack.removeAt(backstack.lastIndex)
                }
            }
        ) { key ->
            NavEntry(key) {
                when (key) {
                    is CheckIn -> CheckInHomeContent(
                        modifier = modifier,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        onAttendanceClick = { backstack.add(Attendance) },
                        onMyTasksClick = { backstack.add(MyTasks) }
                    )
                    is Attendance -> AttendanceUi(
                        onBackClick = {
                            if (backstack.size > 1) {
                                backstack.removeAt(backstack.lastIndex)
                            }
                        }
                    )
                    is MyTasks -> MyTasksUi(
                        onBackClick = {
                            if (backstack.size > 1) {
                                backstack.removeAt(backstack.lastIndex)
                            }
                        }
                    )
                    else -> CheckInHomeContent(
                        modifier = modifier,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        onAttendanceClick = { backstack.add(Attendance) },
                        onMyTasksClick = { backstack.add(MyTasks) }
                    )
                }
            }
        }
    }
}

@Composable
fun CheckInHomeContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    onThemeToggle: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onMyTasksClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDao = remember { UserDatabase.getDatabase(context).userDao() }

    val profileViewModel: ProfileDetailsViewModel = viewModel()
    val userDetails by profileViewModel.userDetails.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getUserDetail(context, 1)
    }

    val dbUsername = userDetails?.username ?: userDetails?.firstName ?: "Developer"

    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant
    val circleBgColor = if (isDarkTheme) Color(0xFF11192C) else Color(0xFFF1F5F9)
    val dividerColor = MaterialTheme.colorScheme.outline
    val primaryAccent = MaterialTheme.colorScheme.primary

    var showLocationOffDialog by remember { mutableStateOf(false) }
    var showCheckOutConfirmation by remember { mutableStateOf(false) }

    val homeScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        homeScrollState.scrollTo(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(homeScrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header with Theme Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )

            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = textColor
                )
            }
        }

        // Greeting Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = cardColor
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good Morning 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor
                    )
                    Text(
                        text = "Hello, $dbUsername",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = primaryAccent.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = primaryAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Working Duration Timer Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = primaryAccent
        ) {
            val savedTimerState by userDao.getUserTimerFlow().collectAsState(initial = null)

            val isCheckedIn = savedTimerState?.isCheckedIn == true
            val isOnBreak = savedTimerState?.isOnBreak == true
            val accumulatedMs = savedTimerState?.accumulatedMs ?: 0L
            val lastWorkingStartMs = savedTimerState?.lastWorkingStartMs ?: 0L
            val lastCheckInTime = savedTimerState?.lastCheckInTime
            val lastCheckOutTime = savedTimerState?.lastCheckOutTime

            fun calculateCurrentWorkingMs(): Long {
                if (!isCheckedIn) return 0L
                if (isOnBreak) return accumulatedMs
                val now = System.currentTimeMillis()
                val activeSegment = if (lastWorkingStartMs > 0L) maxOf(0L, now - lastWorkingStartMs) else 0L
                return accumulatedMs + activeSegment
            }

            fun formatTime(totalMs: Long): String {
                val seconds = (totalMs / 1000) % 60
                val minutes = (totalMs / (1000 * 60)) % 60
                val hours = totalMs / (1000 * 60 * 60)
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            }

            var timerText by remember { mutableStateOf(formatTime(calculateCurrentWorkingMs())) }

            fun performCheckIn() {
                val now = System.currentTimeMillis()
                val currentTimeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val randomLat = 37.7749 + Random.nextDouble(-0.01, 0.01)
                val randomLng = -122.4194 + Random.nextDouble(-0.01, 0.01)

                val timerData = UserTimer(
                    isCheckedIn = true,
                    isOnBreak = false,
                    accumulatedMs = 0L,
                    lastWorkingStartMs = now,
                    lastCheckInTime = "Today at $currentTimeStr",
                    lastCheckOutTime = lastCheckOutTime,
                    lastActivityMsg = "Checked In at $currentTimeStr",
                    id = 1
                )
                scope.launch {
                    userDao.insertUserTimer(timerData)
                    userDao.insertActivityLog(
                        UserActivityLog(
                            title = "Checked In",
                            timestamp = "Today at $currentTimeStr",
                            type = "CHECK_IN"
                        )
                    )
                }

                val locMsg = String.format(Locale.getDefault(), "Lat: %.4f, Lng: %.4f", randomLat, randomLng)
                Toast.makeText(context, "Check In Successful!\nLocation: $locMsg", Toast.LENGTH_LONG).show()
            }

            fun performCheckOut() {
                val currentTimeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

                val timerData = UserTimer(
                    isCheckedIn = false,
                    isOnBreak = false,
                    accumulatedMs = 0L,
                    lastWorkingStartMs = 0L,
                    lastCheckInTime = lastCheckInTime,
                    lastCheckOutTime = "Today at $currentTimeStr",
                    lastActivityMsg = "Checked Out at $currentTimeStr",
                    id = 1
                )
                scope.launch {
                    userDao.insertUserTimer(timerData)
                    userDao.insertActivityLog(
                        UserActivityLog(
                            title = "Checked Out",
                            timestamp = "Today at $currentTimeStr",
                            type = "CHECK_OUT"
                        )
                    )
                }
                Toast.makeText(context, "Check Out Successful!", Toast.LENGTH_SHORT).show()
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                if (isGranted) {
                    if (!isLocationEnabled(context)) {
                        showLocationOffDialog = true
                    } else {
                        performCheckIn()
                    }
                } else {
                    Toast.makeText(context, "Location permission is required to check in.", Toast.LENGTH_SHORT).show()
                }
            }

            if (showLocationOffDialog) {
                AlertDialog(
                    onDismissRequest = { showLocationOffDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOff,
                                contentDescription = "Location Disabled",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Location Services Required",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    text = {
                        Text(
                            text = "Your device location is currently turned off. To check in, please enable location services.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLocationOffDialog = false
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Turn On Location",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLocationOffDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                )
            }

            if (showCheckOutConfirmation) {
                AlertDialog(
                    onDismissRequest = { showCheckOutConfirmation = false },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            text = "Confirm Check Out",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = "Do you really want to check out for today?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showCheckOutConfirmation = false
                                performCheckOut()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF87171)
                            )
                        ) {
                            Text(
                                text = "Check Out",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showCheckOutConfirmation = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                )
            }

            LaunchedEffect(isCheckedIn, isOnBreak, accumulatedMs, lastWorkingStartMs) {
                if (isCheckedIn && !isOnBreak) {
                    while (isActive) {
                        timerText = formatTime(calculateCurrentWorkingMs())
                        delay(1000)
                    }
                } else {
                    timerText = formatTime(calculateCurrentWorkingMs())
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "WORKING DURATION",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = timerText,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isCheckedIn) {
                                if (!hasLocationPermission(context)) {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else if (!isLocationEnabled(context)) {
                                    showLocationOffDialog = true
                                } else {
                                    performCheckIn()
                                }
                            } else {
                                // Prompt confirmation before Check Out
                                showCheckOutConfirmation = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCheckedIn) Color(0xFFF87171) else Color(0xFF4ADE80)
                        )
                    ) {
                        Text(
                            text = if (isCheckedIn) "Check Out" else "Check In",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = {
                            if (!isCheckedIn) {
                                Toast.makeText(context, "Please check in first before taking a break!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val now = System.currentTimeMillis()
                            val currentTimeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            if (!isOnBreak) {
                                // Start Break: pause working timer
                                val currentSegment = if (lastWorkingStartMs > 0L) maxOf(0L, now - lastWorkingStartMs) else 0L
                                val newAccumulated = accumulatedMs + currentSegment
                                val timerData = UserTimer(
                                    isCheckedIn = true,
                                    isOnBreak = true,
                                    accumulatedMs = newAccumulated,
                                    lastWorkingStartMs = 0L,
                                    lastCheckInTime = lastCheckInTime,
                                    lastCheckOutTime = lastCheckOutTime,
                                    lastActivityMsg = "On Break since $currentTimeStr",
                                    id = 1
                                )
                                scope.launch {
                                    userDao.insertUserTimer(timerData)
                                    userDao.insertActivityLog(
                                        UserActivityLog(
                                            title = "Break Started",
                                            timestamp = "Today at $currentTimeStr",
                                            type = "BREAK"
                                        )
                                    )
                                }
                            } else {
                                // Resume from Break: continue working timer
                                val timerData = UserTimer(
                                    isCheckedIn = true,
                                    isOnBreak = false,
                                    accumulatedMs = accumulatedMs,
                                    lastWorkingStartMs = now,
                                    lastCheckInTime = lastCheckInTime,
                                    lastCheckOutTime = lastCheckOutTime,
                                    lastActivityMsg = "Resumed Work at $currentTimeStr",
                                    id = 1
                                )
                                scope.launch {
                                    userDao.insertUserTimer(timerData)
                                    userDao.insertActivityLog(
                                        UserActivityLog(
                                            title = "Resumed Work",
                                            timestamp = "Today at $currentTimeStr",
                                            type = "RESUME"
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOnBreak) Color(0xFFFBBF24) else Color(0xFF1E293B)
                        )
                    ) {
                        Text(
                            text = if (isOnBreak) "Resume" else "Break",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = cardColor,
                onClick = onAttendanceClick
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = circleBgColor,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Attendance",
                                tint = primaryAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Text(
                        text = "Check status",
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor
                    )
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = cardColor,
                onClick = onMyTasksClick
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = circleBgColor,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "My Tasks",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    Text(
                        text = "Check tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor
                    )
                }
            }
        }

        val liveTimerState by userDao.getUserTimerFlow().collectAsState(initial = null)
        val liveTasksState by userDao.getAllTasksFlow().collectAsState(initial = emptyList())
        val liveActivities by userDao.getAllActivitiesFlow().collectAsState(initial = emptyList())

        // Recent Activity Section
        Text(
            text = "Recent Activity Log",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = cardColor
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (liveActivities.isEmpty()) {
                    // Fallback preview item if activity log is empty
                    val isCheckedInLive = liveTimerState?.isCheckedIn == true
                    val isOnBreakLive = liveTimerState?.isOnBreak == true
                    val checkInTitle = when {
                        isCheckedInLive && isOnBreakLive -> "On Break"
                        isCheckedInLive -> "Checked In"
                        liveTimerState?.lastCheckOutTime != null -> "Checked Out"
                        else -> "Check In Status"
                    }
                    val checkInSubtitle = liveTimerState?.lastActivityMsg
                        ?: liveTimerState?.lastCheckInTime
                        ?: "Not checked in yet"

                    val checkInIcon = when {
                        isCheckedInLive && !isOnBreakLive -> Icons.Default.CheckCircle
                        else -> Icons.Default.LockClock
                    }
                    val checkInColor = when {
                        isCheckedInLive && !isOnBreakLive -> Color(0xFF10B981)
                        isOnBreakLive -> Color(0xFFF59E0B)
                        else -> Color(0xFFF87171)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = checkInColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = checkInIcon,
                                    contentDescription = checkInTitle,
                                    tint = checkInColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = checkInTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor
                            )
                            Text(
                                text = checkInSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = subtitleColor
                            )
                        }
                    }
                } else {
                    // List timeline of activities done today (Check in, Break, Resume, Check Out)
                    liveActivities.take(5).forEachIndexed { index, act ->
                        val actIcon = when (act.type) {
                            "CHECK_IN" -> Icons.Default.CheckCircle
                            "BREAK" -> Icons.Default.PauseCircle
                            "RESUME" -> Icons.Default.PlayCircle
                            else -> Icons.Default.LockClock
                        }
                        val actColor = when (act.type) {
                            "CHECK_IN" -> Color(0xFF10B981)
                            "BREAK" -> Color(0xFFF59E0B)
                            "RESUME" -> Color(0xFF3B82F6)
                            else -> Color(0xFFF87171)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = actColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = actIcon,
                                        contentDescription = act.title,
                                        tint = actColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = act.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor
                                )
                                Text(
                                    text = act.timestamp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = subtitleColor
                                )
                            }
                        }

                        if (index < minOf(liveActivities.size, 5) - 1) {
                            HorizontalDivider(color = dividerColor, thickness = 1.dp)
                        }
                    }
                }

                // Show latest task creation activity if present
                val latestTask = liveTasksState.firstOrNull()
                if (latestTask != null) {
                    HorizontalDivider(color = dividerColor, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = "Task Created",
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Task: ${latestTask.title}",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor
                            )
                            Text(
                                text = "${latestTask.createdAt} • ${latestTask.category} (${if (latestTask.isCompleted) "Completed" else if (latestTask.isOnHold) "On Hold" else "Pending"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = subtitleColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    isDarkTheme: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val items = listOf("Home", "Tasks", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.CheckCircle,
        Icons.Default.Person
    )

    val navBgColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val selectedContentColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
    val unselectedContentColor = if (isDarkTheme) Color.Gray else Color(0xFF64748B)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(22.dp),
        color = navBgColor
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onItemSelected(index)
                    },
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item
                        )
                    },
                    label = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedContentColor,
                        selectedTextColor = selectedContentColor,
                        indicatorColor = if (isDarkTheme) MaterialTheme.colorScheme.primary else Color(0xFFE0E7FF),
                        unselectedIconColor = unselectedContentColor,
                        unselectedTextColor = unselectedContentColor
                    )
                )
            }
        }
    }
}

@Composable
fun AnimatedScreen(
    onSplashFinished: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onSplashFinished()
    }

    Column(
        modifier = Modifier
            .size(width = 400.dp, height = 800.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(1000)
            ) + scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(1000)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "App logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(90.dp)
                )

                Text(
                    text = "WorkForce",
                    color = Color.White,
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Stay organized. Stay productive.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckInPreview() {
    CheckInUi()
}
