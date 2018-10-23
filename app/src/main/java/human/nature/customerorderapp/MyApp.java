package human.nature.customerorderapp;

import android.app.Application;

public class MyApp extends Application {
    AppSharedPreference spf;

    @Override
    public void onCreate() {
        super.onCreate();
        spf = new AppSharedPreference(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        spf.setIsLogin(false);
    }
}
