package edu.utp.taskflow.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TaskApiService {

    @GET("todos")
    suspend fun getTasks(
        @Query("limit") limit: Int = 10
    ): RemoteTasksResponse

}
