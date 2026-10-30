package com.example.ui.main.jetpack

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import coil3.compose.AsyncImage
import com.example.ui.main.activity.Login
import com.example.ui.main.activity.TimerActivity
import com.example.ui.main.database.UserDatabase
import com.example.ui.main.model.UserLeave
import com.example.ui.main.sharedpreference.PreferManager
import com.example.ui.main.viewmodel.ProfileDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun UserProfile(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<NavKey>(Profile) }
    val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)

    // Handle back gesture when user is inside sub-screens (e.g., Settings, ProfileDetails)
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                }
            }
        ) { key ->
            NavEntry(key) {
                when (key) {
                    is Profile -> UserProfileContent(
                        modifier = modifier,
                        onProfileClick = { backStack.add(UserDetails) },
                        onSettingsClick = { backStack.add(Settings) },
                        onHelpClick = { backStack.add(HelpUs) },
                        onAppearanceClick = { backStack.add(Appearance) }
                    )
                    is UserDetails -> ProfileDetails(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                    is Settings -> SettingUser(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                    is HelpUs -> HelpSupportScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                    is Appearance -> AppearanceScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                    else -> UserProfileContent(
                        modifier = modifier,
                        onProfileClick = { backStack.add(UserDetails) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAppearanceClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: ProfileDetailsViewModel = viewModel()
    val userDetails by viewModel.userDetails.observeAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCalendarSheet by remember { mutableStateOf(false) }
    var showLeaveSheet by remember { mutableStateOf(false) }

    val userDao = remember { UserDatabase.getDatabase(context).userDao() }
    val dbTasks by userDao.getAllTasksFlow().collectAsState(initial = emptyList())
    val dbLeaves by userDao.getAllLeavesFlow().collectAsState(initial = emptyList())

    val totalTasksCount = dbTasks.size
    val completedTasksCount = dbTasks.count { it.isCompleted }
    val pendingTasksCount = dbTasks.count { !it.isCompleted }

    // State for Apply Leave form
    var leaveType by remember { mutableStateOf("Casual Leave") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var leaveReason by remember { mutableStateOf("") }
    var leaveError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserDetail(context, 1)
    }

    if (showLogoutDialog) {
        Logout(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                PreferManager.clearLogin(context)
                val intent = Intent(context, Login::class.java)
                context.startActivity(intent)
                (context as TimerActivity).finish()
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "User Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = modifier.fillMaxWidth().padding(16.dp, 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val user = viewModel.userDetails.value?.firstName
                    AsyncImage(
                        model = userDetails?.image,
                        contentDescription = "User profile image",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "$user",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier.padding(5.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 0.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = "Tasks",
                            modifier = modifier.padding(5.dp),
                            tint = Color.Green
                        )
                        Text(text = "$totalTasksCount", color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Tasks", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.height(60.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Completed",
                            modifier = modifier.padding(5.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "$completedTasksCount", color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Completed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.height(60.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = "Pending",
                            modifier = modifier.padding(5.dp),
                            tint = Color.Blue
                        )
                        Text(text = "$pendingTasksCount", color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Pending", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileMenuItem(
                    title = "Profile",
                    subtitle = "Personal Information",
                    icon = Icons.Default.Person,
                    onClick = onProfileClick
                )
                ProfileMenuItem(
                    title = "Calendar & Holidays",
                    subtitle = "View company & public holidays",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { showCalendarSheet = true }
                )
                ProfileMenuItem(
                    title = "Apply for Leave",
                    subtitle = "Request time off & view leave history",
                    icon = Icons.AutoMirrored.Filled.EventNote,
                    onClick = { showLeaveSheet = true }
                )
                ProfileMenuItem(
                    title = "Settings",
                    subtitle = "App Notifications",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick
                )
                ProfileMenuItem(
                    title = "Help",
                    subtitle = "Contact Us",
                    icon = Icons.AutoMirrored.Filled.Help,
                    onClick = onHelpClick
                )
                ProfileMenuItem(
                    title = "Appearance",
                    subtitle = "Change theme and appearance",
                    icon = Icons.Default.FormatPaint,
                    onClick = onAppearanceClick
                )
                ProfileMenuItem(
                    title = "Logout",
                    subtitle = "Sign out fom your Account",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Modal Sheet for Calendar & Holidays
    if (showCalendarSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCalendarSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            val holidays = listOf(
                Pair("New Year's Day", "01 Jan 2025"),
                Pair("Republic Day", "26 Jan 2025"),
                Pair("Good Friday", "18 Apr 2025"),
                Pair("Labor Day", "01 May 2025"),
                Pair("Independence Day", "15 Aug 2025"),
                Pair("Thanksgiving", "27 Nov 2025"),
                Pair("Christmas Day", "25 Dec 2025")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Calendar & Holidays 📅",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Upcoming Official Holidays (2025)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(holidays) { (name, date) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Official Holiday",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Apply Leave
    if (showLeaveSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLeaveSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            val leaveTypes = listOf("Casual Leave", "Sick Leave", "Annual Leave", "Unpaid Leave")

            fun pickDate(onDateSelected: (String) -> Unit) {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val formatted = String.format(Locale.getDefault(), "%02d/%02d/%d", d, m + 1, y)
                        onDateSelected(formatted)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Apply for Leave 📝",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Select Leave Type",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(leaveTypes) { type ->
                        val isSel = (leaveType == type)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            modifier = Modifier.clickable { leaveType = type }
                        ) {
                            Text(
                                text = type,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { pickDate { startDate = it } },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("From Date *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = startDate.ifEmpty { "Select Date 📅" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (startDate.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { pickDate { endDate = it } },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("To Date *", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = endDate.ifEmpty { "Select Date 📅" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (endDate.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = leaveReason,
                    onValueChange = { leaveReason = it },
                    label = { Text("Reason for Leave", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                if (leaveError) {
                    Text(
                        text = "Please select both From and To dates!",
                        color = Color(0xFFF87171),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        if (startDate.isBlank() || endDate.isBlank()) {
                            leaveError = true
                        } else {
                            val createdAt = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            scope.launch {
                                userDao.insertLeave(
                                    UserLeave(
                                        leaveType = leaveType,
                                        startDate = startDate,
                                        endDate = endDate,
                                        reason = leaveReason.trim().ifEmpty { "General Leave" },
                                        status = "Pending",
                                        createdAt = createdAt
                                    )
                                )
                                Toast.makeText(context, "Leave request submitted!", Toast.LENGTH_SHORT).show()
                            }
                            startDate = ""
                            endDate = ""
                            leaveReason = ""
                            leaveError = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Submit Leave Request", color = Color.White)
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                Text(
                    text = "Applied Leaves",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (dbLeaves.isEmpty()) {
                    Text(
                        text = "No leave requests found.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dbLeaves, key = { it.id }) { leave ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${leave.leaveType} (${leave.status})",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${leave.startDate} - ${leave.endDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = leave.reason,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                userDao.deleteLeave(leave.id)
                                                Toast.makeText(context, "Leave request deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Leave",
                                            tint = Color(0xFFF87171)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Open $title",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun Logout(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        title = { Text("Logout") },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Logout")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun UserProfilePreview() {
    UserProfile()
}
