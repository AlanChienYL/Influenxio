package com.influenxio.alan

import android.os.Handler
import android.os.Message
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class Bank(private val handler: Handler) {
    private val counterNames: List<String> = arrayListOf("Amy", "Bob", "Cory", "Dora")
    val counters: MutableList<Counter> = arrayListOf()
    private val numberList = ArrayList<Int>()
    val currentWaitingLiveData: LiveData<Int>
        get() = currentWaitingMutableLiveData
    private val currentWaitingMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    var number = 1
        private set
    private val sharedCounterLock = ReentrantLock()

    init {
        counterNames.forEach { name ->
            val counter = Counter(name = name)
            counters.add(counter)
            createCounter(counter)
        }
    }

    fun createCustomer() {
        numberList.add(number)
        currentWaitingMutableLiveData.postValue(numberList.size)
        number += 1
    }

    private fun callNumber(): Int? {
        sharedCounterLock.lock()
        val call = numberList.removeFirstOrNull()
        currentWaitingMutableLiveData.postValue(numberList.size)
        sharedCounterLock.unlock()
        return call
    }

    private fun createCounter(counter: Counter) {
        Timer(counter.name, true).scheduleAtFixedRate(0, 1000) {
            callNumber()?.let { customer ->
                counter.processing = customer.toString()
                updateCounter(counter)
                Thread.sleep(Random.nextLong(1000, 2000))
                counter.processing = "idle"
                counter.processed += "$customer,"
                updateCounter(counter)
            }
        }
    }

    @WorkerThread
    private fun updateCounter(counter: Counter) {
        val message = Message()
        message.obj = counter
        handler.sendMessage(message)
    }
}