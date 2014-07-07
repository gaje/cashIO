package com.gajetastic.cashio.adapter;

import com.gajetastic.cashio.CashInFragment;
import com.gajetastic.cashio.CashOutFragment;
import com.gajetastic.cashio.DashboardFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new CashInFragment();
        case 1:
            return new CashOutFragment();
        case 2:
            return new DashboardFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}