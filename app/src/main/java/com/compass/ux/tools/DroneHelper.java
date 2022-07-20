package com.compass.ux.tools;

import android.content.Context;
import android.util.Log;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.product.Model;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

public class DroneHelper {

    private static final int GIMBAL_FORWARD = 0;
    private static final int GIMBAL_DOWN = 1;
    private int gimbalAction;

    public DroneHelper() {
        gimbalAction = GIMBAL_FORWARD;
    }

    public boolean enterVirtualStickMode() {
        final BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product == null) {
            Log.e("DroneHelper", "No product connected");
            return false;
        }
        //disable gesture mode
        if (product.getModel() == Model.Spark) {
            DJISDKManager.getInstance()
                    .getMissionControl()
                    .getActiveTrackOperator()
                    .setGestureModeEnabled(false, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                Log.e("DroneHelper",
                                        "Gesture Mode Disabling failed, " + djiError.getDescription());
                            }
                        }
                    });
        }
        return true;
    }

    public boolean setVerticalModeToVelocity() {
        //Enter virtual stick mode with some default settings
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "setVerticleModeToVelocity failed: Can't fetch FC");
            return false;
        }
        //这里是MSDK的产品经理，经过开发确认，Virtual Stick标准的配置模式应该为如下配置：
        //推荐默认用如下设置，标准美国手的控制方式
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        flightController.setVirtualStickAdvancedModeEnabled(true);

        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    Log.d("DroneHelper", "Virtual Stick Mode successfully set");
                } else {
                    Log.e("DroneHelper", "Virtual Stick Mode set failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public boolean setVerticalModeToAbsoluteHeight() {
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "setVerticalModeToAbsoluteHeight failed: Can't fetch FC");
            return false;
        }
        flightController.setVerticalControlMode(VerticalControlMode.POSITION);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    Log.d("DroneHelper", "Virtual Stick Mode successfully set");
                } else {
                    Log.e("DroneHelper", "Virtual Stick Mode set failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public boolean exitVirtualStickMode() {
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "exitVirtualStickMode failed: Can't fetch FC");
            return false;
        }
        flightController.setVirtualStickModeEnabled(false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    Log.d("DroneHelper", "Exited Virtual Stick Mode successfully");
                } else {
                    Log.e("DroneHelper", "Exit Virtual Stick Mode failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public boolean sendMovementCommand(FlightControlData flightControlData) {
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "sendMovementCommand failed: Can't fetch FC");
            return false;
        }
        flightController.sendVirtualStickFlightControlData(flightControlData, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    Log.e("DroneHelper", "sendMovementCommand failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public boolean takeoff() {
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "takeoff failed: Can't fetch FC");
            return false;
        }
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    Log.e("DroneHelper", "takeoff failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public void login(Context context) {
        UserAccountManager.getInstance().logIntoDJIUserAccount(context, new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
            @Override
            public void onSuccess(final UserAccountState userAccountState) {
                Log.e("login", "Login Success");

            }

            @Override
            public void onFailure(DJIError error) {
                Log.e("Login Error:"
                        , error.getDescription());
            }
        });
    }

    public void cancelLand(CommonCallbacks.CompletionCallback callback){
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "cancelLand failed: Can't fetch FC");
        }
        flightController.cancelGoHome(callback);
    }

    public boolean land(CommonCallbacks.CompletionCallback callback) {
        FlightController flightController = fetchFlightController();
        if (flightController == null) {
            Log.e("DroneHelper", "land failed: Can't fetch FC");
            return false;
        }
        flightController.startLanding(callback);
        return true;
    }

    public boolean moveVxVyYawrateVz(float forward, float right, float yawRate, float up) {
        //right=往左往右 forward=前后 yawRate=自旋  up=上升下降
        return sendMovementCommand(new FlightControlData(right, forward, yawRate, up));
    }

    public boolean moveVxVyYawrateHeight(float mPitch, float mRoll, float mYaw, float mThrottle) {
        return sendMovementCommand(new FlightControlData(mPitch, mRoll, mYaw, mThrottle));
    }

    public boolean setGimbalPitchDegree(float pitchAngleDegree) {
        Gimbal gimbal = fetchGimbal();
        if (gimbal == null) {
            Log.e("DroneHelper", "setGimbalPitchDegree failed: Can't fetch gimbal");
            return false;
        }
        Rotation rotation = new Rotation.Builder().pitch(pitchAngleDegree)
                .mode(RotationMode.ABSOLUTE_ANGLE)
                .build();
        gimbal.rotate(rotation, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    Log.e("DroneHelper", "setGimbalPitchDegree failed: " + djiError.getDescription());
                }
            }
        });
        return true;
    }

    public void toggleGimbal() {
        if (gimbalAction == GIMBAL_FORWARD) {
            if (!setGimbalPitchDegree(0.0f)) {
                Log.e("DroneHelper", "Gimbal Action failed");
            }
            gimbalAction = GIMBAL_DOWN;
        } else {
            if (!setGimbalPitchDegree(-65.0f)) {
                Log.e("DroneHelper", "Gimbal Action failed");
            }
            gimbalAction = GIMBAL_FORWARD;
        }
    }

    //Get drone components
    public Camera fetchCamera() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product == null) {
            return null;
        }
        return product.getCamera();
    }

    public FlightController fetchFlightController() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product == null) {
            return null;
        }
        if (product instanceof Aircraft) {
            return ((Aircraft) product).getFlightController();
        }
        return null;
    }

    public Gimbal fetchGimbal() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product == null) {
            return null;
        }
        return product.getGimbal();
    }
}
