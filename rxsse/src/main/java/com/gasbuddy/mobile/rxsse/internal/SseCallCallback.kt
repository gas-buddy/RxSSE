package com.gasbuddy.mobile.rxsse.internal

import com.gasbuddy.mobile.rxsse.MessageProcessor
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

internal class SseCallCallback<T>(
    private val call: Call<T>,
    private val observer: Observer<in T>?,
    private val type: Type,
    private val messageProcessor: MessageProcessor
) : Disposable, Callback<T> {

    private var disposed = false
    private val sseEventListener = object :
        SseEventListener {
        override fun onSseComment(comment: String) {
            // ignored
        }

        override fun onSseFailure(throwable: Throwable) {
            observer?.onError(throwable)
        }

        override fun onSseMessage(lastEventId: String, eventName: String, message: String) {
            try {
                observer?.onNext(messageProcessor.processMessage(message, eventName, type))
            } catch (exception: Exception) {
                observer?.onError(exception)
            }
        }

        override fun onSseRetry(timeout: Long) {
            observer?.onError(UnsupportedOperationException("SSE retry is not supported by this adapter"))
        }
    }

    override fun isDisposed() = disposed

    override fun dispose() {
        call.cancel()
        disposed = true
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        if (disposed || call.isCanceled) return

        observer?.onError(throwable)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (disposed || call.isCanceled) return

        if (response.isSuccessful) {
            try {
                processResponse(response)
            } catch (throwable: Throwable) {
                observer?.onError(throwable)
            }
        } else {
            observer?.onError(HttpException(response))
        }
    }

    private fun processResponse(response: Response<T>) {
        (response.body() as? ResponseBody)?.source()?.use {
            source ->
            val reader = SseReader(source, sseEventListener)

            while (!disposed && !call.isCanceled && reader.read()) {
            }

            if (!disposed) {
                if (!call.isCanceled) {
                    observer?.onComplete()
                } else {
                    observer?.onError(RuntimeException())
                }
            }
        }
    }
}
