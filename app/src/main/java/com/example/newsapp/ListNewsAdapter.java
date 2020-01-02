package com.example.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


class ListNewsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>>  data;

    public ListNewsAdapter(Activity getActivity, ArrayList<HashMap<String, String>> getData) {
        activity = getActivity;
        data = getData;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListNewsModel newModel = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.list_row, parent, false);
            newModel = new ListNewsModel(
                    (TextView) convertView.findViewById(R.id.author),
                    (TextView) convertView.findViewById(R.id.title),
                    (TextView) convertView.findViewById(R.id.details),
                    (TextView) convertView.findViewById(R.id.time),
                    (ImageView) convertView.findViewById(R.id.galleryImage)
            );
            convertView.setTag(newModel);
        } else {
            newModel = (ListNewsModel) convertView.getTag();
        }
        newModel.galleryImage.setId(position);
        newModel.author.setId(position);
        newModel.title.setId(position);
        newModel.details.setId(position);
        newModel.time.setId(position);

        HashMap<String, String> listNewMap = new HashMap<String, String>();
        listNewMap = data.get(position);

        try{
            newModel.author.setText(listNewMap.get("author"));
            newModel.title.setText(listNewMap.get("title"));
            newModel.time.setText(listNewMap.get("publishedAt"));
            newModel.details.setText(listNewMap.get("description"));

            if(listNewMap.get("urlToImage").toString().length() < 5) {

                newModel.galleryImage.setVisibility(View.GONE);

            } else {
                Picasso.get()
                        .load(listNewMap.get("urlToImage"))
                        .resize(400, 400)
                        .centerCrop()
                        .into(newModel.galleryImage);
            }
        }catch(Exception e) {}
        return convertView;
    }
}

