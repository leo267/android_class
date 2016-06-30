package com.example.user.simpleui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity implements GeoCodingTask.GeoCodingTaskResponse, GoogleApiClient.ConnectionCallbacks, RoutingListener, GoogleApiClient.OnConnectionFailedListener , LocationListener {

    TextView noteTextView;
    TextView menuResultsTextView;
    TextView storeInfoTextView;
    ImageView staticMap;

    MapFragment mapFragment;
    GoogleMap googleMap;
    //畫路徑需要的變數
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    LatLng storeLocation;
    private ArrayList<Polyline> polylines;   // ALT+ENTER 建立 , ArrayList<Polyline>

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
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//            googleMap.moveCamera(cameraUpdate);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("我的位置"));

            //先記錄location , 確保當可以連線時，就有位置資料畫線
            storeLocation = latLng;

            createGoogleAPIClient();

        }
    }

    private void createGoogleAPIClient() {
        if (googleApiClient == null) {
            //需要連線
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)   //需要實作ConnectionCallback
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API) //取得什麼服務
                    .build();
            googleApiClient.connect();
        }
    }

    private void createLocationRequest() {
        // 手機移動中，必須一直Update
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //locationRequest.setFastestInterval();//若別的APP有使用locationRequest , 就沿用別的人，沒有的話，再用自己
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // 取得當前的位置 (最後一次request的位置)

        //檢查permission是不是granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // 向使用者要求permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            return;
        }

        googleMap.setMyLocationEnabled(true);
        createLocationRequest();

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


        //轉換動作
        LatLng start = new LatLng(25.0186348, 121.5398379); //塞入一個預設的，本班教室


        if (location != null) {
            //起始位置
            start = new LatLng(location.getLatitude(), location.getLongitude());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17));

        //處理 routing
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING) //預設是走路的路線
                .waypoints(start, storeLocation) //起點, 終點
                .withListener(this) //要實作RoutingListener
                .build();
        //執行
        routing.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            //已經create過了，必須讓他重新連線
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            //中斷掉，不要activity不在時，還持續與Google連線。
            googleApiClient.disconnect();
        }

    }

    @Override
    public void onRoutingFailure(RouteException e) {
//        Log.d("Debug", e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        //前置動作，本例子不需處理
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int index) {
        //routes : 回傳各種路線
        //routes.get(index) : 取得最短路徑

        // 處理畫線的動作
        //Polyline多點的線
        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < routes.size(); i++) {

            //In case of more than 5 alternative routes
            //GoogleMap畫線，吃PolylineOptions。
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.RED);
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(routes.get(i).getPoints()); //Points 轉彎的點
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ routes.get(i).getDistanceValue()+": duration - "+ routes.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // 改變相機的位置
        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
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
