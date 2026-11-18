package edu.utp.taskflow.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApiService {
    // USUARIOS
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    // TAREAS
    @GET("tasks")
    suspend fun getTasks(
        @Query("userId") userId: String
    ): List<RemoteTaskDto>

    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskRequestDto
    ): RemoteTaskDto

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body task: TaskRequestDto
    ) : RemoteTaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String
    ): RemoteTaskDto
}
