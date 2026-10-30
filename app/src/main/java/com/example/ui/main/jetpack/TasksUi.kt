package com.example.ui.main.jetpack

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.main.database.UserDatabase
import com.example.ui.main.model.Product
import com.example.ui.main.model.UserTask
import com.example.ui.main.viewmodel.TasksViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTasks(
    viewModel: TasksViewModel = viewModel(),
    products: List<Product> = viewModel.productList.value
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDao = remember { UserDatabase.getDatabase(context).userDao() }
    val dbTasks by userDao.getAllTasksFlow().collectAsState(initial = emptyList())

    val tasksCategories = listOf("overview", "projects", "schedule", "favorites", "tasks", "activity")
    var searchTasks by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDesc by remember { mutableStateOf("") }
    var newTaskCategory by remember { mutableStateOf("Work") }
    var titleError by remember { mutableStateOf(false) }

    var taskToDelete by remember { mutableStateOf<UserTask?>(null) }

    val selectedIndex = viewModel.selectedIndex.value
    val selectedCategory = if (selectedIndex in tasksCategories.indices) tasksCategories[selectedIndex] else "overview"

    val filteredDbTasks = dbTasks.filter { task ->
        val matchesSearch = task.title.contains(searchTasks, ignoreCase = true) ||
                task.description.contains(searchTasks, ignoreCase = true)
        val matchesCategory = if (selectedCategory.equals("overview", ignoreCase = true)) {
            true
        } else {
            task.category.contains(selectedCategory, ignoreCase = true) ||
                    task.title.contains(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    val filteredApiProducts = products.filter { product ->
        val matchesSearch = product.title.contains(searchTasks, ignoreCase = true) ||
                product.description.contains(searchTasks, ignoreCase = true)
        val matchesCategory = if (selectedCategory.equals("overview", ignoreCase = true)) {
            true
        } else {
            product.category.contains(selectedCategory, ignoreCase = true) ||
                    product.title.contains(selectedCategory, ignoreCase = true) ||
                    product.description.contains(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${taskToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        val task = taskToDelete
                        if (task != null) {
                            scope.launch { userDao.deleteTask(task.id) }
                            Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                        }
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF87171))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { taskToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    titleError = false
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = searchTasks,
                onValueChange = { searchTasks = it },
                label = {
                    Text(
                        "Search Tasks",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Search"
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(tasksCategories) { index, category ->
                    val isSelected = index == selectedIndex

                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable {
                            viewModel.selectedIndex.value = index
                        }
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredDbTasks.isEmpty() && products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Database tasks list
                    items(filteredDbTasks, key = { "db_${it.id}" }) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { checked ->
                                        scope.launch {
                                            userDao.updateTaskCompletion(task.id, checked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF4ADE80),
                                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        checkmarkColor = Color.Black
                                    )
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = task.title,
                                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (task.isOnHold) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "ON HOLD",
                                                        color = Color(0xFFF59E0B),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = task.category,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = task.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Hold / Resume Button
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                userDao.updateTaskHold(task.id, !task.isOnHold)
                                                Toast.makeText(
                                                    context,
                                                    if (!task.isOnHold) "Task put on hold" else "Task resumed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (task.isOnHold) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = "Toggle Hold",
                                            tint = if (task.isOnHold) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Delete Button
                                    IconButton(
                                        onClick = { taskToDelete = task }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Task",
                                            tint = Color(0xFFF87171)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // API products list
                    itemsIndexed(filteredApiProducts) { _, product ->
                        val isCompleted = viewModel.isTaskCompleted(product.id)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = isCompleted,
                                    onCheckedChange = {
                                        viewModel.toggleTaskCompletion(product.id)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF4ADE80),
                                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        checkmarkColor = Color.Black
                                    )
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = product.title,
                                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = product.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Create New Task",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = {
                        newTaskTitle = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Task Title *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text("Task title cannot be empty!", color = Color(0xFFF87171))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = newTaskDesc,
                    onValueChange = { newTaskDesc = it },
                    label = { Text("Task Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val categoriesList = listOf("Work", "Urgent", "Personal", "Design", "Dev")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoriesList) { cat ->
                        val isSel = (newTaskCategory == cat)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            modifier = Modifier.clickable { newTaskCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newTaskTitle.trim().isEmpty()) {
                            titleError = true
                            Toast.makeText(context, "Credentials required: Task title cannot be empty!", Toast.LENGTH_SHORT).show()
                        } else {
                            val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            scope.launch {
                                userDao.insertTask(
                                    UserTask(
                                        title = newTaskTitle.trim(),
                                        description = newTaskDesc.trim().ifEmpty { "Created in tasks tab" },
                                        category = newTaskCategory,
                                        createdAt = timeFormatted
                                    )
                                )
                                Toast.makeText(context, "Task saved to database!", Toast.LENGTH_SHORT).show()
                            }
                            newTaskTitle = ""
                            newTaskDesc = ""
                            titleError = false
                            showBottomSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Save Task",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UserTasksPreview() {
    UserTasks(
        products = listOf()
    )
}
