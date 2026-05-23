package com.bousmah.meteoapp_marouane.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val images: List<String>? = null
)

data class OllamaResponse(
    val response: String
)

interface ChatApi {
    @POST("api/generate")
    suspend fun generate(@Body request: OllamaRequest): OllamaResponse

    companion object {
        const val BASE_URL = "http://10.0.2.2:11434/"
    }
}
