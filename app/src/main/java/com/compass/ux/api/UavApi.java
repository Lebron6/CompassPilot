package com.compass.ux.api;


import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by James on 2022/6/21.
 */

public interface UavApi {
    /**
     * 登录
     *
     * @return
     */
    @Headers("Content-Type:application/json")
    @POST("login")
    Call<LoginResult> userLogin(@Body LoginValues loginValues);


}