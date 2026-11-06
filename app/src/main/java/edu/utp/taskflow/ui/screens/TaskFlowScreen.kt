package edu.utp.taskflow.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskFilter
import edu.utp.taskflow.model.TaskPriority
import edu.utp.taskflow.ui.theme.Muted
import edu.utp.taskflow.ui.theme.SoftBorder
import edu.utp.taskflow.ui.theme.Success
import edu.utp.taskflow.ui.theme.TaskFlowPalette


import edu.utp.taskflow.ui.components.TaskHeader
import edu.utp.taskflow.ui.components.ProgressCard
import edu.utp.taskflow.ui.components.StatisticsSection
import edu.utp.taskflow.ui.components.TaskFilterBar
import edu.utp.taskflow.ui.components.TaskCard
import edu.utp.taskflow.ui.components.EmptyTasksState

@Composable
fun TaskFlowScreen(
    tasks: List<Task>,
    selectedFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    onToggleTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onNewTaskClick: () -> Unit,
    palette: TaskFlowPalette,
    onPaletteChange: (TaskFlowPalette) -> Unit
) {
    val completed = tasks.count { it.isCompleted }
    val pending = tasks.size - completed
    val progress = if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size
    val visible = when (selectedFilter) {
        TaskFilter.ALL -> tasks
        TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 108.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TaskHeader(palette, onPaletteChange) }
            item { ProgressCard(progress, completed, tasks.size) }
            item { StatisticsSection(tasks.size, pending, completed) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Mis tareas", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Mantén el enfoque en lo importante.",
                        color = Muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item { TaskFilterBar(selectedFilter, onFilterChange) }
            if (visible.isEmpty()) {
                item { EmptyTasksState() }
            } else {
                items(visible, key = { it.id }) { task ->
                    TaskCard(
                        task,
                        onToggle = { onToggleTask(task.id) },
                        onDelete = { onDeleteTask(task.id) },
                        onClick = { onTaskClick(task.id) })
                }
            }
        }
        FloatingActionButton(
            onClick = onNewTaskClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(22.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "＋  Nueva tarea",
                modifier = Modifier.padding(horizontal = 18.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
