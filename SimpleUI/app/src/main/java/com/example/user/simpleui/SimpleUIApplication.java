package com.example.user.simpleui;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by user on 2016/6/23.
 */
public class SimpleUIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Order.class);
        ParseObject.registerSubclass(Drink.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                        // 從[Core Settings]的Application ID取得，與Server連線驗證會需要Application ID。
//                        .applicationId("76ee57f8e5f8bd628cc9586e93d428d5")

                        // 從[Core Settings]取得Parse API Address
//                        .server("http://parseserver-ps662-env.us-east-1.elasticbeanstalk.com/parse/")
                        .applicationId("ccsC7pi62OFUhqLs3Q0bMumcSs2Ap6718dtLkCe6")
                        .server("https://parseapi.back4app.com/")
                        .clientKey("kgmQgGzWireFA7vgfLQWf1Fs0jAu9OCE3MFRJP0z")
                                //Client Key : 驗證密碼
                                // 要在本機打開localDataBase , enableLoaclDataStore();
                        .enableLocalDataStore()
                        .build()
        );
    }
}
