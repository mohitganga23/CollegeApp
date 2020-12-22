package com.example.android.collegeapp.Activities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android.collegeapp.R;

public class SettingsMenuList extends ArrayAdapter {

    private String[] menuItem;
    private Integer[] menuItemImage;
    private Activity context;

    public SettingsMenuList(@NonNull Activity context, String[] menuItem,Integer[] menuItemImage) {
        super(context, R.layout.settings_list_item,menuItem);
        this.context = context;
        this.menuItem = menuItem;
        this.menuItemImage = menuItemImage;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            row = inflater.inflate(R.layout.settings_list_item,null,true);

        TextView menuText = row.findViewById(R.id.menuItem);
        ImageView menuImage = row.findViewById(R.id.menuImage);

        menuText.setText(menuItem[position]);
        menuImage.setImageResource(menuItemImage[position]);

        return row;
    }
}
