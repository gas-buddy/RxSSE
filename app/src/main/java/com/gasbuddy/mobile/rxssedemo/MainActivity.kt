package com.gasbuddy.mobile.rxssedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gasbuddy.mobile.rxsse.RxJavaSseCallAdapterFactory
import com.gasbuddy.mobile.rxssedemo.api.API
import com.gasbuddy.mobile.rxssedemo.api.GsonMessageProcessor
import com.gasbuddy.mobile.rxssedemo.api.MockInterceptor
import com.gasbuddy.mobile.rxssedemo.api.SSEData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit


class MainActivity : AppCompatActivity() {

    private val sseCallAdapterFactory = RxJavaSseCallAdapterFactory(
        scheduler = Schedulers.io(),
        messageProcessor = GsonMessageProcessor()
    )

    // Don't use this in a real app, this is just for demo purposes
    private val api = Retrofit.Builder()
        .baseUrl("https://www.example.com")
        .addCallAdapterFactory(sseCallAdapterFactory) // add the SSE call adapter factory to process SSE events
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(MockInterceptor()) // add a fake response
                .build()
        )
        .build()
        .create(API::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        api.events()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<SSEData>() {
                override fun onComplete() {
                    println("On complete")
                    dispose()
                }

                override fun onNext(SSEData: SSEData) {
                    println("On next + $SSEData")
                }

                override fun onError(e: Throwable) {
                    println("On error + ${e.localizedMessage}")
                    dispose()
                }
            })

    }
}
