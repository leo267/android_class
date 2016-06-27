package com.example.user.simpleui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // 取出Intent的資料
        Intent intent = getIntent();
        String note = intent.getStringExtra("note");
        String menuResults = intent.getStringExtra("menuResults");
        String storeInfo = intent.getStringExtra("storeInfo");

        Log.d("Debug", note);
        Log.d("Debug", menuResults);
        Log.d("Debug", storeInfo);

    }
}
