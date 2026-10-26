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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.main.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun CheckInUi(modifier: Modifier = Modifier) {
    val viewModel : LoginViewModel = viewModel()
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF11192C))
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Good Morning 👋",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Text(
            text = "Hello, Developer",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF3159C9),
                    RoundedCornerShape(16.dp)
                )
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "YOUR DAILY PROGRESS",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "3 of 5 tasks",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            LinearProgressIndicator(
                progress = { 0.6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = Color.White,
                trackColor = Color(0xFF7895E5),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            Text(
                text = "Keep going, you're doing great!",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Quick Actions",
            Modifier.padding(10.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Attendance",
                        tint = Color(0xFF6D9EFF)
                    )
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Check your status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Attendance",
                        tint = Color.Green
                    )
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Check your tasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        Text(
            text = "Recent Activity",
            Modifier.padding(10.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            onClick = {}
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Attendance",
                        tint = Color.Green
                    )

                    Column {
                        Text(
                            text = "Completed Profile Setup",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Text(
                            text = "Today at 9:30",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = "Attendance",
                        tint = Color.Yellow
                    )

                    Column {
                        Text(
                            text = "Review Pending Tasks",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Text(
                            text = "Today : Pending",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )
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

    NavigationBar(
        containerColor = Color(0xFF1E293B),
        modifier = Modifier.clip(shape = RoundedCornerShape(18.dp)),
        windowInsets = WindowInsets(0,0,0,0)
    ) {
        items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = {
                        onItemSelected(index)
                    },
                    icon = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(item)
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = item
                            )
                        }
                    },
                    label = {
                        Text(item)
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


