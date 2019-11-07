package com.example.doodling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;

import com.example.doodling.View.Drawing;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    public static class PictureColumns implements BaseColumns {
        public static final String PICTURE = "picture";
    }
    private static final int VERSION = 1;//数据库版本号
    private static final String TABLE_NAME="picture";
    private static final String DATABASE_NAME = "picture.db";//数据库名称
    public DataBase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE DRAWING"+"("+
                "ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "NAME VARCHAR(256) NOT NULL,"+
                "TIME VARCHAR(256) NOT NULL,"+
                "group_id INTEGER NOT NULL,"+
                "create_time INTEGER NOT NULL,"+
                "ext_clume_one VARCHAR(256),"+
                "ext_clume_two VARCHAR(256),"+
                "ext_clume_three INTEGER,"+
                "ext_clume_four INTEGER,"+
                PictureColumns.PICTURE + " blob not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //添加图片
    public void addDrawing(Drawing drawing, byte[] picture){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("NAME", drawing.getName());
        content.put("TIME",drawing.getDate());
        content.put("group_id", drawing.getGroupId());
        content.put("create_time",drawing.getDateTiem());
        content.put(PictureColumns.PICTURE,picture);
        db.insert("DRAWING",null,content);
        db.close();
    }

    //获取所有图片信息
    public ArrayList<Drawing> getDrawing(){
        ArrayList<Drawing> Drawings=new ArrayList<>();
        SQLiteDatabase database=getWritableDatabase();
        String query = "SELECT * FROM DRAWING";
        Cursor cursor=database.rawQuery(query,null);
        //Cursor cursor = database.query("picture", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Drawing drawing=new Drawing();
                drawing.setId(cursor.getInt(0));
                drawing.setName(cursor.getString(1));
                drawing.setDate(cursor.getString(2));
                //add 2019 1030 yhm start
                drawing.setGroupId(cursor.getLong(3));
                drawing.setDateTiem(cursor.getLong(4));
                //add 2019 1030 yhm end
                Drawings.add(drawing);
            }while (cursor.moveToNext());{
            }
        }

        database.close();
        return Drawings;
    }

    //获取所有图片信息
    public ArrayList<Drawing> getSearchDrawing(String searchParam){
        ArrayList<Drawing> Drawings=new ArrayList<>();
        SQLiteDatabase database=getWritableDatabase();
        //String query = "SELECT * FROM DRAWING WHERE name like '%"+ searchParam+"%' or time like '%"+searchParam+"%'";
        String query = "SELECT * FROM DRAWING WHERE name like '%"+ searchParam+"%'";
        Cursor cursor=database.rawQuery(query,null);
        //Cursor cursor = database.query("picture", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Drawing drawing=new Drawing();
                drawing.setId(cursor.getInt(0));
                drawing.setName(cursor.getString(1));
                drawing.setDate(cursor.getString(2));
                //add 2019 1030 yhm start
                drawing.setGroupId(cursor.getLong(3));
                drawing.setDateTiem(cursor.getLong(4));
                //add 2019 1030 yhm end
                Drawings.add(drawing);
            }while (cursor.moveToNext());{
            }
        }

        database.close();
        return Drawings;
    }


    //获取图片
    public void  getMap(Drawing drawing){
        SQLiteDatabase db=getWritableDatabase();
        //String query = "SELECT * FROM DRAWING WHERE NAME ="+drawing.getName();
        String query = "SELECT * FROM DRAWING WHERE ID ="+drawing.getId();
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                byte[] bytes=cursor.getBlob(cursor.getColumnIndexOrThrow(DataBase.PictureColumns.PICTURE));
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                Drawable drawable = bitmapDrawable;
                drawing.setDrawable(drawable);
                drawing.setBitmap(bitmap);
            }while (cursor.moveToNext());
        }
        db.close();
    }

    public void delectPicture(Drawing drawing){
        SQLiteDatabase database=getWritableDatabase();
        String sql="DELECT * FROM DRAWING WHERE ID="+drawing.getId();
        database.execSQL(sql);
        database.close();
    }



}
