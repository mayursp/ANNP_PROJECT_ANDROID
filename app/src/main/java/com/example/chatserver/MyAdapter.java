package com.example.chatserver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String> {

    int[] contimg;
    String[] contname;
    String[] contdesc;
    Context mContext;
    public MyAdapter(@NonNull Context context, int[] imgid,String[] name) {
        super(context, R.layout.mainscreenlayout);
        this.contname = name;
        this.contimg = imgid;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return contname.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder =  new ViewHolder();

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.mainscreenlayout, parent, false);
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.textView_name);
            convertView.setTag(viewHolder);

        }
        else{

            viewHolder = (ViewHolder)convertView.getTag();

        }

        viewHolder.mImage.setImageResource(contimg[position]);
        viewHolder.mName.setText(contname[position]);

        return convertView;

    }

    static class ViewHolder{
        ImageView mImage;
        TextView mName;
    }

}
