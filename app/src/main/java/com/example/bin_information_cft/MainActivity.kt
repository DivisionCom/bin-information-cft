package com.example.bin_information_cft

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bin_information_cft.adapters.CardModel
import com.example.bin_information_cft.data.DbItem
import com.example.bin_information_cft.data.MainDb
import com.example.bin_information_cft.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var item: CardModel
    private var requests: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvTextLatitude.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvTextLongitude.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        requestCardData(DEFAULT_BIN)

        updateRequestList()
        updateInformation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateRequestList() {
        lifecycleScope.launch {
            requests = MainDb.getDb(this@MainActivity).getDao().requestsList()
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_dropdown_item_1line,
                requests.toList()
            )
            with(binding) {
                etInput.setAdapter(adapter)
                etInput.threshold = 1

                etInput.setOnTouchListener { _, _ ->
                    when {
                        requests.isNotEmpty() -> {
                            // show all suggestions
                            when {
                                etInput.text.toString() != EMPTY_STRING_VALUE -> adapter.filter
                                    .filter(null)
                            }
                            etInput.showDropDown()
                        }
                    }
                    false
                }
            }
        }
    }


    private fun updateInformation() {
        binding.etInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(bin: Editable) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    when (binding.etInput.text.toString()) {
                        EMPTY_STRING_VALUE -> requestCardData(DEFAULT_BIN)
                        else -> requestCardData(bin.toString())
                    }
                }
            }

            override fun beforeTextChanged(
                bin: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                bin: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                saveRequests()
            }
        })
    }

    private fun saveRequests() {
        val text = binding.etInput.text
        val db = MainDb.getDb(this)
        val request = DbItem(
            null,
            text.toString()
        )
        val requestLiveData = MainDb.getDb(this).getDao().getItem(text.toString())

        requestLiveData.observe(this, Observer { requests ->
            when (binding.etInput.length()) {
                8 -> if (requests == null) {
                    Thread {
                        db.getDao().insertItem(request)
                        updateRequestList()
                    }.start()
                }
            }
        })
    }

    private fun requestCardData(bin: String) {
        val url = URL + bin
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parseCardData(result)
            },
            { error ->
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        )
        queue.add(request)
    }

    private fun parseCardData(result: String) {
        try {
            val mainObject = JSONObject(result)
            item = CardModel(
                getCardData(mainObject, JSON_KEY_NUMBER, "length", true),
                getCardData(mainObject, JSON_KEY_NUMBER, "luhn", true),
                getCardData(mainObject, EMPTY_STRING_VALUE, "scheme", false),
                getCardData(mainObject, EMPTY_STRING_VALUE, "type", false),
                getCardData(mainObject, EMPTY_STRING_VALUE, "brand", false),
                getCardData(mainObject, EMPTY_STRING_VALUE, "prepaid", false),
                getCardData(mainObject, JSON_KEY_COUNTRY, "numeric", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "alpha2", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "name", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "emoji", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "currency", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "latitude", true),
                getCardData(mainObject, JSON_KEY_COUNTRY, "longitude", true),
                getCardData(mainObject, JSON_KEY_BANK, "name", true),
                getCardData(mainObject, JSON_KEY_BANK, "url", true),
                getCardData(mainObject, JSON_KEY_BANK, "phone", true),
                getCardData(mainObject, JSON_KEY_BANK, "city", true),
            )

            with(binding) {
                tvTextCountry.text = getString(
                    R.string.tvTextCountryAlpha2NameCurrency,
                    item.countryAlpha2,
                    item.countryName,
                    item.countryCurrency
                )
                tvTextBank.text = getString(
                    R.string.tvTextBankNameCity,
                    item.bankName,
                    item.bankCity
                )
                tvTextBrand.text = item.brand
                tvTextLength.text = item.length
                tvTextLuhn.text = item.luhn
                tvTextPhone.text = item.bankPhone
                tvTextPrepaid.text = item.prepaid
                tvTextScheme.text = item.scheme
                tvTextType.text = item.type
                tvTextUrl.text = item.bankUrl
                tvTextLatitude.text = item.countryLatitude
                tvTextLongitude.text = item.countryLongitude
            }
        } catch (ex: Exception) {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                .show()
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
                                    EMPTY_STRING_VALUE
                                }
                            }
                        }
                        else -> {
                            EMPTY_STRING_VALUE
                        }
                    }
                }
                false -> {
                    validatedData = when {
                        mainObject.has(jsonString) -> {
                            mainObject.getString(jsonString)
                        }
                        else -> {
                            EMPTY_STRING_VALUE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            validatedData = EMPTY_STRING_VALUE
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                .show()
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

    fun goToMap(view: View) {
        val sLatitude = item.countryLatitude
        val sLongitude = item.countryLongitude
        val url = "geo:$sLatitude,$sLongitude?z=22&q=$sLatitude,$sLongitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        private const val DEFAULT_BIN = "45717360"
        private const val JSON_KEY_BANK = "bank"
        private const val JSON_KEY_COUNTRY = "country"
        private const val JSON_KEY_NUMBER = "number"
        private const val EMPTY_STRING_VALUE = ""
        private const val URL = "https://lookup.binlist.net/"
    }
}