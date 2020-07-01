package com.example.attendance.ui.main;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.attendance.Fragment31;
import com.example.attendance.Fragment32;
import com.example.attendance.Fragment33;
import com.example.attendance.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    String entryid;
    public SectionsPagerAdapter(Context context, FragmentManager fm, String entryid) {
        super(fm);
        mContext = context;
        this.entryid = entryid;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position)
        {
            case 0:
                fragment = new Fragment31();
                break;
            case 1:
                fragment = new Fragment32(entryid);
                break;
            case 2:
                fragment = new Fragment33(entryid);
                break;
            default:
                Toast.makeText(mContext,"wrong choice",Toast.LENGTH_SHORT).show();
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}