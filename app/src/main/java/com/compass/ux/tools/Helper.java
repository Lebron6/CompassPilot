package com.compass.ux.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.compass.ux.app.ApronApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import dji.common.product.Model;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class Helper {

	public Helper() {

	}



    public static boolean isMultiStreamPlatform() {
	    if (DJISDKManager.getInstance() == null){
	        return false;
        }
        Model model = DJISDKManager.getInstance().getProduct().getModel();
        return model != null && (model == Model.INSPIRE_2
                || model == Model.MATRICE_200
                || model == Model.MATRICE_210
                || model == Model.MATRICE_210_RTK
                || model == Model.MATRICE_200_V2
                || model == Model.MATRICE_210_V2
                || model == Model.MATRICE_210_RTK_V2
                || model == Model.MATRICE_300_RTK
                || model == Model.MATRICE_600
                || model == Model.MATRICE_600_PRO
                || model == Model.A3
                || model == Model.N3);
    }

    public static boolean isM300Product() {
        if (DJISDKManager.getInstance().getProduct() == null) {
            return false;
        }
        Model model = DJISDKManager.getInstance().getProduct().getModel();
        return model == Model.MATRICE_300_RTK;
    }



    //rtk判断
    public static boolean isRtkAvailable() {
        return isFlightControllerAvailable() && isAircraft() && (null != ApronApp.getAircraftInstance()
                .getFlightController()
                .getRTK());
    }


    public static boolean isCameraModuleAvailable() {
        return isProductModuleAvailable() && (null != ApronApp.getProductInstance().getCamera());
    }

    public static boolean isFlightControllerAvailable() {
        return isProductModuleAvailable() && isAircraft() && (null != ApronApp.getAircraftInstance()
                .getFlightController());
    }
    public static boolean isProductModuleAvailable() {
        return (null != ApronApp.getProductInstance());
    }
    public static boolean isAircraft() {
        return ApronApp.getProductInstance() instanceof Aircraft;
    }

    public static boolean isAirlinkAvailable() {
        return isProductModuleAvailable() && (null != ApronApp.getProductInstance().getAirLink());
    }
}
