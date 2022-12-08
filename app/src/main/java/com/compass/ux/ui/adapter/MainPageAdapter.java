package com.compass.ux.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.compass.ux.ui.fragment.EquipmentFragment;
import com.compass.ux.ui.fragment.FlightHistoryFragment;
import com.compass.ux.ui.fragment.GalleryFragment;
import com.compass.ux.ui.fragment.HomeFragment;
import com.compass.ux.ui.fragment.PersonalFragment;


public class MainPageAdapter extends FragmentPagerAdapter {

    private HomeFragment homeFragment;
//    private EquipmentFragment equipmentFragment;
    private FlightHistoryFragment flightHistoryFragment;
//    private GalleryFragment galleryFragment;
    private PersonalFragment personalFragment;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            if(homeFragment==null){
                homeFragment = new HomeFragment();
                return homeFragment;
            }else{
                return homeFragment;
            }
        }else if(position==1){
            if(flightHistoryFragment==null){
                flightHistoryFragment = new FlightHistoryFragment();
                return flightHistoryFragment;
            }else{
                return flightHistoryFragment;
            }
        }else if(position==2){
            if(personalFragment==null){
                personalFragment = new PersonalFragment();
                return personalFragment;
            }else{
                return personalFragment;
            }
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
