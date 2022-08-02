package com.craft.silicon.centemobile.data.source.remote

import com.craft.silicon.centemobile.util.AppLogger
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

interface TimeoutInterceptor {
    fun onTimedOut(chain: Interceptor.Chain): Boolean {
        throw Exception("Not implemented")
    }

    fun intercept(chain: Interceptor.Chain): Response {
        throw Exception("Not implemented")
    }
}

class TimeoutInterceptorImpl : TimeoutInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (onTimedOut(chain))
            throw SocketTimeoutException()
        return chain.proceed(chain.request())
    }

    override fun onTimedOut(chain: Interceptor.Chain): Boolean {
        try {
            val response = chain.proceed(chain.request())
            val content = response.toString()
            response.close()
            AppLogger.instance.appLog("Doctor Who:", "Timed Out: $content")
        } catch (e: SocketTimeoutException) {
            return true
        }
        return false
    }
}