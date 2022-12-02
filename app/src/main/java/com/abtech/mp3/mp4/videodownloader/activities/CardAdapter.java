package com.abtech.mp3.mp4.videodownloader.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abtech.mp3.mp4.videodownloader.R;

import java.util.ArrayList;

public class CardAdapter extends ArrayAdapter<CardModel> {

    public CardAdapter(@NonNull Context context, ArrayList<CardModel> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.cardlayout_main, parent, false);
        }

        CardModel courseModel = getItem(position);
       // TextView courseTV = listitemView.findViewById(R.id.logo_text);
        ImageView courseIV = listitemView.findViewById(R.id.logo);

       // courseTV.setText(courseModel.getLogo_name());
        courseIV.setImageResource(courseModel.getLogo());
        return listitemView;
    }
}

