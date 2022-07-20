package com.compass.ux.manager;

import android.util.Log;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;

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

    public void loginAccount() {
        UserAccountManager.getInstance().logIntoDJIUserAccount(ApronApp.getApplication(),
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {

                    }

                    @Override
                    public void onFailure(DJIError error) {

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
