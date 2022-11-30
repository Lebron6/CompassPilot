package com.compass.ux.api;


import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.entity.UpdataPasswordResult;
import com.compass.ux.entity.UserInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by James on 2022/6/21.
 */

public interface UavApi {
    /**
     * 登录
     */
    @Headers("Content-Type:application/json")
    @POST("login")
    Call<LoginResult> userLogin(@Body LoginValues loginValues);

    /**
     * 登录2
     */
    @Headers("Content-Type:application/json")
    @POST("/login")
    Call<LoginResult> userLogin2(@Body LoginValues loginValues);

    /**
     * 修改密码
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/system/password")
    Call<UpdataPasswordResult> updataPassword(@Header("authorization") String token,
                                              @Query("oldPassword") String oldPassword, @Query("newPassword") String newPassword);

    /**
     * 获取用户信息
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/system/user-info")
    Call<UserInfo> getUserInfo(@Header("authorization") String token);


    /**
     * 飞行历史
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/data-statistics/flight-history")
    Call<UserInfo> flightHistory(@Header("authorization") String token,
                                 @Query("startTime") String startTime,
                                 @Query("endTime") String endTime,
                                 @Query("pageSize") String pageSize,
                                 @Query("pageNum") String pageNum);


}