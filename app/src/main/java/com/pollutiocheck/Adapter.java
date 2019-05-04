package com.pollutiocheck;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pollutiocheck.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<StationItem> sl;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public View mColorCode;


        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView1);
            mTextView2 = itemView.findViewById(R.id.textView2);
            mColorCode = itemView.findViewById(R.id.itemBackground);
        }
        
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder evh = new ViewHolder(v);
        return evh;
    }

    public Adapter(ArrayList<StationItem> sList ){
        sl = sList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StationItem currentItem = sl.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
        holder.mColorCode.setBackgroundColor(currentItem.getColorCode(currentItem.getText2()));

    }

    @Override
    public int getItemCount() {
        return sl.size();
    }



}

