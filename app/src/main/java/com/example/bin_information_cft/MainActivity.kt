package com.example.bin_information_cft

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bin_information_cft.adapters.CardModel
import com.example.bin_information_cft.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var item: CardModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvTextLatitude.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvTextLongitude.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.etInput.hint = "4571 7360"
        requestCardData("45717360")

        updateInformation()
    }

    private fun updateInformation() {
        binding.etInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    when (binding.etInput.text.toString()) {
                        "" -> requestCardData("45717360")
                        else -> requestCardData(s.toString())
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                bin: CharSequence, start: Int,
                before: Int, count: Int
            ) {
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
        try {
            val mainObject = JSONObject(result)
            item = CardModel(
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

            binding.tvTextCountry.text = getString(
                R.string.tvTextCountryAlpha2NameCurrency,
                item.countryAlpha2,
                item.countryName,
                item.countryCurrency
            )
            binding.tvTextBank.text = getString(
                R.string.tvTextBankNameCity,
                item.bankName,
                item.bankCity
            )
            binding.tvTextBrand.text = item.brand
            binding.tvTextLength.text = item.length
            binding.tvTextLuhn.text = item.luhn
            binding.tvTextPhone.text = item.bankPhone
            binding.tvTextPrepaid.text = item.prepaid
            binding.tvTextScheme.text = item.scheme
            binding.tvTextType.text = item.type
            binding.tvTextUrl.text = item.bankUrl
            binding.tvTextLatitude.text = item.countryLatitude
            binding.tvTextLongitude.text = item.countryLongitude
        } catch (ex: Exception) {
            val toast =
                Toast.makeText(applicationContext, "Произошла ошибка!", Toast.LENGTH_SHORT)
            toast.show()
        }
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
        } catch (ex: Exception) {
            validatedData = ""
            val toast = Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_SHORT)
            toast.show()
        }

        when (validatedData) {
            "true" -> validatedData = "Yes"
            "false" -> validatedData = "No"
            "visa" -> validatedData = "Visa"
            "mastercard" -> validatedData = "Mastercard"
            "debit" -> validatedData = "Debit"
            "credit" -> validatedData = "Credit"
        }

        return validatedData
    }

    fun goToMap (view: View) {
        val sLatitude = item.countryLatitude
        val sLongitude = item.countryLongitude
        val url = "geo:$sLatitude,$sLongitude?z=22&q=$sLatitude,$sLongitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}