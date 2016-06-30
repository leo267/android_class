package com.example.user.simpleui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class OrderDetailActivity extends AppCompatActivity implements GeoCodingTask.GeoCodingTaskResponse {

    TextView noteTextView;
    TextView menuResultsTextView;
    TextView storeInfoTextView;
    ImageView staticMap;

    MapFragment mapFragment;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // 取出Intent的資料
        Intent intent = getIntent();
        String note = intent.getStringExtra("note");
        String menuResults = intent.getStringExtra("menuResults");
        String storeInfo = intent.getStringExtra("storeInfo");

        noteTextView = (TextView) findViewById(R.id.noteTextView);
        storeInfoTextView = (TextView) findViewById(R.id.storeInfoTextView);
        menuResultsTextView = (TextView) findViewById(R.id.menuResultsTextView);
        staticMap = (ImageView) findViewById(R.id.imageView);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);

        //設定data前，最好先檢查資料是不是null
        if (note != null)
            noteTextView.setText(note);

        if (storeInfo != null)
            storeInfoTextView.setText(storeInfo);

        //先命名一個String , 將句子加上去
        String text = "";
        if (menuResults != null) {
            //資料需要進行轉換
            try {
                JSONArray array = new JSONArray(menuResults);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    //先把杯數轉成String
                    String lNumber = String.valueOf(object.getInt("lNumber"));
                    String mNumber = String.valueOf(object.getInt("mNumber"));
                    text += object.getString("drinkName") + ":大杯" + lNumber + "杯  中杯" + mNumber + "杯\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            menuResultsTextView.setText(text);

            //先取得address
//            String storeInfos = storeInfo.split(",")[1];   //[0]店名、[1]地址
            String[] storeInfos = storeInfo.split(",");
            if (storeInfos != null && storeInfos.length > 1) {
                String address = storeInfos[1];//[0]店名、[1]地址
                Log.d("Debug", address);
                //開另外一隻AsyncTask去做query的動作
                (new GeoCodingTask(this)).execute(address); //塞OrderDetailAcitivty在Constractor
            }

            //跟mapFragment取GoogleMap
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                }
            });
        }
    }

    // 取得 response
    @Override
    public void responseWithGeoCodingResults(LatLng latLng) {
        if (googleMap != null) {
            // 控制Camera (放大/縮小、移動)
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            googleMap.moveCamera(cameraUpdate);
        }
    }
/*
    private static class GeoCodingTask extends AsyncTask<String, Void, Bitmap> {
        // 用WeakReference去指
        WeakReference<ImageView> imageViewWeakReference;

        @Override
        protected Bitmap doInBackground(String... params) {
            //先拿到address
            String address = params[0];
            double[] latlng = Utils.getLatLngFromGoogleMapAPI(address);
            if (latlng != null) {
                //Log.d("Debug", latlng.toString());
                Log.d("Debug", String.valueOf(latlng[0]));
                Log.d("Debug", String.valueOf(latlng[1]));
            }
            return Utils.getStaticMap(latlng);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //如果View還在，就把Bitmap設定上去
            if(imageViewWeakReference.get() != null){
                ImageView imageView = imageViewWeakReference.get();
                imageView.setImageBitmap(bitmap);
            }
        }

        // Construct
        public GeoCodingTask(ImageView imageView){
            // weak reference 指向 imageView
            this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }
    }
    */
}
