package com.example.ui.main.jetpack

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.main.sharedpreference.PreferManager
import com.example.ui.main.viewmodel.ProfileDetailsViewModel

@Composable
fun UserProfile(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<NavKey>(Profile) }
    val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)

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

@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAppearanceClick: () -> Unit = {}
) {
    val viewModel: ProfileDetailsViewModel = viewModel()
    val userDetails by viewModel.userDetails.observeAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
        containerColor = Color(0xFF11192C),
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
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = modifier.fillMaxWidth().padding(16.dp, 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
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
                        color = Color.White,
                        modifier = modifier.padding(5.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 0.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E293B)
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
                        Text(text = "12", color = Color.White)
                        Text(text = "Tasks", color = Color.White)
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.height(60.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Completed",
                            modifier = modifier.padding(5.dp),
                            tint = Color(0xFF3F52C8)
                        )
                        Text(text = "8", color = Color.White)
                        Text(text = "Completed", color = Color.White)
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.height(60.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = "Completed",
                            modifier = modifier.padding(5.dp),
                            tint = Color.Blue
                        )
                        Text(text = "4", color = Color.White)
                        Text(text = "Pending", color = Color.White)
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
        color = Color(0xFF1E293B)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Open $title",
                tint = Color(0xFF3F52C8),
                modifier = Modifier.padding(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, color = Color.White)
                Text(text = subtitle, color = Color.Gray)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open $title",
                tint = Color.White
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
