package com.example.attendance;

import android.graphics.Bitmap;

public class student {
    private String nm,no,c,sec;
    private String Imgview;
    public student() {
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getImgview() {
        return Imgview;
    }

    public void setImgview(String imgview) {
        Imgview = imgview;
    }
}
