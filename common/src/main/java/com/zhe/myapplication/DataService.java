package com.zhe.myapplication;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zhe on 16/5/22.
 */
public interface DataService {

    @GET()
    Call<ResponseBody> getData();

    //    @Headers({"Content-Type: application/json", "Accept: application/json"}) // 需要添加头
    @POST("share/changeSharePraise")
    Call<BaseResult<String>> postJsonData(@Query("token") String token, @Query("locale") String locale, @Body RequestBody body);

    @POST("continent/fullContinentList")
    Call<BaseResult<List<City>>> postBodyData(@Query("token") String token, @Query("locale") String locale, @Body RequestBody body);

    @FormUrlEncoded
    @POST("continent/fullContinentList")
    Call<BaseResult<List<City>>> postBodyData2(@Query("token") String token, @Query("locale") String locale, @FieldMap Map<String, String> body);

}
