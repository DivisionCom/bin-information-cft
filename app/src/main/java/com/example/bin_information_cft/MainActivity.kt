package com.example.bin_information_cft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bin_information_cft.adapters.CardModel
import com.example.bin_information_cft.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.etInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                requestCardData(s.toString())
            }
        })
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
        binding.tvTextCountry.text = item.countryName
    }

    private fun getCardData(
        mainObject: JSONObject,
        jsonObject: String,
        jsonString: String,
        getJSONobj: Boolean
    ): String {
        var validatedData: String
        try {
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
        } catch (ex: java.lang.Exception) {
            validatedData = ""
            val toast = Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT)
            toast.show()
        }

        return validatedData
    }

}