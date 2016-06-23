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
        Parse.initialize(new Parse.Configuration.Builder(this)
                        // 從[Core Settings]的Application ID取得，與Server連線驗證會需要Application ID。
                        .applicationId("76ee57f8e5f8bd628cc9586e93d428d5")
                                // 從[Core Settings]取得Parse API Address
                        .server("http://parseserver-ps662-env.us-east-1.elasticbeanstalk.com/parse/")
                                //Client Key : 驗證密碼
                        .build()
        );
    }
}
