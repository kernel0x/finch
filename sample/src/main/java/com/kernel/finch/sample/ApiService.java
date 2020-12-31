package com.kernel.finch.sample;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

class ApiService {

    static ApiInterface getInstance(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("https://httpbin.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        return retrofit.create(ApiInterface.class);
    }

    static class Data {
        final String string;

        Data(String string) {
            this.string = string;
        }
    }

    interface ApiInterface {
        @DELETE("/delete")
        Call<Void> delete();

        @GET("/get")
        Call<Void> get();

        @POST("/post")
        Call<Void> post(@Body Data body);

        @PATCH("/patch")
        Call<Void> patch(@Body Data body);

        @PUT("/put")
        Call<Void> put(@Body Data body);

        @GET("/stream/{lines}")
        Call<Void> stream(@Path("lines") int lines);

        @GET("/stream-bytes/{bytes}")
        Call<Void> streamBytes(@Path("bytes") int bytes);

        @GET("/delay/{seconds}")
        Call<Void> delay(@Path("seconds") int seconds);

        @GET("/xml")
        Call<Void> xml();

        @GET("/json")
        Call<Void> json();

        @GET("/html")
        Call<Void> html();

        @GET("/basic-auth/{user}/{passwd}")
        Call<Void> basicAuth(@Path("user") String user, @Path("passwd") String passwd);

        @GET("/status/{code}")
        Call<Void> status(@Path("code") int code);

        @GET("/redirect-to")
        Call<Void> redirectTo(@Query("url") String url);

        @GET("/image")
        Call<Void> image(@Header("Accept") String accept);

        @GET("/gzip")
        Call<Void> gzip();

        @GET("/cookies/set")
        Call<Void> cookieSet(@Query("k1") String value);

        @GET("/deny")
        Call<Void> deny();

        @GET("/cache/{seconds}")
        Call<Void> cache(@Path("seconds") int seconds);
    }

}
