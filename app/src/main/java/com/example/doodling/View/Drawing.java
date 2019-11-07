package com.example.doodling.View;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Drawing implements Serializable {

    private long id;
    private String name;
    private String date;
    private Bitmap bitmap;
    private Drawable drawable;
    private long groupId;
    private long dateTiem;

    public long getDateTiem() {
        return dateTiem;
    }

    public void setDateTiem(long dateTiem) {
        this.dateTiem = dateTiem;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString(){
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String  date) {
        this.date = date;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
