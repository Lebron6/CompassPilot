package com.compass.ux.ui.view.datescroller.listener;


import com.compass.ux.ui.view.datescroller.DateScrollerDialog;

/**
 * 日期设置的监听器
 *
 * @author C.L. Wang
 */
public interface OnDateSetListener {
    void onDateSet(DateScrollerDialog timePickerView, long milliseconds, long milliFinishSeconds);
}
