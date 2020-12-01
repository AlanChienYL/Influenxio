package com.influenxio.alan

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.influenxio.alan.databinding.ActivityMainBinding
import com.influenxio.alan.databinding.ItemBinding

class MainActivity : AppCompatActivity() {
    private val bank: Bank by lazy { Bank(handler) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private val counterViews: MutableList<ItemBinding> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initHandler()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bank = bank
        binding.activity = this
        setContentView(binding.root)
        handleWaiting()
        createCounterViews()
    }

    fun clickToAddCustom() {
        bank.createCustomer()
        binding.buttonNext.text = getString(R.string.next, bank.number)
    }

    private fun handleWaiting() {
        bank.currentWaitingLiveData.observe(this) {
            binding.textWaiting.text = getString(R.string.waiting, it)
        }
    }

    private fun initHandler() {
        val thread = HandlerThread("counterThread").apply{
            start()
        }
        handler = object : Handler(thread.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val counter = msg.obj as Counter
                counterViews[bank.counters.indexOf(counter)].counter = counter
            }
        }
    }

    private fun createCounterViews() {
        bank.counters.forEach { counter ->
            val view = ItemBinding.inflate(layoutInflater)
            view.counter = counter
            binding.linearLayout.addView(view.root)
            counterViews.add(view)
        }
    }
}