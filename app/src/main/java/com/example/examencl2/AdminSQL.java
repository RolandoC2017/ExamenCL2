package com.example.examencl2;

import android.content.Context;
import android.database.sqlite.*;

public class AdminSQL extends SQLiteOpenHelper {

    public AdminSQL(Context context, String name,SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table docente(codigo int primary key,nombre text, apellido text, sexo text,fecha text,dni text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
