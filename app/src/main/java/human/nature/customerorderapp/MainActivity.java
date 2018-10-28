package human.nature.customerorderapp;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import human.nature.customerorderapp.Adapters.CoaPagerAdapter;
import human.nature.customerorderapp.EventBus.Events;
import human.nature.customerorderapp.EventBus.GlobalBus;
import human.nature.customerorderapp.Fragments.EditInfoDialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {
    PagerSlidingTabStrip tabs;
    ViewPager pager;
    Toolbar toolbar;
    DrawerLayout drawerLayout;

    StaticDatas staticDatas = StaticDatas.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.White));
        getSupportActionBar().setTitle(staticDatas.s_name);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                switch(item.getItemId()){
                    case R.id.edit_info:
                        EditInfoDialog editInfoDialog = new EditInfoDialog(MainActivity.this);
                        editInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        editInfoDialog.show();
                        Window window = editInfoDialog.getWindow();
                        window.setLayout(MATCH_PARENT, WRAP_CONTENT);
                        break;
                }
                return false;
            }
        });
        View headerView = navigationView.getHeaderView(0);
        TextView user_name = headerView.findViewById(R.id.s_name);
        user_name.setText("사용자 상호명");

        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);

        CoaPagerAdapter adapter = new CoaPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setShouldExpand(true);
        tabs.setTextSize(45);
        tabs.setViewPager(pager);
//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position == 1){
//                    Events.Msg msg = new Events.Msg(Events.REFRESH_ORDER_LIST);
//                    GlobalBus.getBus().post(msg);
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if(position == 1){
//                    Events.Msg msg = new Events.Msg(Events.REFRESH_ORDER_LIST);
//                    GlobalBus.getBus().post(msg);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1){
                    Events.Msg msg = new Events.Msg(Events.REFRESH_ORDER_LIST);
                    GlobalBus.getBus().post(msg);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    Events.Msg msg = new Events.Msg(Events.REFRESH_ORDER_LIST);
                    GlobalBus.getBus().post(msg);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        AutoLayout.setResizeView(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            finish();
        }
    }
}
