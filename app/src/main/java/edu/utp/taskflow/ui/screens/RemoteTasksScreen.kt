package edu.utp.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskFilter

import edu.utp.taskflow.ui.components.ProgressCard
import edu.utp.taskflow.ui.components.StatisticsSection
import edu.utp.taskflow.ui.components.TaskFilterBar
import edu.utp.taskflow.ui.components.TaskCard
import edu.utp.taskflow.ui.components.EmptyTasksState

import edu.utp.taskflow.ui.theme.Muted

@Composable
fun RemoteTasksScreen(
    tasks: List<Task>,
    isLoading: Boolean,
    isSaving: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onNewTaskClick: () -> Unit
) {

    // Filtro independiente de las tareas locales
    var selectedFilter by rememberSaveable {
        mutableStateOf(TaskFilter.ALL)
    }

    val completed = tasks.count {
        it.isCompleted
    }

    val pending = tasks.size - completed

    val progress =
        if (tasks.isEmpty()) 0f
        else completed.toFloat() / tasks.size

    val visible = when (selectedFilter) {

        TaskFilter.ALL -> tasks

        TaskFilter.PENDING ->
            tasks.filter { !it.isCompleted }

        TaskFilter.COMPLETED ->
            tasks.filter { it.isCompleted }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 108.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // ENCABEZADO
            item {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "Tareas en línea",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Gestiona tus tareas desde MockAPI.",
                        color = Muted,
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ESTADO DE CARGA
            if (isLoading) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        CircularProgressIndicator()

                        Text("Consultando tareas...")
                    }
                }

            } else {

                // MENSAJE DE ERROR
                if (errorMessage != null) {

                    item {
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = errorMessage,
                                color =
                                    MaterialTheme.colorScheme.error
                            )

                            Button(
                                onClick = onRetry,
                                enabled = !isSaving
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // PROGRESO
                item {
                    ProgressCard(
                        progress,
                        completed,
                        tasks.size
                    )
                }

                // ESTADÍSTICAS
                item {
                    StatisticsSection(
                        tasks.size,
                        pending,
                        completed
                    )
                }

                // TÍTULO DE SECCIÓN
                item {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {

                        Text(
                            text = "Mis tareas remotas",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Tareas almacenadas en la API.",
                            color = Muted,
                            style =
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // FILTROS
                item {
                    TaskFilterBar(
                        selectedFilter,
                        onChange = {
                            selectedFilter = it
                        }
                    )
                }

                // LISTA DE TAREAS
                if (visible.isEmpty()) {

                    item {
                        EmptyTasksState()
                    }

                } else {

                    items(
                        visible,
                        key = { it.id }
                    ) { task ->

                        TaskCard(
                            task = task,

                            onToggle = {
                                onToggleTask(task)
                            },

                            onDelete = {
                                onDeleteTask(task.id)
                            },

                            onClick = {
                                onTaskClick(task.id)
                            },

                            readOnly = false,

                            enabled = !isSaving
                        )
                    }
                }
            }
        }

        // BOTÓN PARA CREAR TAREAS
        if (!isLoading && !isSaving) {

            FloatingActionButton(
                onClick = onNewTaskClick,

                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(22.dp),

                containerColor =
                    MaterialTheme.colorScheme.primary,

                contentColor = Color.White,

                shape = RoundedCornerShape(20.dp)
            ) {

                Text(
                    text = "＋  Nueva tarea",

                    modifier = Modifier.padding(
                        horizontal = 18.dp
                    ),

                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}