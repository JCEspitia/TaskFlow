package edu.utp.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority

@Composable
fun TaskDetailScreen(
    task: Task?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalle de tarea",
            style = MaterialTheme.typography.headlineMedium
        )

        if (task == null) {
            Text(
                text = "No se encontró la tarea",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Identificador: ${task.id}"
                    )
                    Text(
                        text = "Categoría: ${task.category}"
                    )

                    val priorityText = when (task.priority) {
                        TaskPriority.HIGH -> "Alta"
                        TaskPriority.MEDIUM -> "Media"
                        TaskPriority.LOW -> "Baja"
                    }

                    Text(
                        text = "Prioridad: $priorityText"
                    )

                    val statusText =
                        if (task.isCompleted) {
                            "Completada"
                        } else {
                            "Pendiente"
                        }

                    Text(
                        text = "Estado: $statusText"
                    )
                }
            }
        }
        
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a mis tareas")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
    TaskDetailScreen(
        Task(
            1,
            "hola",
            "prueba",
            TaskPriority.MEDIUM
        ),
        {}
    )
}