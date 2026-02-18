package com.particlesector.soniflac.core.network.interceptor

import com.particlesector.soniflac.core.common.Constants
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", Constants.RadioBrowser.USER_AGENT)
            .build()
        return chain.proceed(request)
    }
}
