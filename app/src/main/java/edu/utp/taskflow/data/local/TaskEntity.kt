package edu.utp.taskflow.data.local

import edu.utp.taskflow.model.TaskPriority

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val category: String,

    val priority: String,

    val isCompleted: Boolean = false
)