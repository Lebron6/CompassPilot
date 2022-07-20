package com.compass.ux.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;

public class SystemManager extends BaseManager {

    private SystemManager() {
    }

    private static class AppHolder {
        private static final SystemManager INSTANCE = new SystemManager();
    }

    public static SystemManager getInstance() {
        return SystemManager.AppHolder.INSTANCE;
    }

    public void restartApp() {
        Intent intent = ApronApp.getApplication().getPackageManager().getLaunchIntentForPackage(ApronApp.getApplication().getPackageName());
        intent.putExtra("REBOOT", "reboot");
        PendingIntent restartIntent = PendingIntent.getActivity(ApronApp.getApplication(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) ApronApp.getApplication().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
