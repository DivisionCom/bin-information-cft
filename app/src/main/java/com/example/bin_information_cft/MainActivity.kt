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
            getCardData(mainObject, "number", "length", true),
            getCardData(mainObject, "number", "luhn", true),
            getCardData(mainObject, "", "scheme", false),
            getCardData(mainObject, "", "type", false),
            getCardData(mainObject, "", "brand", false),
            getCardData(mainObject, "", "prepaid", false),
            getCardData(mainObject, "country", "numeric", true),
            getCardData(mainObject, "country", "alpha2", true),
            getCardData(mainObject, "country", "name", true),
            getCardData(mainObject, "country", "emoji", true),
            getCardData(mainObject, "country", "currency", true),
            getCardData(mainObject, "country", "latitude", true),
            getCardData(mainObject, "country", "longitude", true),
            getCardData(mainObject, "bank", "name", true),
            getCardData(mainObject, "bank", "url", true),
            getCardData(mainObject, "bank", "phone", true),
            getCardData(mainObject, "bank", "city", true),
        )
    }

    private fun getCardData(
        mainObject: JSONObject,
        jsonObject: String,
        jsonString: String,
        getJSONobj: Boolean
    ): String {
        val validatedData: String
        when (getJSONobj) {
            true -> {
                validatedData = when {
                    mainObject.has(jsonObject) -> {
                        when {
                            mainObject.getJSONObject(jsonObject).has(jsonString) -> {
                                mainObject.getJSONObject(jsonObject).getString(jsonString)
                            }
                            else -> {
                                ""
                            }
                        }
                    }
                    else -> {
                        ""
                    }
                }
            }
            false -> {
                validatedData = when {
                    mainObject.has(jsonString) -> {
                        mainObject.getString(jsonString)
                    }
                    else -> {
                        ""
                    }
                }
            }
        }
        return validatedData
    }

}