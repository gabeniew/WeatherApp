package com.gabrielniewielski.weatherapp.data.models.network

import retrofit2.Response

object NetworkResponseHandler {

    fun <T> handleResponse(
        response: Response<T>,
        successCallback: (data: T) -> Unit,
        errorCallback: (error: Throwable) -> Unit
    ) {
        if (response.isSuccessful) {
            response.body()?.let { successCallback(it) }
                ?: errorCallback(Throwable("Empty response body"))
        } else {
            errorCallback(Throwable("Error: ${response.code()} - ${response.message()}"))
        }
    }

    fun handleFailure(t: Throwable, errorCallback: (error: Throwable) -> Unit) {
        errorCallback(t)
    }
}