package com.example.hideit

import com.example.hideit.jsonclasses.AnalyserData
import com.example.hideit.jsonclasses.AnalysizedResponse
import com.example.hideit.jsonclasses.AnonymizeText
import com.example.hideit.jsonclasses.AnonymizedResult
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface WebServices {
    companion object{
        var BASE_URL:String = "https://presidio-analyzer-prod.azurewebsites.net/"
        var retrofit: Retrofit  = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @POST("analyze")
    fun postAnalysized(@Body analyserData: AnalyserData):Call<AnalysizedResponse>

}