package com.example.bin_information_cft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bin_information_cft.adapters.CardModel
import org.json.JSONObject

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
            { result ->
                parseCardData(result)
            },
            { error ->
                Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    private fun parseCardData(result: String) {
        val mainObject = JSONObject(result)
        val item = CardModel(
            mainObject.getJSONObject("number").getString("length"),
            mainObject.getJSONObject("number").getString("luhn"),
            mainObject.getString("scheme"),
            mainObject.getString("type"),
            mainObject.getString("brand"),
            mainObject.getString("prepaid"),
            mainObject.getJSONObject("country").getString("numeric"),
            mainObject.getJSONObject("country").getString("alpha2"),
            mainObject.getJSONObject("country").getString("name"),
            mainObject.getJSONObject("country").getString("emoji"),
            mainObject.getJSONObject("country").getString("currency"),
            mainObject.getJSONObject("country").getString("latitude"),
            mainObject.getJSONObject("country").getString("longitude"),
            mainObject.getJSONObject("bank").getString("name"),
            mainObject.getJSONObject("bank").getString("url"),
            mainObject.getJSONObject("bank").getString("phone"),
            mainObject.getJSONObject("bank").getString("city"),
        )
    }
}