package com.altankoc.rotame.core.network

import com.altankoc.rotame.core.util.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.HttpException

private val gson = Gson()

suspend fun <T> safeApiCall(block: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(block())
    } catch (e: HttpException) {
        val errorMessage = parseErrorMessage(e.response()?.errorBody())
        Resource.Error(errorMessage)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Bağlantı hatası")
    }
}

private fun parseErrorMessage(errorBody: ResponseBody?): String {
    return try {
        val bodyString = errorBody?.string() ?: return "Bir hata oluştu"
        val jsonObject = gson.fromJson(bodyString, JsonObject::class.java)
        jsonObject.get("message")?.asString ?: "Bir hata oluştu"
    } catch (e: Exception) {
        "Bir hata oluştu"
    }
}