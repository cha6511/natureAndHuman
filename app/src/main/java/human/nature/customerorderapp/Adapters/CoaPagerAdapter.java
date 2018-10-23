package human.nature.customerorderapp.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import human.nature.customerorderapp.Fragments.ItemListFragment;
import human.nature.customerorderapp.Fragments.NotificationFragment;
import human.nature.customerorderapp.Fragments.OrderListFragment;

public class CoaPagerAdapter extends FragmentStatePagerAdapter {

    ItemListFragment itemListFragment = ItemListFragment.getInstance();
    OrderListFragment orderListFragment = OrderListFragment.getInstance();
    NotificationFragment notificationFragment = NotificationFragment.getInstance();

    public CoaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return itemListFragment;
            case 1:
                return orderListFragment;
            case 2:
                return notificationFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }



    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "상품목록";
            case 1:
                return "주문목록";
            case 2:
                return "공지사항";
        }
        return super.getPageTitle(position);
    }
}
