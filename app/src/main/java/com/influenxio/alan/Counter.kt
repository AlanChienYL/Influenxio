package com.influenxio.alan

import android.os.Handler
import android.os.Message
import androidx.annotation.WorkerThread
import java.util.*
import kotlin.concurrent.schedule

data class Counter(
    var name: String? = null,
    var processing: String? = "idle",
    var processed: String = "",
    private val handler: Handler
) {
    var isRunning = false
        private set
    private var timerTask: TimerTask? = null

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Counter -> other.name == name
            else -> super.equals(other)
        }
    }

    fun handleCustomer(customer: Customer) {
        isRunning = true
        processing = customer.id.toString()
        update()
        timerTask = Timer().schedule(customer.processTime) {
            processing = "idle"
            processed += "${customer.id},"
            isRunning = false
            update()
        }
    }

    @WorkerThread
    private fun update() {
        val message = Message()
        message.obj = this
        handler.sendMessage(message)
    }
}