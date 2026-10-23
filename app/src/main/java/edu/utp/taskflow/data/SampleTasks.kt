package edu.utp.taskflow.data

import edu.utp.taskflow.model.Task
import edu.utp.taskflow.model.TaskPriority

val sampleTasks = listOf(
    Task(1, "Preparar presentación del proyecto", "Universidad", TaskPriority.HIGH),
    Task(2, "Revisar documentación de Compose", "Aprendizaje", TaskPriority.MEDIUM),
    Task(3, "Organizar repositorio GitHub", "Desarrollo", TaskPriority.LOW),
    Task(4, "Planificar actividades de la semana", "Personal", TaskPriority.HIGH),
    Task(5, "Enviar avances del proyecto", "Universidad", TaskPriority.MEDIUM),
    Task(6, "Leer sobre arquitectura Android", "Aprendizaje", TaskPriority.LOW)
)
