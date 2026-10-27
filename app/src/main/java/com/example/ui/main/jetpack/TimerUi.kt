package com.example.ui.main.jetpack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.ui.main.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun CheckInUi(modifier: Modifier = Modifier) {
    val viewModel: LoginViewModel = viewModel()
    val selectedItem = viewModel.selectedItem
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem.value,
                onItemSelected = { selectedItem.value = it }
            )
        },
        containerColor = Color(0xFF11192C)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem.intValue) {
                0 -> CheckInContent()
                1 -> UserTasks()
                2 -> UserProfile()
            }
        }
    }
}

@Composable
fun CheckInContent(modifier: Modifier = Modifier) {
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
    onAttendanceClick: () -> Unit = {},
    onMyTasksClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF11192C))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Text(
            text = "Check In",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        // Greeting Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF1E293B)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good Morning 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Hello, Developer",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3159C9).copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = Color(0xFF6D9EFF),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Daily Progress Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF3159C9)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR DAILY PROGRESS",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "60%",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "3 of 5 tasks completed",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )

                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )

                Text(
                    text = "Keep going, you're doing great!",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E293B),
                onClick = onAttendanceClick
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF11192C),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Attendance",
                                tint = Color(0xFF6D9EFF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Check status",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E293B),
                onClick = onMyTasksClick
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF11192C),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "My Tasks",
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Check tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Recent Activity Section
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF1E293B)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4ADE80).copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Completed Profile Setup",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Today at 9:30 AM",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF2E3B52), thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFBBF24).copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = "Pending",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Review Pending Tasks",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Today • Pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf("Home", "Tasks", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.CheckCircle,
        Icons.Default.Person
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF1E293B)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = { onItemSelected(index) },
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
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF3159C9),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
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
                    tint = Color(0xFF3159C9),
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
