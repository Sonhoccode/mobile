package com.example.fastfood.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;

    // Phương thức private để quản lý "nhà bếp" Retrofit
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Lấy API cho các chức năng chính (đồ ăn, user, shop...).
     */
    public static FoodAPI getApi() {
        return getRetrofit().create(FoodAPI.class);
    }

    /**
     * Lấy API dành riêng cho việc đăng nhập/đăng ký.
     */
    public static AuthAPI getAuthApi() {
        return getRetrofit().create(AuthAPI.class);
    }
}