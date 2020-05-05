package com.gasbuddy.mobile.rxsse

import java.lang.reflect.Type


interface MessageProcessor {
    fun <T> processMessage(data: String, event: String, type: Type): T
}