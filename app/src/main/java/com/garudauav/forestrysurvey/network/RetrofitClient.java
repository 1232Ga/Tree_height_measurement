package com.garudauav.forestrysurvey.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // private static final String BASE_URL ="https://uat-forestry-service.bluehawk.ai/forestry/";
    private static final String BASE_URL ="https://forestry-service.bluehawk.ai/forestry/";

    private static Retrofit retrofit=null;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(45, TimeUnit.SECONDS)
            .connectTimeout(45, TimeUnit.SECONDS)
            .build();
    public static Retrofit getInstance(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient(okHttpClient.newBuilder()))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
