package com.compass.ux.ui.view;

import android.app.Activity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.compass.ux.R;
import com.compass.ux.callback.OnTypeSelectedListener;


/**
 * Created by James on 2018/1/16.
 * 从底部选择
 */
public class ButtomSelectWindow extends PopupWindow {

    private PopupWindow popupWindow;
    private Activity context;

    public ButtomSelectWindow(Activity context) {
        this.context = context;
    }

    OnTypeSelectedListener listener;

    public void showView(View viewShowPop, final OnTypeSelectedListener listener) {
        final View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_task_type, null);
        this.listener = listener;
        TextView jt = contentView.findViewById(R.id.tv_jt);
        TextView yj = contentView.findViewById(R.id.tv_yj);
        TextView tf = contentView.findViewById(R.id.tv_tf);
        TextView za = contentView.findViewById(R.id.tv_za);
        jt.setOnClickListener(onClickListener);
        yj.setOnClickListener(onClickListener);
        tf.setOnClickListener(onClickListener);
        za.setOnClickListener(onClickListener);
        contentView.measure(0, 0);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        // 使其聚集
        popupWindow.setFocusable(true);

        popupWindow.setWidth((int) viewShowPop.getWidth());
        contentView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = contentView.findViewById(R.id.layout_all).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_task_pop));
        popupWindow.showAtLocation(viewShowPop, Gravity.BOTTOM, 0, 0);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_yj:
                    listener.select("应急");
                    popupWindow.dismiss();
                    break;
                case R.id.tv_za:
                    listener.select("治安");
                    popupWindow.dismiss();
                    break;
                case R.id.tv_jt:
                    listener.select("交通");
                    popupWindow.dismiss();
                    break;
                case R.id.tv_tf:
                    listener.select("突发");
                    popupWindow.dismiss();
                    break;

            }
        }
    };
}