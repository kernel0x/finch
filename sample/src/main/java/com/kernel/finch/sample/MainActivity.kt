package com.kernel.finch.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kernel.finch.Finch
import com.kernel.finch.log.FinchLogger
import com.kernel.finch.networklog.okhttp.FinchOkHttpLogger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var cb: Callback<Void> = object : Callback<Void> {
        override fun onResponse(call: Call<Void>?, response: Response<Void>?) {}
        override fun onFailure(call: Call<Void>?, t: Throwable?) {
            t?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.buttonFinch).setOnClickListener { onStartFinch() }
        findViewById<View>(R.id.buttonHttpRequests).setOnClickListener { onStartRequests() }
    }

    private fun onStartFinch() {
        FinchLogger.log("onStartFinch")
        Finch.showNetworkEventListActivity()
    }

    private fun onStartRequests() {
        FinchLogger.log("onStartRequests")
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(FinchOkHttpLogger.logger as Interceptor)
            .build()
        val api = ApiService.getInstance(okHttpClient)
        api.get().enqueue(cb)
        api.post(ApiService.Data("ololo")).enqueue(cb)
        api.patch(ApiService.Data("ololo")).enqueue(cb)
        api.put(ApiService.Data("ololo")).enqueue(cb)
        api.delete().enqueue(cb)
        api.delay(4).enqueue(cb)
        api.delay(12).enqueue(cb)
        api.redirectTo("https://google.com").enqueue(cb)
        api.gzip().enqueue(cb)
        api.html().enqueue(cb)
        api.xml().enqueue(cb)
        api.json().enqueue(cb)
        api.stream(500).enqueue(cb)
        api.streamBytes(2048).enqueue(cb)
        api.image("image/png").enqueue(cb)
        api.cookieSet("cookie").enqueue(cb)
        api.basicAuth("user", "passwd").enqueue(cb)
        api.deny().enqueue(cb)
        api.status(205).enqueue(cb)
        api.status(403).enqueue(cb)
        api.status(500).enqueue(cb)
        api.status(999).enqueue(cb)
        api.cache(25).enqueue(cb)
    }
}
