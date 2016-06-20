package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView txv;
    EditText editText;
    RadioGroup radioGroup;
    CheckBox checkBox;
    ListView listView;
    Spinner storeSpinner;

    ArrayList<Order> orders = new ArrayList<>();
    String drinkName = "black tea";
    String menuResults = "";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    static final int REQUEST_CODE_DRINK_MENU_ACTIVITY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txv = (TextView) findViewById(R.id.txv);
        editText = (EditText) findViewById(R.id.editText);
        radioGroup = (RadioGroup) findViewById(R.id.iceRadioGroup);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        listView = (ListView) findViewById(R.id.listView);
        storeSpinner = (Spinner) findViewById(R.id.spinner);

        sharedPreferences = (SharedPreferences) getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editText.setText(sharedPreferences.getString("editText", ""));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = editText.getText().toString();
                editor.putString("editText", text);
                editor.apply();

                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    click(v);
                    return true;
                }
                return false;
            }
        });

        setupOrdersData(); // 移到onCreate去執行就好，避免一直進行I/O呼叫
        setupListView();
        setupSpinner();

        txv.setText(sharedPreferences.getString("txv", ""));
        txv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("txv", s.toString());
                editor.apply();
            }
        });

        storeSpinner.setSelection(sharedPreferences.getInt("storeSpinner", 0));
        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("storeSpinner", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.d("Debug", "Main Activity onCrearte");
    }

    void setupListView() {
        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);
    }

    private void setupOrdersData(){
        String content = Utils.readFile(this, "history");
        String[] datas = content.split("\n"); // 空行
        for (int i=0; i<datas.length; i++){
            // 將資料放回orders裡面
            Order order = Order.newInstanceWithData(datas[i]);
            if(order != null){
                orders.add(order);
            }
        }
    }

    void setupSpinner() {
        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        storeSpinner.setAdapter(adapter);
    }

    public void click(View view) {
        String note = editText.getText().toString();
        Order order = new Order();
        order.menuResults = menuResults;
        order.note = note;
        order.storeInfo = storeSpinner.getSelectedItem().toString();
        orders.add(order);

        //把order寫進file裡面
        Utils.writeFile(this, "history", order.getJsonObject().toString());
        //每一行都是object , 一行一行存入

        txv.setText(note);
        menuResults = "";
        editText.setText("");

        setupListView();
    }

    public void goToMenu(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, DrinkMenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_DRINK_MENU_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRINK_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                menuResults = data.getStringExtra("results");
                Toast.makeText(this, "完成菜單", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消菜單", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug", "Main Activity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Debug", "Main Activity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Debug", "Main Activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Debug", "Main Activity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "Main Activity onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Debug", "Main Activity onRestart");
    }
}
