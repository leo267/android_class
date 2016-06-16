package com.example.user.simpleui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DrinkOrderDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DrinkOrderDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinkOrderDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private DrinkOrder drinkOrder;

    private OnFragmentInteractionListener mListener;

    public DrinkOrderDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinkOrderDialog.
     */
    // TODO: Rename and change types and number of parameters
    // 我們塞入 DrinkOrder , 但因為bundle不吃這種格式，所以DrinkOrder要在加入getJSONObject方法。
    public static DrinkOrderDialog newInstance(DrinkOrder drinkOrder) {
        DrinkOrderDialog fragment = new DrinkOrderDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, drinkOrder.getJsonObject().toString());
        fragment.setArguments(args);
        return fragment;
    }

/*    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // 取得 Bundle 中的資料，並Instance DrinkOrder的變數
            String data = getArguments().getString(ARG_PARAM1);
            drinkOrder = DrinkOrder.newInstanceWithJsonObject(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink_order_dialog, container, false);
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 將 onCreate , onCreateView 註解 , 透過 onCreateDialog 整合處理
        if (getArguments() != null) {
            // 取得 Bundle 中的資料，並Instance DrinkOrder的變數
            String data = getArguments().getString(ARG_PARAM1);
            drinkOrder = DrinkOrder.newInstanceWithJsonObject(data);
        }

        LayoutInflater inflater =  LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.fragment_drink_order_dialog, null);

        //build 基本的 alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(root)
                .setTitle(drinkOrder.drinkName)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        NumberPicker numberPicker1 = (NumberPicker) root.findViewById(R.id.numberPicker);
        numberPicker1.setMaxValue(100);
        numberPicker1.setMinValue(0);
        numberPicker1.setValue(drinkOrder.mNumber);
        NumberPicker numberPicker2 = (NumberPicker) root.findViewById(R.id.numberPicker2);
        numberPicker2.setMaxValue(100);
        numberPicker2.setMinValue(0);
        numberPicker2.setValue(drinkOrder.lNumber);
        return builder.create();

        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        // 實作一個 Interface , 來與 Activity 溝通的介面
        // Interface 是一個至有Method且沒有Variable及實作的物件

        //        void onFragmentInteraction(Uri uri); //刪掉,本練習中不會使用到。
    }
}
