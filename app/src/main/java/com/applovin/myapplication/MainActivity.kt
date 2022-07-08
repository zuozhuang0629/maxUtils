package com.applovin.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.applovin.sdk.MaxUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    fun init() {
        MaxUtils.get().initSdk(this) {
            return@initSdk "RH7xIMirQp-k9XpQo6fmPQgzvCNPd1VTpxsoG4eyyoz2-fkg4HgvP7tWttcTng2iC9vnT4Mvp7Gi_69V2xZxPX"
        }

        MaxUtils.get().initMaxInter("069977ccb92b2669", this)

        findViewById<View>(R.id.button).setOnClickListener {
            MaxUtils.get().showMaxBanner(this, "10424d6678069178", findViewById(R.id.banner_f))

        }
        findViewById<View>(R.id.button2).setOnClickListener {
            MaxUtils.get().showInter(true, "069977ccb92b2669") {

            }
        }
        findViewById<View>(R.id.button3).setOnClickListener {
            MaxUtils.get().showMaxNative(this, "69065a61d0b36520", findViewById(R.id.native_f))
        }
    }
}