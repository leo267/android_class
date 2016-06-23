package com.example.user.simpleui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrinkMenuActivity extends AppCompatActivity implements DrinkOrderDialog.OnDrinkOrderListener {

    ListView drinkListView;
    TextView priceTextView;

    List<Drink> drinks = new ArrayList<>();
    ArrayList<DrinkOrder> drinkOrders = new ArrayList<>();

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
                Drink drink = (Drink) parent.getAdapter().getItem(position);
                showDetailDrinkMenu(drink);
            }
        });

        Log.d("Debug", "Drink Menu Activity onCreate");
    }

    private void showDetailDrinkMenu(Drink drink) {
        // 使用getFragmentManager()的話, DrinkOrderDialog的import要改成 android.app.Fragment;
        //使用getSupportFragmentManager , 要使用import android.Suipport.v4.Fragment;
        FragmentManager fragmentManager = getFragmentManager(); //跟Activity拿取getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        DrinkOrder drinkOrder = new DrinkOrder();
        Boolean flag = false;

        for (DrinkOrder order : drinkOrders) {
            if (order.drinkName.equals(drink.getName())) {
                drinkOrder = order;
                flag = true;
                break;
            }
        }

        if (!flag) {
            // flag==false, 表示裡面還是沒有東西
            // 因此將drinkOrder創造出來
            drinkOrder.mPrice = drink.getmPrice();
            drinkOrder.lPrice = drink.getlPrice();
            drinkOrder.drinkName = drink.getName();
        }


        DrinkOrderDialog orderDialog = DrinkOrderDialog.newInstance(drinkOrder);
        orderDialog.show(ft, "DrinkOrderDialog");
//        ft.replace(R.id.root, orderDialog);
//        ft.addToBackStack(null);
//        ft.commit();
    }

    private void updateTotalPrice() {
        int total = 0;
        for (DrinkOrder order : drinkOrders) {
            total += order.mPrice * order.mNumber + order.lPrice * order.lNumber;
        }
        priceTextView.setText(String.valueOf(total));
    }

    private void setData() {
        // 透過網路取得drinks
        Drink.getQuery().findInBackground(new FindCallback<Drink>() {
            @Override
            public void done(List<Drink> objects, ParseException e) {
                if(e == null){
                    drinks = objects;
                    setupListView();
                }
            }
        });

        /*for (int i = 0; i < imageId.length; i++) {
            Drink drink = new Drink();
            drink.name = names[i];
            drink.mPrice = mPrices[i];
            drink.lPrice = lPrices[i];
            drink.imageId = imageId[i];
            drinks.add(drink);
        }*/
    }

    public void setupListView() {
        DrinkAdapter adapter = new DrinkAdapter(this, drinks);
        drinkListView.setAdapter(adapter);
    }

    public void done(View view) {
        Intent intent = new Intent();
        JSONArray array = new JSONArray();
        for (DrinkOrder drink : drinkOrders) {
            JSONObject object = drink.getJsonObject();
            array.put(object);
        }
        // array.toString() 轉成字串
        intent.putExtra("results", array.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
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

    @Override
    public void OnDrinkOrderFinished(DrinkOrder drinkOrder) {
        // 檢查是否有重複訂單
        for (int i = 0; i < drinkOrders.size(); i++) {
            if (drinkOrders.get(i).drinkName.equals(drinkOrder.drinkName)) {
                // 才不會有重複的訂單
                drinkOrders.set(i, drinkOrder);
                updateTotalPrice();
                return;
            }
        }

        drinkOrders.add(drinkOrder);
        updateTotalPrice();
    }
}
