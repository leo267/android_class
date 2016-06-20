package com.example.user.simpleui;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leo on 2016/6/8.
 */
public class Order {
    String menuResults;
    String note;
    String storeInfo;

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("note", note);
            jsonObject.put("menuResults", menuResults);
            jsonObject.put("storeInfo", storeInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Order newInstanceWithData(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            Order order = new Order();
            order.note = jsonObject.getString("note");
            order.storeInfo = jsonObject.getString("storeInfo");
            order.menuResults = jsonObject.getString("menuResults");
            return order;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
