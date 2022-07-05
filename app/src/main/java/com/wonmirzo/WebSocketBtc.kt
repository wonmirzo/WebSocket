package com.wonmirzo

import android.util.Log
import com.google.gson.Gson
import com.wonmirzo.model.BitCoin
import com.wonmirzo.model.Currency
import com.wonmirzo.model.DataSend
import okhttp3.*
import okio.ByteString

class WebSocketBtc {
    private lateinit var mWebSocket: WebSocket
    private lateinit var socketListener: SocketListener
    private var gson = Gson()

    fun connectToSocket(currency: String) {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send(gson.toJson(Currency("bts:subscribe", DataSend(currency))))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("@@@", "Receiving : $text")
                val bitCoin = gson.fromJson(text, BitCoin::class.java)
                socketListener.onSuccess(bitCoin)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@", "Receiving bytes : $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@", "Closing : $code / $reason")
                // webSocket.close(1000, null)
                // webSocket.cancel()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@", "Error : ${t.message}")
                socketListener.onFailure(t.localizedMessage)
            }
        })

        client.dispatcher.executorService.shutdown()
    }

    fun socketListener(socketListener: SocketListener) {
        this.socketListener = socketListener
    }
}

interface SocketListener {
    fun onSuccess(bitCoin: BitCoin)
    fun onFailure(message: String)
}