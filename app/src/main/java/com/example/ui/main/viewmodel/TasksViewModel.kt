package com.example.ui.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.main.model.Product
import com.example.ui.main.repository.LoginRepository
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    val loginRepository = LoginRepository()
    val selectedIndex = mutableStateOf(0)
    val productList = mutableStateOf<List<Product>>(emptyList())
    val completedTaskIds: SnapshotStateList<Int> = mutableStateListOf()

    private val sampleTaskTitles = listOf(
        "Design System Component Audit",
        "Review Code Pull Requests",
        "Database Migration & Backup",
        "Weekly Team Standup Meeting",
        "API Endpoint Security Review",
        "Frontend Performance Optimization",
        "User Onboarding Flow Testing",
        "Update Project Documentation",
        "CI/CD Pipeline Build Fixes",
        "Sprint Planning & Retrospective"
    )

    private val sampleTaskDescriptions = listOf(
        "Review and align UI components with Material 3 design guidelines.",
        "Inspect pending PRs and approve changes for the upcoming release.",
        "Verify database schema integrity and create backup snapshots.",
        "Sync with team leads on daily task progress and blockers.",
        "Audit authentication tokens and secure REST API endpoints.",
        "Improve Compose UI rendering speed and eliminate frame drops.",
        "Test user sign-up, login, and profile management flows.",
        "Write KDoc comments and update technical architecture docs.",
        "Fix failing unit test builds and streamline release deployment.",
        "Organize upcoming backlog tasks and assign story points."
    )

    init {
        fetchProducts(0, 10)
    }

    fun fetchProducts(skip: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            try {
                val response = loginRepository.getList(skip, limit)
                if (response?.isSuccessful == true) {
                    val rawProducts = response.body()?.products ?: emptyList()
                    val mappedProducts = rawProducts.mapIndexed { index, product ->
                        product.copy(
                            title = sampleTaskTitles.getOrElse(index % sampleTaskTitles.size) { product.title },
                            description = sampleTaskDescriptions.getOrElse(index % sampleTaskDescriptions.size) { product.description }
                        )
                    }
                    productList.value = mappedProducts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        if (completedTaskIds.contains(taskId)) {
            completedTaskIds.remove(taskId)
        } else {
            completedTaskIds.add(taskId)
        }
    }

    fun isTaskCompleted(taskId: Int): Boolean {
        return completedTaskIds.contains(taskId)
    }
}
