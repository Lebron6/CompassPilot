package com.compass.ux.ui.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.compass.ux.R;
import com.qmuiteam.qmui.util.QMUIDirection;
import com.qmuiteam.qmui.util.QMUIViewHelper;

public class PullRefreshTaskList extends LinearLayout{
    int visible= View.GONE;//默认显示
    ListView mListView;
    private Context _context;
    public PullRefreshTaskList(final Context context, AttributeSet attrs) {
        super(context, attrs);
        _context=context;
        // 加载布局
        View view= LayoutInflater.from(context).inflate(R.layout.layout_uav_setting,this,true);//最后参数必须为true
        // 获取控件


    }

    public void Toggle(){
        if(visible==View.GONE){
            visible=View.VISIBLE;
            QMUIViewHelper.slideIn(this, 300, null, true, QMUIDirection.RIGHT_TO_LEFT);
        }
        else{
            visible=View.GONE;
            QMUIViewHelper.slideOut(this, 300, null, true, QMUIDirection.LEFT_TO_RIGHT);
        }
    }


}
