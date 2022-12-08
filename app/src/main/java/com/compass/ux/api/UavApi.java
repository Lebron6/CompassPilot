package com.compass.ux.api;


import com.compass.ux.entity.AirName;
import com.compass.ux.entity.CallBackResult;
import com.compass.ux.entity.EquipmentDetailsData;
import com.compass.ux.entity.EquipmentResult;
import com.compass.ux.entity.FlightHistoryList;
import com.compass.ux.entity.FlightHistoryDetails;
import com.compass.ux.entity.FlightPoints;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginSimpleResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.entity.MessageResult;
import com.compass.ux.entity.ReplyValues;
import com.compass.ux.entity.TaskReportResult;
import com.compass.ux.entity.TaskReportValues;
import com.compass.ux.entity.UpdataPasswordResult;
import com.compass.ux.entity.UserInfo;

import retrofit2.Call;
import retrofit2.http.Body;
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
    Call<LoginSimpleResult> userLogin2(@Body LoginValues loginValues);

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
     * 通过SN获取设备名称
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/uav-data/uavName")
    Call<AirName> getName(@Header("authorization") String token, @Query("uavCode") String uavCode);


    /**
     * 设备管理
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/uav-data/daping/list/uav")
    Call<EquipmentResult> equipmentList(@Header("authorization") String token,
                                        @Query("pageNum") int pageNum,
                                        @Query("pageSize") int pageSize,
                                        @Query("status") String status
                                 );

    /**
     * 设备详情
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/uav-data/info/detail")
    Call<EquipmentDetailsData> equipmentDetail(@Header("authorization") String token,
                                               @Query("uavCode") String uavCode

    );

    /**
     * 飞行历史
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/data-statistics/flight-history")
    Call<FlightHistoryList> flightHistory(@Header("authorization") String token,
                                          @Query("startTime") String startTime,
                                          @Query("endTime") String endTime,
                                          @Query("pageSize") int pageSize,
                                          @Query("pageNum") int pageNum,
                                          @Query("userId") String userId
                                          );


    /**
     * 飞行历史详情
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/data-statistics/flight/detail")
    Call<FlightHistoryDetails> historyDetail(@Header("authorization") String token,
                                             @Query("sortieId") String id

    );

    /**
     * 飞行历史点位
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/data-statistics/flight/points")
    Call<FlightPoints> flightPoints(@Header("authorization") String token,
                                    @Query("sortieId") String id

    );

    /**
     * 任务报备
     */
    @Headers("Content-Type:application/json")
    @POST("/taskReportEntity/api/add")
    Call<TaskReportResult> taskReport(@Header("authorization") String token, @Body TaskReportValues taskReportValues);

    /**
     * 消息
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/annunciateEntity/api/listAnnunciate")
    Call<MessageResult> messageList(@Header("authorization") String token,
                                    @Query("pageNum") int pageNum,
                                    @Query("pageSize") int pageSize

    );

    /**
     * 获取回复信息
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @GET("/replyEntity/api/get")
    Call<CallBackResult> callBackList(@Header("authorization") String token,
                                      @Query("taskid") String taskid
    );


    /**
     * 回复
     */
    @Headers("Content-Type:application/json")
    @POST("/replyEntity/api/add")
    Call<TaskReportResult> reply(@Header("authorization") String token, @Body ReplyValues replyValues);
}