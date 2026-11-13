package edu.utp.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskFilter
import edu.utp.taskflow.ui.components.EmptyTasksState
import edu.utp.taskflow.ui.components.ProgressCard
import edu.utp.taskflow.ui.components.StatisticsSection
import edu.utp.taskflow.ui.components.TaskCard
import edu.utp.taskflow.ui.components.TaskFilterBar
import edu.utp.taskflow.ui.theme.Muted

@Composable
fun RemoteTasksScreen(
    tasks: List<Task>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {

    // Estado del filtro remoto
    var selectedFilter by rememberSaveable {
        mutableStateOf(TaskFilter.ALL)
    }

    // Estadísticas de las tareas remotas
    val completed = tasks.count {
        it.isCompleted
    }

    val pending = tasks.size - completed

    val progress =
        if (tasks.isEmpty()) {
            0f
        } else {
            completed.toFloat() / tasks.size
        }

    // Tareas visibles según el filtro
    val visible = when (selectedFilter) {

        TaskFilter.ALL -> tasks

        TaskFilter.PENDING ->
            tasks.filter { !it.isCompleted }

        TaskFilter.COMPLETED ->
            tasks.filter { it.isCompleted }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 28.dp,
            bottom = 28.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        // ENCABEZADO REMOTO
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
                    text = "Consultadas desde DummyJSON.",
                    color = Muted,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }
        }

        // MOSTRAR CARGA
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

        } else if (errorMessage != null) {

            // MOSTRAR ERROR
            item {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = onRetry
                    ) {
                        Text("Reintentar")
                    }
                }
            }

        } else {

            // PROGRESO: COMPONENTE REUTILIZADO
            item {
                ProgressCard(
                    progress,
                    completed,
                    tasks.size
                )
            }

            // ESTADÍSTICAS: COMPONENTE REUTILIZADO
            item {
                StatisticsSection(
                    tasks.size,
                    pending,
                    completed
                )
            }

            // FILTROS: COMPONENTE REUTILIZADO
            item {
                TaskFilterBar(
                    selectedFilter,
                    onChange = {
                        selectedFilter = it
                    }
                )
            }

            // ESTADO VACÍO O LISTA DE TAREAS
            if (visible.isEmpty()) {

                item {
                    EmptyTasksState()
                }

            } else {

                items(
                    items = visible,
                    key = { it.id }
                ) { task ->

                    TaskCard(
                        task = task,
                        onToggle = {},
                        onDelete = {},
                        onClick = {},
                        readOnly = true
                    )
                }
            }
        }
    }
}