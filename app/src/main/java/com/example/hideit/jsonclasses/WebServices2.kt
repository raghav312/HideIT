package com.example.hideit.jsonclasses

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServices2 {
    companion object{
        var BASE_URL:String = "https://presidio-anonymizer-prod.azurewebsites.net/"
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @POST("anonymize")
    fun postAnonymizer(@Body anonymizeText:AnonymizeText):Call<AnonymizedResult>
}