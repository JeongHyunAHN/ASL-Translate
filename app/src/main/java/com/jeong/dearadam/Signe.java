package com.jeong.dearadam;

import android.graphics.Bitmap;

public class Signe {

    private Bitmap imageToShow;
    private char name;

    Signe(char name, Bitmap imageToShow){
        this.name = name; // 'a'
        this.imageToShow = imageToShow; // image of alphabet 'a'
    }

    public Bitmap getImageToShow(){
        return this.imageToShow;
    } // called by dispAlphaImg

    public char getName()
    {
        return this.name;
    }
}
