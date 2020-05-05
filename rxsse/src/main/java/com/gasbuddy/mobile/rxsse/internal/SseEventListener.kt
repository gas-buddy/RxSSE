package com.gasbuddy.mobile.rxsse.internal

internal interface SseEventListener {
    fun onSseFailure(throwable: Throwable)
    fun onSseMessage(lastEventId: String, eventName: String, message: String)
    fun onSseRetry(timeout: Long)
    fun onSseComment(comment: String)
}