package com.gasbuddy.mobile.rxsse

import com.gasbuddy.mobile.rxsse.internal.SseCallAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Streaming
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * The call adapter that converts the SSE formatted web service data into Observables.
 * @param messageProcessor the processor that converts the data class into something usable by your code.
 * @param scheduler the rx thread to run on
 */
class RxJavaSseCallAdapterFactory(
    private val scheduler: Scheduler = Schedulers.io(),
    private val messageProcessor: MessageProcessor
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Must be annotated with @Streaming
        annotations.firstOrNull { it is Streaming } ?: return null

        // Must return an Observable
        val rawType = getRawType(returnType)
        if (rawType != Observable::class.java) {
            return null
        }

        // Must have a parameterized Observable
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Observable return type must be parameterized as Observable<Foo>")
        }

        val observableType = getParameterUpperBound(0, returnType)

        val responseType = when (getRawType(observableType)) {
            Response::class.java -> {
                if (observableType !is ParameterizedType) {
                    throw IllegalStateException("Response must be parameterized as Response<Foo>")
                }
                getParameterUpperBound(0, observableType)
            }
            else -> observableType
        }

        return SseCallAdapter(responseType, scheduler, messageProcessor)
    }
}