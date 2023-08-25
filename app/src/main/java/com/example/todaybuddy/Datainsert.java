package com.example.todaybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.todaybuddy.databinding.ActivityDatainsertBinding;

public class Datainsert extends AppCompatActivity {
    ActivityDatainsertBinding binding;
    boolean isUpdate = false;
    int noteid = -1;
    Noteviewmodel noteviewmodel;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDatainsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        noteviewmodel = new ViewModelProvider(this).get(Noteviewmodel.class); // Initialize noteviewmodel

       // update
        String type =  getIntent().getStringExtra("type");
        if (type.equals("update")) {
            setTitle("Update");
            id = getIntent().getIntExtra("noteid", 0);
            String oldtitle = getIntent().getStringExtra("notetext");
            String olddisplay = getIntent().getStringExtra("notedisplay");
            binding.Title.setText(oldtitle);
            binding.Display.setText(olddisplay);
        }
    }
       @Override
    public void onBackPressed() {
        //update
        String title  = binding.Title.getText().toString().trim();
        String display  = binding.Display.getText().toString().trim();
        boolean isUpdate = getIntent().getStringExtra("type").equals("update");
        if (isUpdate){
            String oldtitle = getIntent().getStringExtra("notetext");
            String olddisplay = getIntent().getStringExtra("notedisplay");

            if (oldtitle != null && olddisplay != null &&(!oldtitle.equals(title)  || !olddisplay.equals(display))){
                Intent i = new Intent();
                i.putExtra("notetext", binding.Title.getText().toString());
                i.putExtra("notedisplay", binding.Display.getText().toString());
                i.putExtra("noteid", id);
                setResult(RESULT_OK, i);//this will send result in main activity
        }
            else {
                setResult(RESULT_CANCELED);
                //Toast.makeText(this, "update cancle", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Datainsert.this,MainActivity.class));

            }

        }else {
            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(display)) {
                Intent i = new Intent();
                i.putExtra("title", binding.Title.getText().toString());
                i.putExtra("display", binding.Display.getText().toString());
                setResult(RESULT_OK, i);//this will send result in main activity
               // Toast.makeText(this, "inserted", Toast.LENGTH_SHORT).show();
            }
            else {
                setResult(RESULT_CANCELED); // Discard empty note
                startActivity(new Intent(Datainsert.this,MainActivity.class));

            }

        }
        super.onBackPressed();
        //finish();
    }


}
