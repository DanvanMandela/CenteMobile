package com.craft.silicon.centemobile.data.source.remote.callback

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

interface RemoteCallback<T> : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        when (response.code()) {
            HttpsURLConnection.HTTP_OK,
            HttpsURLConnection.HTTP_CREATED,
            HttpsURLConnection.HTTP_ACCEPTED,
            HttpURLConnection.HTTP_BAD_REQUEST,
            HttpsURLConnection.HTTP_NOT_AUTHORITATIVE ->
                response.body()?.apply { onSuccess(this) }
            HttpURLConnection.HTTP_UNAUTHORIZED -> onUnauthorized()
            else -> onFailed(Throwable("Default " + response.code() + " " + response.message()))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        t.apply { onFailed(this) }
    }

    fun onSuccess(responseBody: T)
    fun onUnauthorized()
    fun onFailed(throwable: Throwable)
}