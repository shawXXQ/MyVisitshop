package com.example.shaw.myvisitshop.application;

import org.litepal.LitePalApplication;

/**
 * Created by Shaw on 2017/7/21.
 */

public class App extends LitePalApplication {
    private App app;

    public App initApplication() {
        app = new App();
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
