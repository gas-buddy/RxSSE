package com.gasbuddy.mobile.rxsse.internal

import com.gasbuddy.mobile.rxsse.MessageProcessor
import io.reactivex.rxjava3.core.Scheduler
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class SseCallAdapter(
        private val responseType: Type,
        private val scheduler: Scheduler,
        private val messageProcessor: MessageProcessor
) : CallAdapter<Any, Any> {

    override fun adapt(call: Call<Any>): Any = SseEnqueueObservable(
        originalCall = call,
        responseType = responseType,
        messageProcessor = messageProcessor
    ).subscribeOn(scheduler)

    override fun responseType(): Type = ResponseBody::class.java
}