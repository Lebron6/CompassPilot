package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.viewpager.widget.ViewPager;
import com.compass.ux.R;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityMainBinding;
import com.compass.ux.ui.adapter.MainPageAdapter;


/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    private MainPageAdapter pageAdapter;
    private ActivityMainBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    public void initView() {
        pageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mBinding.vpContent.setAdapter(pageAdapter);
        mBinding.vpContent.setOffscreenPageLimit(2);
        mBinding.vpContent.setOnPageChangeListener(onPagerChangerListener);
        mBinding.rgMain.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.rbHome.setChecked(true);
    }

    ViewPager.OnPageChangeListener onPagerChangerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mBinding.rbHome.setChecked(true);
                    break;
//                case 1:
//                    mBinding.rbEquipment.setChecked(true);
//                    break;
                case 1:
                    mBinding.rbFlightHistory.setChecked(true);
                    break;
                case 2:
                    mBinding.rbPersonal.setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            setTabSelection(checkedId);
        }
    };

    private void setTabSelection(int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                mBinding.vpContent.setCurrentItem(0, false);
                break;
//            case R.id.rb_equipment:
//                mBinding.vpContent.setCurrentItem(1, false);
//                break;
            case R.id.rb_flight_history:
                mBinding.vpContent.setCurrentItem(1, false);
                break;
            case R.id.rb_personal:
                mBinding.vpContent.setCurrentItem(2, false);
                break;

        }
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}