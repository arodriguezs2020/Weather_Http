package com.example.weatherhttp

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {
    var BASE_URL : String = "https://openweathermap.org/img/w/"
    val getClient: DataService
        get() {
            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build()

            return retrofit.create(DataService::class.java)
        }

}