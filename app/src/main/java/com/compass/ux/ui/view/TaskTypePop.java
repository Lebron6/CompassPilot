package com.compass.ux.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.compass.ux.R;
import com.compass.ux.callback.OnTimeSelectedListener;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.adapter.GalleryAdapter;
import com.compass.ux.ui.adapter.TaskTypeAdapter;

import java.util.List;

public class TaskTypePop extends PopupWindow {

    private RecyclerView lv_quest;
    private TaskTypeAdapter adapter;
    private PopupWindow popupWindow;
    private Activity context;

    public TaskTypePop(Activity context) {
        this.context = context;
    }

    /**
     * @param adapter
     */
    public void showView(View viewShowPop, TaskTypeAdapter adapter, final OnTimeSelectedListener listener) {
        final View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_task_type, null);
        lv_quest = (RecyclerView) contentView.findViewById(R.id.rv_task_type);
        RecyclerViewHelper.initRecyclerViewV(context, lv_quest, false, adapter);
//        lv_quest.setAdapter(adapter);
//        lv_quest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                listener.select(position);
//                popupWindow.dismiss();
//            }
//        });
        contentView.measure(0, 0);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        // 使其聚集
        popupWindow.setFocusable(true);

        popupWindow.setWidth((int) viewShowPop.getWidth());
        contentView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = contentView.findViewById(R.id.rv_task_type).getTop();
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

}