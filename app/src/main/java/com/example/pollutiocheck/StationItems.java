package com.example.pollutiocheck;

public class StationItems {
    private int mImageResource;
    private String mText1;
    private String mText2;

    public StationItems(int ImageResource, String text1, String text2){
        mImageResource = ImageResource;
        mText1 = text1;
        mText2 = text2;
    }

    public int getImageResource(){
        return mImageResource;
    }

    public String getText1(){
        return mText1;
    }

    public String getText2(){
        return mText2;
    }
}
