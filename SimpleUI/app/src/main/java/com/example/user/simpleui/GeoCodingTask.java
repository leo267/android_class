package com.example.user.simpleui;

import android.os.AsyncTask;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

/**
 * Created by user on 2016/6/30.
 * 我們希望他回傳兩個double , 因此使用回傳型態為double[]
 */
public class GeoCodingTask extends AsyncTask<String, Void, double[]> {
    WeakReference<GeoCodingTaskResponse> geoCodingTaskResponseWeakReference;

    @Override
    protected double[] doInBackground(String... params) {
        // get address
        String address = params[0];
        //get latlng
        double[] latlng = Utils.getLatLngFromGoogleMapAPI(address);
        //return
        return latlng;
    }

    @Override
    protected void onPostExecute(double[] doubles) {
        super.onPostExecute(doubles);
        if (geoCodingTaskResponseWeakReference.get() != null) {
            GeoCodingTaskResponse response = geoCodingTaskResponseWeakReference.get();
            //LatLng for GoogleMap API使用的
            response.responseWithGeoCodingResults(new LatLng(doubles[0], doubles[1]));
        }
    }

    public GeoCodingTask(GeoCodingTaskResponse geoCodingTaskResponse) {
        // geoCodingTaskResponse = OrderDetailAcitivty
        this.geoCodingTaskResponseWeakReference = new WeakReference<GeoCodingTaskResponse>(geoCodingTaskResponse);
    }

    //命名一個interface。OrderDetailActivity 去實作介面。
    interface GeoCodingTaskResponse {
        //回傳座標的results。
        void responseWithGeoCodingResults(LatLng latLng);

    }
}

