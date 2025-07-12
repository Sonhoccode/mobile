package com.example.fastfood.data.api;

import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.FoodModel;
import com.example.fastfood.data.model.PaymentAccount;
import com.example.fastfood.data.model.ShopInfo;
import com.example.fastfood.data.model.SupportRequest;
import com.example.fastfood.data.model.User;
import com.example.fastfood.data.model.ChangePasswordRequest;
import com.example.fastfood.data.model.CreatePaymentRequest;
import com.example.fastfood.data.model.CreatePaymentResponse;
import com.example.fastfood.data.model.Order;
import com.example.fastfood.data.model.OrderRequest;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FoodAPI {

    // --- Món ăn ---
    @GET("/foods")
    Call<List<FoodModel>> getFoods();

    // --- Người dùng ---
    @GET("api/users/{id}")
    Call<User> getUser(@Path("id") String userId);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);

    // --- Cửa hàng & Hỗ trợ ---
    @GET("/api/shops")
    Call<ShopInfo> getShopInfo();

    @POST("/api/support-request")
    Call<ResponseBody> sendSupportRequest(@Body SupportRequest request);

    // --- Tài khoản thanh toán ---
    @GET("/api/payment-account/{userPhone}")
    Call<List<PaymentAccount>> getPaymentAccounts(@Path("userPhone") String userPhone);

    @POST("/api/payment-account/add")
    Call<ResponseBody> addPaymentAccount(@Body PaymentAccount paymentAccount);

    @DELETE("/api/payment-account/delete/{id}")
    Call<Void> deletePaymentAccount(@Path("id") String id);

    @POST("api/users/change-password")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);
    @POST("/api/payment/create_payment_url")
    Call<CreatePaymentResponse> createVnPayPayment(@Body CreatePaymentRequest request);

    @GET("orders/history/{userId}")
    Call<List<Order>> getOrderHistory(@Path("userId") String userId);

    @POST("orders") // Endpoint để tạo đơn hàng mới
    Call<ApiResponse> createOrder(@Body OrderRequest request);
}
