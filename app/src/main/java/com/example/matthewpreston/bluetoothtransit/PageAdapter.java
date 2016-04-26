package com.example.matthewpreston.bluetoothtransit;



        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                OnlineFragment online = new OnlineFragment();
                return online;
            case 1:
                BluetoothFragment bluetooth = new BluetoothFragment();
                return bluetooth;
            case 2:
                WifiFragment wifi = new WifiFragment();
                return wifi;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
