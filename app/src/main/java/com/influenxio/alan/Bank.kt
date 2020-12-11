package com.influenxio.alan

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class Bank(private val handler: Handler) {
    private val counterNames: List<String> = arrayListOf("Amy", "Bob", "Cory", "Dora")
    val counters: MutableList<Counter> = arrayListOf()
    private val numberList = ArrayList<Customer>()
    val currentWaitingLiveData: LiveData<Int>
        get() = currentWaitingMutableLiveData
    private val currentWaitingMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    var number = 1
        private set

    init {
        counterNames.forEach { name ->
            val counter = Counter(name = name, handler = handler)
            counters.add(counter)
        }
        Timer().scheduleAtFixedRate(0, 1000) {
            while (numberList.isNotEmpty()) {
                run {
                    counters.forEach {
                        if (!it.isRunning) {
                            getNextCustomer()?.let { customer ->
                                it.handleCustomer(customer)
                            }
                            return@run
                        }
                    }
                }
            }
        }
    }

    fun createCustomer() {
        numberList.add(Customer(number, Random.nextLong(1000, 2000)))
        currentWaitingMutableLiveData.postValue(numberList.size)
        number += 1
    }

    private fun getNextCustomer(): Customer? {
        val next = numberList.removeFirstOrNull()
        currentWaitingMutableLiveData.postValue(numberList.size)
        return next
    }
}