package com.example.newzz

import android.app.Application
import com.example.newzz.util.ConnectivityObserver
import com.example.newzz.util.NetworkChecker

class MyApplication : Application() {
    lateinit var connectivityObserver: NetworkChecker

    override fun onCreate() {
        super.onCreate()
        connectivityObserver = NetworkChecker(this)
    }
}