package com.example.user.simpleui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Leo on 2016/6/9.
 */
public class OrderAdapter extends BaseAdapter {

    ArrayList<Order> orders;
    LayoutInflater inflater;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.orders = orders;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listivew_order_item, null);
            TextView drinkName = (TextView) convertView.findViewById(R.id.drinkNameTextView);
            TextView note = (TextView) convertView.findViewById(R.id.noteTextView);
            TextView storeInfo = (TextView) convertView.findViewById(R.id.storeInfoTextView);

            holder = new Holder();
            holder.drinkName = drinkName;
            holder.note = note;
            holder.storeInfo = storeInfo;

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Order order = orders.get(position);
        holder.drinkName.setText(order.drinkName);
        holder.note.setText(order.note);
        holder.storeInfo.setText(order.storeInfo);
        
        return convertView;
    }

    class Holder {
        TextView drinkName;
        TextView note;
        TextView storeInfo;
    }
}
