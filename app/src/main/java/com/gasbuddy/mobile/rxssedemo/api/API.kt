package com.gasbuddy.mobile.rxssedemo.api

import com.google.gson.annotations.SerializedName
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*


interface API {

    @Headers("Content-Type:application/json", "Accept:text/event-stream", "Cache-Control:no-cache")
    @GET("/my-end-point")
    @Streaming
    fun events() : Observable<SSEData>
}

data class SSEData(
    @SerializedName("thing") val someThing: String,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("important") val important: Boolean = false
)