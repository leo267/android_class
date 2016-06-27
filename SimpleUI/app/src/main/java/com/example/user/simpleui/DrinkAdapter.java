package com.example.user.simpleui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/6/13.
 */
public class DrinkAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Drink> drinks;

    public DrinkAdapter(Context context, List<Drink> drinks) {
        this.inflater = LayoutInflater.from(context);
        this.drinks = drinks;
    }

    @Override
    public int getCount() {
        return drinks.size();
    }

    @Override
    public Object getItem(int position) {
        return drinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_drink_item, null);
            holder = new Holder();
            holder.drinkName = (TextView) convertView.findViewById(R.id.noteTextView);
            holder.mPriceTextView = (TextView) convertView.findViewById(R.id.mPriceTextView);
            holder.lPriceTextView = (TextView) convertView.findViewById(R.id.lPriceTextView);
            holder.drinkImageView = (ImageView) convertView.findViewById(R.id.drinkImageView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Drink drink = drinks.get(position);
        holder.drinkName.setText(drink.getName());
        holder.mPriceTextView.setText(String.valueOf(drink.getmPrice()));
        holder.lPriceTextView.setText(String.valueOf(drink.getlPrice()));

        // android 沒有 url 載下資源檔(圖片) , 加入library：compile 'com.squareup.picasso:picasso:2.5.2'
        // into 塞到哪邊裡賣
//        Picasso.with(inflater.getContext()).load(drink.getImage().getUrl()).into(holder.drinkImageView);
        //改使用getFile方式，取得ParseFile圖檔
        drink.getImage().getFileInBackground(new GetFileCallback() {
            @Override
            public void done(File file, ParseException e) {
                Picasso.with(inflater.getContext()).load(file).into(holder.drinkImageView);
            }
        });

//        holder.drinkImageView.setImageResource(drink.getImageId());
        return convertView;
    }

    class Holder {
        TextView drinkName;
        TextView mPriceTextView;
        TextView lPriceTextView;
        ImageView drinkImageView;
    }
}
