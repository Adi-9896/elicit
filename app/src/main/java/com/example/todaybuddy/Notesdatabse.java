package com.example.todaybuddy;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.jvm.Synchronized;

@Database(entities = {notes.class},version = 1)
public abstract class Notesdatabse extends RoomDatabase {

    public static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(4);
    public static Notesdatabse  instance;
    public abstract Notesdao notesDao();
    public  static synchronized Notesdatabse getInstance(Context context){
        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), Notesdatabse.class,"Notes_databse")
                    .fallbackToDestructiveMigration().build();
        }
        return instance;


    }

}
