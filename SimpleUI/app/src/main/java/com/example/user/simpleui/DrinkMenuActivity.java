package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrinkMenuActivity extends AppCompatActivity {

    ListView drinkListView;
    TextView priceTextView;

    ArrayList<Drink> drinks = new ArrayList<>();
    ArrayList<Drink> drinkOrders = new ArrayList<>();

    //SET DATA
    String[] names = {"冬瓜紅茶", "玫瑰鹽奶蓋紅茶", "珍珠紅茶拿鐵", "紅茶拿鐵"};
    int[] mPrices = {25, 35, 45, 35};
    int[] lPrices = {35, 45, 55, 45};
    int[] imageId = {R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
        setData();

        //get UI componet
        drinkListView = (ListView) findViewById(R.id.drinkListView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);

        setupListView();

        drinkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //取出飲料資料
                DrinkAdapter drinkAdapter = (DrinkAdapter) parent.getAdapter();
                //取出所選的飲料
                Drink drink = (Drink) drinkAdapter.getItem(position);
                //放入選擇的飲料清單
                drinkOrders.add(drink);
                updateTotalPrice();
            }
        });

        Log.d("Debug", "Drink Menu Activity onCreate");
    }


    private void updateTotalPrice() {
        int total = 0;
        for(Drink drink : drinkOrders)
        {
            total += drink.mPrice;
        }
        priceTextView.setText(String.valueOf(total));
    }

    private void setData() {
        for (int i = 0; i < imageId.length; i++) {
            Drink drink = new Drink();
            drink.name = names[i];
            drink.mPrice = mPrices[i];
            drink.lPrice = lPrices[i];
            drink.imageId = imageId[i];
            drinks.add(drink);
        }
    }

    private void setupListView() {
        DrinkAdapter adapter = new DrinkAdapter(this, drinks);
        drinkListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug", "Drink Menu Activity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Drink Menu Activity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "Drink Menu Activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Debug", "Drink Menu Activity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "Drink Menu Activity onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Debug", "Drink Menu Activity onRestart");
    }
}
