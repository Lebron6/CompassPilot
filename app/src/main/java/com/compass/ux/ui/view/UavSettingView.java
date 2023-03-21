package com.compass.ux.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.compass.ux.R;
import com.compass.ux.ui.fragment.setting.FlightControllerFragment;
import com.qmuiteam.qmui.util.QMUIDirection;
import com.qmuiteam.qmui.util.QMUIViewHelper;

public class UavSettingView extends LinearLayout {
    int visible = View.GONE;//默认显示
    private Context _context;
    private TextView tv_dismiss,tv_title;
    private RadioGroup radioGroup;
    private RadioButton rbfk;
    private RadioButton rbgz;
    private RadioButton rbykq;
    private RadioButton rbgq;
    private RadioButton rbdc;
    private RadioButton rbyt;
    private RadioButton rbsz;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public UavSettingView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        // 加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_uav_setting, this, true);//最后参数必须为true
        // 获取控件
        tv_dismiss = view.findViewById(R.id.tv_dismiss);
        tv_title = view.findViewById(R.id.tv_title);
        radioGroup = view.findViewById(R.id.rg_setting_tab);
        rbdc = view.findViewById(R.id.rb_dc);
        rbfk = view.findViewById(R.id.rb_fk);
        rbgq = view.findViewById(R.id.rb_gq);
        rbgz = view.findViewById(R.id.rb_bz);
        rbsz = view.findViewById(R.id.rb_sz);
        rbykq = view.findViewById(R.id.rb_ykq);
        rbyt = view.findViewById(R.id.rb_yt);
        tv_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toggle();
            }
        });


    }

    public void setContex(AppCompatActivity activity){

        fragmentManager=activity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        rbfk.setChecked(true);

    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_dc:
                    tv_title.setText("智能电池");

                    break;
                case R.id.rb_fk:
                    fragmentTransaction.replace(R.id.frame, new FlightControllerFragment()).commit();
                    tv_title.setText("飞控参数设置");
                    break;
                case R.id.rb_gq:

                    break;
                case R.id.rb_bz:

                    break;
                case R.id.rb_sz:

                    break;
                case R.id.rb_ykq:

                    break;
                case R.id.rb_yt:

                    break;

            }
        }
    };

    public void Toggle() {
        if (visible == View.GONE) {
            visible = View.VISIBLE;
            QMUIViewHelper.slideIn(this, 300, null, true, QMUIDirection.RIGHT_TO_LEFT);
        } else {
            visible = View.GONE;
            QMUIViewHelper.slideOut(this, 300, null, true, QMUIDirection.LEFT_TO_RIGHT);
        }
    }


}
