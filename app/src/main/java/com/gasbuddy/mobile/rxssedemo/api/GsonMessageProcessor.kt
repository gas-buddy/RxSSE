package com.gasbuddy.mobile.rxssedemo.api

import com.gasbuddy.mobile.rxsse.MessageProcessor
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.lang.reflect.Type


class GsonMessageProcessor: MessageProcessor {

    companion object {
        private val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()
    }

    override fun <T> processMessage(data: String, event: String, type: Type): T {
        return gson.fromJson<T>(data, type)
    }
}