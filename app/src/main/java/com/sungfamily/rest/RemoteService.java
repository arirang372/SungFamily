package com.sungfamily.rest;

import android.content.Context;
import com.sungfamily.models.HttpCourierResponse;
import com.sungfamily.models.Review;
import com.sungfamily.models.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface RemoteService
{
   String LOCAL_HOST = "http://3.15.215.70:8090/";
   // String LOCAL_HOST = "http://192.168.1.155:8080/";
    String DEPLOY_BASE_URL = LOCAL_HOST + "SungFamilyService/";

    @POST("firebase/v1/auth")
    Observable<retrofit2.Response<HttpCourierResponse>> logInUser(@Body User review);


    @GET("firebase/v1/send/message")
    Observable<retrofit2.Response<HttpCourierResponse>> sendFirebaseMessage(@Query("userId") Integer userId,
                                                                            @Query("message") String message);

    @POST("firebase/v1/write/review")
    Observable<retrofit2.Response<HttpCourierResponse>> writeReview(@Body Review review);

    class Factory
    {
        private static RemoteService service;
        private static Retrofit rt;

        public static RemoteService getInstance(Context context) {
            if (service == null) {
                OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                builder.readTimeout(10, TimeUnit.SECONDS);
                builder.connectTimeout(10, TimeUnit.SECONDS);
                builder.writeTimeout(10, TimeUnit.SECONDS);

                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);

                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .build();
                        return chain.proceed(request);
                    }
                });

                int cacheSize = 10 * 1024 * 1024; // 10 meg
                Cache cache = new Cache(context.getCacheDir(), cacheSize);
                builder.cache(cache);

                rt = new Retrofit.Builder()
                        .client(builder.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(DEPLOY_BASE_URL).build();

                service = rt.create(RemoteService.class);
                return service;
            } else {
                return service;
            }
        }


    }
}
