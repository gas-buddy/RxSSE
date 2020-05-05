package com.gasbuddy.mobile.rxsse.internal

import com.gasbuddy.mobile.rxsse.MessageProcessor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import retrofit2.Call
import java.lang.reflect.Type

internal class SseEnqueueObservable<T>(
        private val originalCall: Call<T>,
        private val responseType: Type,
        private val messageProcessor: MessageProcessor
) : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>?) {
        val call = originalCall.clone()
        val callback = SseCallCallback<T>(
            call = call,
            observer = observer,
            type = responseType,
            messageProcessor = messageProcessor
        )

        observer?.onSubscribe(callback)
        call.enqueue(callback)
    }

}