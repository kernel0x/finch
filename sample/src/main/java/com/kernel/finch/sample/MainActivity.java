package com.kernel.finch.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.kernel.finch.FinchUtil;
import com.kernel.finch.FinchInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Callback<Void> cb = new Callback<Void>() {
        @Override
        public void onResponse(Call call, Response response) {
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonFinch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartFinch();
            }
        });

        findViewById(R.id.buttonHttpRequests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartRequests();
            }
        });
    }

    private void onStartFinch() {
        startActivity(FinchUtil.getLaunchIntent(this));
    }

    private void onStartRequests() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new FinchInterceptor(getApplication()))
                .build();

        ApiService.ApiInterface api = ApiService.getInstance(okHttpClient);

        api.get().enqueue(cb);
        api.post(new ApiService.Data("ololo")).enqueue(cb);
        api.patch(new ApiService.Data("ololo")).enqueue(cb);
        api.put(new ApiService.Data("ololo")).enqueue(cb);
        api.delete().enqueue(cb);
        api.delay(4).enqueue(cb);
        api.delay(12).enqueue(cb);
        api.redirectTo("https://google.com").enqueue(cb);
        api.gzip().enqueue(cb);
        api.html().enqueue(cb);
        api.xml().enqueue(cb);
        api.json().enqueue(cb);
        api.stream(500).enqueue(cb);
        api.streamBytes(2048).enqueue(cb);
        api.image("image/png").enqueue(cb);
        api.cookieSet("cookie").enqueue(cb);
        api.basicAuth("user", "passwd").enqueue(cb);
        api.deny().enqueue(cb);
        api.status(205).enqueue(cb);
        api.status(403).enqueue(cb);
        api.status(500).enqueue(cb);
        api.status(999).enqueue(cb);
        api.cache(25).enqueue(cb);
    }
}