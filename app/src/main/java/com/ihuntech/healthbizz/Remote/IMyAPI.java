package com.ihuntech.healthbizz.Remote;

import com.ihuntech.healthbizz.Model.Banner;
import com.ihuntech.healthbizz.Model.Category;
import com.ihuntech.healthbizz.Model.Country;
import com.ihuntech.healthbizz.Model.Product;
import com.ihuntech.healthbizz.Model.State;
import com.ihuntech.healthbizz.Model.UserResponse;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IMyAPI {

    @FormUrlEncoded
    @POST("login.php")
    Call<UserResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<UserResponse> registerUser(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("getBanner.php")
    Observable<List<Banner>> getBanner();

    @GET("getCategory.php")
    Observable<List<Category>> getCategory();

    @GET("getAllItems.php")
    Observable<List<Product>> getAllItems();

    @FormUrlEncoded
    @POST("getItemByCategory.php")
    Observable<List<Product>> getItemByCategory(
            @Field("category_id") String category_id
    );

    @GET("getCountry.php")
    Observable<List<Country>> getCountry();

    @FormUrlEncoded
    @POST("getStates.php")
    Observable<List<State>> getStates(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("storeOrders.php")
    Call<String> storeOrders(
            @Field("user_id") String user_id,
            @Field("cart") JSONObject cart,
            @Field("shipping") String shipping,
            @Field("payment_method") String payment_method,
            @Field("txnid") String txnid,
            @Field("transaction_number") String transaction_number,
            @Field("order_status") String order_status,
            @Field("shipping_info") String shipping_info,
            @Field("billing_info") String billing_info,
            @Field("payment_status") String payment_status,
            @Field("created_at") String created_at,
            @Field("updated_at") String updated_at,
            @Field("state") String state
    );
}
