package com.example.user.simpleui;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Leo on 2016/6/8.
 */
@ParseClassName("Order")
public class Order extends ParseObject {

    //跟parse sv說有哪些columns
    public String getNote() { return getString("note"); }
    public void setNote(String note) { put("note", note); }

    public String getMenuResults() {
        String menuResults = getString("menuResults");
        if(menuResults == null) menuResults = "";
        return menuResults;
    }
    public void setMenuResults(String menuResults) { put("menuResults", menuResults); }

    public String getStoreInfo() { return getString("storeInfo"); }
    public void setStoreInfo(String storeInfo) { put("storeInfo", storeInfo); }

    // 只會拿到Order的資料
    public static ParseQuery<Order> getQuery(){ return ParseQuery.getQuery(Order.class); }

    // 代替 findInBackground
    public static void getOrdersFromRemote(final FindCallback<Order> callback)
    {
        getQuery().findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                // 檢查有無exception
                if(e == null) {
                    //全部存在local端 , 使用 pinAllInBackground()
                    ParseObject.pinAllInBackground(objects);
                }
                callback.done(objects, e);
            }
        });
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("note", getNote());
            jsonObject.put("menuResults", getMenuResults());
            jsonObject.put("storeInfo", getStoreInfo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Order newInstanceWithData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Order order = new Order();
            order.setNote(jsonObject.getString("note"));
            order.setStoreInfo(jsonObject.getString("storeInfo"));
            order.setMenuResults(jsonObject.getString("menuResults"));
            return order;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
