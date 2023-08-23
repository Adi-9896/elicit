package com.example.todaybuddy;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;  //BIN     //
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
//BINDING ALL THE FINCTIONS IN THIS CLASSS

public class Notesrepositor {
    private  Notesdao notesdao;
    private LiveData<List<notes>> noteslist;


    public Notesrepositor(Application applicationp) {
        Notesdatabse notesdatabse =  Notesdatabse.getInstance(applicationp);
        notesdao = notesdatabse.notesDao();
         noteslist = notesdao.fetchAll();
    }

    public LiveData<List<notes>> fetchAll(){
        return noteslist;
    }

        public void insert(notes notes){
        Notesdatabse.databaseWriteExecutor.execute(() -> {
            notesdao.insert(notes);
        });

    }

    public  void  update(notes notes){
        Notesdatabse.databaseWriteExecutor.execute(() -> {
            notesdao.update(notes);
        });
    }

    // Method to delete a note (executes in a background thread)
    public void deleteNote(notes note) {
        Notesdatabse.databaseWriteExecutor.execute(() -> {
            notesdao.delete(note);
        });
    }

}
