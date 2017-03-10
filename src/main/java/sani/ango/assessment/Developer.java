package sani.ango.assessment;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by ango on 07-Mar-17.
 */
public class Developer implements Serializable{
    private String username;
    private String imgURL;
    private Bitmap imgBitmap;

    public Developer(String name, String url){
        this(name, url, null);
    }

    public Developer(String name, String url, Bitmap bitmap){
        username = name;
        imgURL = url;
        imgBitmap = bitmap;
    }

    public String getUsername(){
        return username;
    }

    public String getImgURL(){
        return imgURL;
    }
}
