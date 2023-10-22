package com.example.assignmentthree;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;



public class CameraAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> cameraTitles;

    public CameraAdapter(Context context, ArrayList<String> cameraTitles){
        super(context, R.layout.list_item, cameraTitles);
        this.context = context;
        this.cameraTitles = cameraTitles;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        // Set the camera title
        TextView textView = convertView.findViewById(R.id.tv_title);
        textView.setText(cameraTitles.get(position));

        // Set the camera icon
        ImageView imageView = convertView.findViewById(R.id.iv_icon);
        imageView.setImageResource(R.drawable.img_mm_camera_teal);

        return convertView;
    }

}
