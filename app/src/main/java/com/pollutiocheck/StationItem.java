package com.pollutiocheck;

import android.graphics.Color;

public class StationItem {
    private int mImageResource;
    private String mText1;
    private String mText2;
    private int mAppId;
    private String mPollutionValues;


    public StationItem(int ImageResource, String text1, String text2, int appId, String pollutionValues) {
        mImageResource = ImageResource;
        mText1 = text1;
        mText2 = text2;
        getColorCode(mText2);
        mAppId = appId;
        mPollutionValues = pollutionValues;

    }

    public String getPollutionValues(){ return mPollutionValues; }

    public int getAppId() { return mAppId; }

    public int getImageResource() {
        return mImageResource;
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    //A method that will return color code based on the pollution index
    public int getColorCode(String mText2) {
        if (mText2.contains("Air pollution index: Bardzo dobry")) {
            return Color.parseColor("#57b108");
        } else if (mText2.contains("Air pollution index: Dobry")) {
            return Color.parseColor("#b0dd10");
        } else if (mText2.contains("Air pollution index: Umiarkowany")) {
            return Color.parseColor("#ffd911");
        } else if (mText2.contains("Air pollution index: Dostateczny")) {
            return Color.parseColor("#e58100");
        } else if (mText2.contains("Air pollution index: Zły")) {
            return Color.parseColor("#e50000");
        } else if (mText2.contains("Air pollution index: Bardzo zły")) {
            return Color.parseColor("#990000");
        } else {
            return Color.WHITE;
        }

    }

}

