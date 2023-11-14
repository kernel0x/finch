package com.kernel.finch.sample

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal object ApiService {
    fun getInstance(client: OkHttpClient): ApiInterface {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://httpbin.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiInterface::class.java)
    }

    internal class Data
    internal interface ApiInterface {
        @DELETE("/delete")
        fun delete(): Call<Void>

        @GET("/get")
        fun get(): Call<Void>

        @POST("/post")
        fun post(@Body body: Data): Call<Void>

        @PATCH("/patch")
        fun patch(@Body body: Data): Call<Void>

        @PUT("/put")
        fun put(@Body body: Data): Call<Void>

        @GET("/stream/{lines}")
        fun stream(@Path("lines") lines: Int): Call<Void>

        @GET("/stream-bytes/{bytes}")
        fun streamBytes(@Path("bytes") bytes: Int): Call<Void>

        @GET("/delay/{seconds}")
        fun delay(@Path("seconds") seconds: Int): Call<Void>

        @GET("/xml")
        fun xml(): Call<Void>

        @GET("/json")
        fun json(): Call<Void>

        @GET("/html")
        fun html(): Call<Void>

        @GET("/basic-auth/{user}/{passwd}")
        fun basicAuth(@Path("user") user: String, @Path("passwd") passwd: String): Call<Void>

        @GET("/status/{code}")
        fun status(@Path("code") code: Int): Call<Void>

        @GET("/redirect-to")
        fun redirectTo(@Query("url") url: String): Call<Void>

        @GET("/image")
        fun image(@Header("Accept") accept: String): Call<Void>

        @GET("/gzip")
        fun gzip(): Call<Void>

        @GET("/cookies/set")
        fun cookieSet(@Query("k1") value: String): Call<Void>

        @GET("/deny")
        fun deny(): Call<Void>

        @GET("/cache/{seconds}")
        fun cache(@Path("seconds") seconds: Int): Call<Void>
    }
}
