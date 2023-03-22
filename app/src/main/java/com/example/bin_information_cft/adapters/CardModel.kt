package com.example.bin_information_cft.adapters

data class CardModel(
    val length: String,
    val luhn: String,
    val scheme: String,
    val type: String,
    val brand: String,
    val prepaid: String,
    val countryNumeric: String,
    val countryAlpha2: String,
    val countryName: String,
    val countryEmoji: String,
    val countryCurrency: String,
    val countryLatitude: String,
    val countryLongitude: String,
    val bankName: String,
    val bankUrl: String,
    val bankPhone: String,
    val bankCity: String
)
