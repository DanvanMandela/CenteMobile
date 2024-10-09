package com.elmacentemobile.data.source.remote

import okhttp3.Interceptor
import okhttp3.Response


class AuthorizationInterceptor constructor(
    private val tokenType: String,
    private val accessToken: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header(tokenType, accessToken).build()

        return chain.proceed(request)
    }
}