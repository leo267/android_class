package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView txv;
    EditText editText;
    RadioGroup radioGroup;
    CheckBox checkBox;
    ListView listView;
    Spinner storeSpinner;

    List<Order> orders = new ArrayList<>();
    String drinkName = "black tea";
    String menuResults = "";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    static final int REQUEST_CODE_DRINK_MENU_ACTIVITY = 0;
    static final int REQEUST_CODE_LOGIN_ACTIVITY = 1;

    // Facebook使用的變數
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        //testObject.saveInBackground();  //另外開一個Thread，在背景執行上傳動作。

        // 下載資料，可能會是一個List或是Object
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                // 先確認是否有錯誤(exception)
                if (e == null) {
                    for (ParseObject object : objects) {
                        Toast.makeText(MainActivity.this, object.getString("foo"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("debug", "Exception : " + e.toString());
                }
            }
        });


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
//        setupFaceBook();

        // User 沒有登入的情況下，開啟LoginActivity
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQEUST_CODE_LOGIN_ACTIVITY);
        }

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 先把訂單抓出來
                Order order = (Order) parent.getAdapter().getItem(position);
                //把Order裡面的東西，給到另一個Activity
                goToDetailOrder(order);
            }
        });
        Log.d("Debug", "Main Activity onCrearte");
    }

    private void goToDetailOrder(Order order) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OrderDetailActivity.class);
        //資料放進 intent
        intent.putExtra("note", order.getNote());
        intent.putExtra("menuResults", order.getMenuResults());
        intent.putExtra("storeInfo", order.getStoreInfo());
        startActivity(intent);
    }

    void setupFaceBook() {
        // 設定與Facebook連線
        // loginButton 已經自動設定連線，點下時會跳出登入頁面。
        // 如何與 acivitiy 溝通 ，解: FB已經幫我們完成， callbackManager
        // manifest 須加上「com.facebook.FacebookActivity」
        // 要先建立facebook activity , 按下loginButton
        // 會去執行 「com.facebook.FacebookActivity」這個LoginActivity，再透過 startActivityForResult() 把資料回傳。
        // 因此onActivityResults也要加入 callBackManager.onActivityResults(requestCode, .. , data);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);

        // 設定想取得的權限 :
        // 看文件https://developers.facebook.com/docs/facebook-login/permissions/#adding
        loginButton.setReadPermissions("email", "public_profile");

        // 設定監聽事件
        // 透過 callback function 取得 results
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // 取得 AccesToken 後，就可以向使用者問資料了
                AccessToken accessToken = loginResult.getAccessToken();
                Log.d("Debug", accessToken.getPermissions().toString());
                // 到期期限
                //Log.d("Debug", accessToken.getExpires().toString());

                // 使用Graph API 與 Facebook 詢問資料
                // 單純拿使用者的資料
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Debug", object.toString());
                        try {
                            txv.setText(object.getString("name"));

                            // has() 確認有沒有傳給我email , 有才往下執行
                            if (object.has("email")) {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // 設定request要哪些東西
                // 設定要哪些欄位 , ex: email, id, name
                Bundle bundle = new Bundle();
                bundle.putString("fields", "email, id, name");
                request.setParameters(bundle);
                request.executeAsync(); // 執行 (同asyncTask)
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        // 不是在 login 當下處理 , 則需先取得accessToken,在做request
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            //GraphRequest ...
        }
    }

    void setupListView() {
        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);
    }

    private void setupOrdersData() {
/*        String content = Utils.readFile(this, "history");
        String[] datas = content.split("\n"); // 空行
        for (int i = 0; i < datas.length; i++) {
            // 將資料放回orders裡面
            Order order = Order.newInstanceWithData(datas[i]);
            if (order != null) {
                orders.add(order);
            }
        }*/
        //檢查網路狀況
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        // 實做一個FindCallback
        final FindCallback<Order> callback = new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                // 有錯誤(Exception)就不執行
                if (e == null) {
                    // 將網路上的資料，放進 orders (總訂單)
                    orders = objects;   // 將orders型態ArrayList<Order> 改成 List<Order>，才有辦法接收 objects，同步調整OrderAdapter
                    setupListView();    //更新listview
                }
            }
        };

        if (info != null && info.isConnected()) {
            // 將資料載下來 (增加存進loacl database功能)
            Order.getOrdersFromRemote(new FindCallback<Order>() {
                @Override
                public void done(List<Order> objects, ParseException e) {
                    if (e != null) {
                        // 代表有 exception
                        // 當手機有網路，但可能跑到中途掛掉了，就讓他改用Local Database
                        //  需要經過一段時間，才會從local database拿取資料，因為系統在嘗試取得線上database
                        Order.getQuery().fromLocalDatastore().findInBackground(callback);
                        //Toast 說現在沒有連上網路
                        Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        // 如果沒有錯誤，呼叫一般的
                        callback.done(objects, e);
                    }
                }
            });
        } else {
            // 無網路狀態時
            Order.getQuery().fromLocalDatastore().findInBackground(callback);
        }
    }

    void setupSpinner() {
        String[] data = getResources().getStringArray(R.array.storeInfo);
        final ArrayList array = new ArrayList();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        String str = object.getString("name") + "," + object.getString("address");
                        array.add(str);
                    }
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        storeSpinner.setAdapter(adapter);
    }

    public void click(View view) {
        String note = editText.getText().toString();
        Order order = new Order();
        order.setMenuResults(menuResults);
        order.setNote(note);
        order.setStoreInfo(storeSpinner.getSelectedItem().toString());
        order.pinInBackground();    // 當按下Click，同時將資料存進 Local Database。
        order.saveEventually();
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
//        callbackManager.onActivityResult(requestCode, resultCode, data);    // 讓facebook去處理，回傳的結果。

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
