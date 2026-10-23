package edu.utp.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ModalBottomSheet

import edu.utp.taskflow.data.sampleTasks
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskFilter
import edu.utp.taskflow.model.TaskPriority
import edu.utp.taskflow.ui.screens.TaskFlowScreen
import edu.utp.taskflow.ui.theme.TaskFlowPalette
import edu.utp.taskflow.ui.theme.TaskFlowTheme
import edu.utp.taskflow.ui.components.NewTaskForm
import edu.utp.taskflow.viewmodel.TaskViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TaskFlowDemo() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFlowDemo(taskViewModel: TaskViewModel = viewModel()) {
    // Único estado interactivo de la base visual: cambiar la paleta.
    // Las operaciones sobre tareas siguen pendientes para la clase.
    var palette by remember { mutableStateOf(TaskFlowPalette.OCEAN) }

    var showNewTaskSheet by remember {
        mutableStateOf(false)
    }

    var newTaskTitle by remember {
        mutableStateOf("")
    }

    var newTaskPriority by remember {
        mutableStateOf(TaskPriority.MEDIUM)
    }

    TaskFlowTheme(palette = palette) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TaskFlowScreen(
                tasks = taskViewModel.tasks,
                selectedFilter = taskViewModel.selectedFilter,
                onFilterChange = taskViewModel::changeFilter,
                onToggleTask = taskViewModel::toggleTask,
                onDeleteTask = taskViewModel::deleteTask,
                onNewTaskClick = {
                    newTaskTitle = ""
                    newTaskPriority = TaskPriority.MEDIUM
                    showNewTaskSheet = true
                },
                palette = palette,
                onPaletteChange = { palette = it }
            )
        }

        if (showNewTaskSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showNewTaskSheet = false
                }
            ) {
                NewTaskForm(
                    title = newTaskTitle,
                    onTitleChange = { newTaskTitle = it },
                    selectedPriority = newTaskPriority,
                    onPriorityChange = { newTaskPriority = it },
                    onSave = {
                        if (newTaskTitle.isNotBlank()) {
                            taskViewModel.addTask(
                                title = newTaskTitle,
                                priority = newTaskPriority
                            )

                            showNewTaskSheet = false
                        }
                    },
                    onCancel = {
                        showNewTaskSheet = false
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskFlowPreview() {
    TaskFlowDemo()
}
