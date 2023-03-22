package com.example.bin_information_cft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestCardData("45717360")
    }

    private fun requestCardData(bin: String) {
        val url = "https://lookup.binlist.net/" +
                bin
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    result -> Log.d("MyLog", "Result: $result")
            },
            {
                    error -> Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }
}