package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    TextView txv;
    EditText editText;
    RadioGroup radioGroup;
    CheckBox checkBox;
    ListView listView;
    Spinner storeSpinner;

    //    String selectedSex = "Male";
//    String name = "";
//    String sex = "";
    String drinkName = "black tea";
    ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txv = (TextView) findViewById(R.id.txv);
        editText = (EditText) findViewById(R.id.editText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        listView = (ListView) findViewById(R.id.listView);
        storeSpinner = (Spinner) findViewById(R.id.spinner);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    click(v);
                    return true;
                }
                return false;
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.maleRadioButton) {
//                    selectedSex = "Male";
//                } else if (checkedId == R.id.femaleRadioButton) {
//                    selectedSex = "Female";
//                }
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                drinkName = radioButton.getText().toString();
            }
        });

        setupListView();
        setupSpinner();

//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                changeTextView();
//            }
//        });
    }

    void setupListView() {
//        String[] data = new String[] {"123","456","789","Hello", "ListView","Hi"};
//        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,drinks);

//        List<Map<String,String>> data = new ArrayList<>();
//
//        for (int i = 0; i < orders.size(); i++) {
//            Order order = orders.get(i);
//            Map<String, String> item = new HashMap<>();
//            item.put("drinkName", order.drinkName);
//            item.put("note", order.note);
//            data.add(item);
//        }
//
//        String[] from = new String[]{"drinkName", "note"};
//        int[] to = new int[]{R.id.drinkNameTextView, R.id.noteTextView};
//        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listivew_order_item, from, to);
        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);
    }

    void setupSpinner() {
        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data);
        storeSpinner.setAdapter(adapter);
    }

    public void click(View view) {
        String note = editText.getText().toString();

//        sex = selectedSex;

//        changeTextView();

        Order order = new Order();
        order.drinkName = drinkName;
        order.note = note;
        order.storeInfo = storeSpinner.getSelectedItem().toString();
        orders.add(order);
        setupListView();
        txv.setText(drinkName);

        editText.setText("");
    }

//    public void changeTextView()
//    {
//        if(name.equals("")) return;
//
//        if(checkBox.isChecked())
//        {
//            txv.setText(name);
//        }
//        else
//        {
//            String content = name + " sex: " + sex;
//            txv.setText(content);
//        }
//    }
}
