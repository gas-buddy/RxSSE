package com.gasbuddy.mobile.rxsse.internal

import okio.BufferedSource
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.TimeUnit

internal class SseReader(
    private val source: BufferedSource,
    private val listener: SseEventListener,
    readTimeOutInMillis: Long = 0L // defaults to unlimited
) {

    init {
        source.timeout().timeout(readTimeOutInMillis, TimeUnit.MILLISECONDS)
    }

    private val data = StringBuilder()
    private var lastEventId: String = ""
    private var eventName: String = ""

    fun read(): Boolean {
        try {
            processLine(source.readUtf8LineStrict())
        } catch (e: EOFException) {
            // The read readUtf8LineStrict throws at the end of the stream, this is "normal". This
            // can happen if the server just closes the connection unexpectedly, we have no way to
            // determine what happened.  An incomplete data block will NOT be processed but we will attempt
            // to send any full lines we've already received to the listener.
            sendEvent()
            return false
        } catch (e: IOException) {
            listener.onSseFailure(e)
            return false
        }

        return true
    }

    private fun processLine(line: String) {
        val lineType = LineType.fromLine(line)
        val trimmedLine = line.removePrefix(lineType.value).trim()

        when (lineType) {
            LineType.EMPTY -> sendEvent()
            LineType.EVENT -> eventName = trimmedLine
            LineType.DATA -> data.append(trimmedLine)
            LineType.ID -> {
                sendEvent(); lastEventId = trimmedLine
            }
            LineType.RETRY -> listener.onSseRetry(trimmedLine.toLongOrNull() ?: 0L)
            LineType.COMMENT -> {
                listener.onSseComment(trimmedLine); sendEvent()
            }
        }
    }

    private fun sendEvent() {
        if (data.isNotEmpty()) {
            listener.onSseMessage(lastEventId, eventName, data.toString())
            data.clear()
            eventName = ""
        }
    }
}

private enum class LineType(val value: String) {
    DATA("data:"),
    EVENT("event:"),
    ID("id:"),
    RETRY("retry:"),
    EMPTY(""),
    COMMENT(":");

    companion object {
        fun from(string: String) = values().firstOrNull { string == it.value } ?: EMPTY
        fun fromLine(string: String) = values().firstOrNull { string.startsWith(it.value) } ?: EMPTY
    }
}

