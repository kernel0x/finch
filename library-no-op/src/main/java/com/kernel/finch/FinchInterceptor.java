package com.kernel.finch;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public final class FinchInterceptor implements Interceptor {
    public enum Period {
        ONE_HOUR,
        ONE_DAY,
        ONE_WEEK,
        FOREVER
    }

    public FinchInterceptor(Context context) {
    }

    public FinchInterceptor setShowNotification(boolean isShow) {
        return this;
    }

    public FinchInterceptor setMaxContentLength(long max) {
        return this;
    }

    public FinchInterceptor setRetentionPeriod(Period period) {
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
}
