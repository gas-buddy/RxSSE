package com.gasbuddy.mobile.rxssedemo.api

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val responseString = sseEvents

        return chain.proceed(chain.request())
            .newBuilder()
            .code(200)
            .protocol(Protocol.HTTP_2)
            .message(responseString)
            .body(responseString.toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}


const val sseEvents = """
id:1
event: SSE Sample Event
data: {"thing":"A thing happened","count":42}

id:2
data: {"thing":"Loud noises!","count":22}

: This SSE data elements can be multiline.
: The data is complete when two new line feeds are received.  ie. \n\n
id:3
event: Multi Line Event
data: {
data: "thing":"Really neat event",
data: "count":32,
data: "important": true
data: }

"""
