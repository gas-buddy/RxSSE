package com.gasbuddy.mobile.rxsse

import java.lang.reflect.Type

interface MessageProcessor {

    /**
     * Convert the SSE message and event information into a concrete class
     */
    fun <T> processMessage(data: String, event: String, type: Type): T
}