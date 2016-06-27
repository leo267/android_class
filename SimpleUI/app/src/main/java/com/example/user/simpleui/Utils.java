package com.example.user.simpleui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by user on 2016/6/20.
 */
public class Utils {
    public static void writeFile(Context context, String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer, 0, buffer.length); // 東西輸入到buffer
            fis.close();
            String string = new String(buffer); // buffer轉成string
            return string;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ""; //表示失敗,回傳一個空字串
    }

    //取得json的byte檔
    public static byte[] urlToBytes(String urlString) {
        // Url ex: http://maps.google.com/maps/api/geocode/json?address=台北市大安區羅斯福路四段一號
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            //讀進buffer裡面
            InputStream inputStream = connection.getInputStream();
            //等同於一個buffer
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                // 持續寫進 output array stream
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 回傳精度、緯度，應該是回傳double array
    public static double[] getLatLngFromGoogleMapAPI(String address) {
        try {
            address = URLEncoder.encode(address, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //串進API裡面
        String apiURL = "http://maps.google.com/maps/api/geocode/json?address=" + address;

        //url轉成bytes
        byte[] bytes = Utils.urlToBytes(apiURL);

        //先檢查bytes是否null
        if (bytes == null) return null;

        //先轉成字串
        String result = new String(bytes);

        //轉成JSONObject
        try {
            JSONObject jsonObject = new JSONObject(result);

            //檢查status是否ok
            if (jsonObject.getString("status").equals("OK")) {
                // 拿到location (內含經緯度資訊)
                JSONObject location = jsonObject.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                //回傳經緯度
                return new double[]{lat, lng};
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //取得過程中發生錯誤，回傳null。
        return null;
    }


    public static Bitmap getStaticMap(double[] latlng) {
        //字串整理
        String center = String.valueOf(latlng[0]) + "," + String.valueOf(latlng[1]);
        //串進api
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + center + "&size=640x400&zoom=17";

        byte[] bytes = Utils.urlToBytes(staticMapUrl);
        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
        return null;
    }

}
