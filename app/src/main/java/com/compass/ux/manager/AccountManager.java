package com.compass.ux.manager;

import android.content.Context;
import android.util.Log;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.orhanobut.logger.Logger;

import dji.common.error.DJIError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.useraccount.UserAccountManager;

/**
 * 账号
 */
public class AccountManager extends BaseManager {

    private AccountManager() {
    }

    private static class AccountManagerHolder {
        private static final AccountManager INSTANCE = new AccountManager();
    }

    public static AccountManager getInstance() {
        return AccountManager.AccountManagerHolder.INSTANCE;
    }

    public void loginAccount(Context context) {
        UserAccountManager.getInstance().logIntoDJIUserAccount(context,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Logger.e("登录成功: "+userAccountState.name());

                    }

                    @Override
                    public void onFailure(DJIError error) {
                        Logger.e("登录失败: "+error.getDescription());
                    }
                });
    }

    public void loginOut() {
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {

            }
        });

    }
}
