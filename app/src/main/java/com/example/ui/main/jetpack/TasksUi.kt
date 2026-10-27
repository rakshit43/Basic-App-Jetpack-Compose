package com.example.ui.main.jetpack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.main.model.Product
import com.example.ui.main.viewmodel.TasksViewModel


@Composable
fun UserTasks(
    viewModel: TasksViewModel = viewModel(),
    products: List<Product> = viewModel.productList.value
) {
    val tasks = {
        listOf(
            "overview", "projects", "schedule", "favorites", "tasks", "activity"
        )
    }
    var searchTasks by remember { mutableStateOf("") }
    val filteredTasks = products.filter { it.title.contains(searchTasks, ignoreCase = true)
            || it.description.contains(searchTasks, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color(0xFF11192C),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
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
                .padding(10.dp),

        ) {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = searchTasks,
                onValueChange = { searchTasks = it },
                label = {
                    Text(
                        "Search Tasks",
                        color = Color.White
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF324154),
                    unfocusedContainerColor = Color(0xFF324154),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        tint = Color(0xFF3F52C8),
                        contentDescription = "Search"
                    )
                }
            )
            Spacer(modifier = Modifier.padding(10.dp))
            if(products.isEmpty())
            {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFF3F52C8))
                }
            }
            else{
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    itemsIndexed(tasks()) { index, category ->
                        val selectedIndex = viewModel.selectedIndex.value
                        val isSelected = index == selectedIndex

                        Surface(
                            color = if (isSelected) Color(0xFF3F52C8) else Color.LightGray,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable {
                                viewModel.selectedIndex.value = index
                            }
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else Color.Black,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 10.dp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                        itemsIndexed(
                            filteredTasks
                        ) { _, product ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = product.title,
                                        color = Color.White
                                    )
                                    Text(
                                        text = product.description,
                                        color = Color.LightGray
                                    )
                        }
                    }
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