package com.wonmirzo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wonmirzo.databinding.ActivityMainBinding
import com.wonmirzo.model.BitCoin
import okhttp3.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var webSocketBtc: WebSocketBtc

    private var lineValues = ArrayList<Entry>()
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webSocketBtc = WebSocketBtc()

        initViews()
    }

    private fun initViews() {
        webSocketBtc.connectToSocket("live_trades_btcusd")
        webSocketBtc.socketListener(object : SocketListener {
            override fun onSuccess(bitCoin: BitCoin) {
                count++
                runOnUiThread {
                    if (bitCoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(
                            this@MainActivity,
                            "Successfully Connected, ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lineValues.add(Entry(count.toFloat(), bitCoin.data.price.toFloat()))
                        Toast.makeText(this@MainActivity, "Connected Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(message: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}