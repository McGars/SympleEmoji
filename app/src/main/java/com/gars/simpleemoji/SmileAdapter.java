package com.gars.simpleemoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Владимир on 16.10.2015.
 */
public class SmileAdapter extends ArrayAdapter<SmileItem> {
    private final LayoutInflater inflater;

    public SmileAdapter(Context context, List<SmileItem> list) {
        super(context,0, list);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        View v = convertView;
        if(convertView == null){
            v = inflater.inflate(R.layout.item, null);
            holder = createHolder(v);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        SmileItem item = getItem(position);
        holder.ivImage.setImageResource(item.icon);

        return v;
    }

    private Holder createHolder(View v) {
        Holder holder = new Holder();
        holder.ivImage = (ImageView) v.findViewById(R.id.ivImage);
        return holder;
    }

    class Holder{
        ImageView ivImage;
    }
}
