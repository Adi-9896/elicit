package com.example.todaybuddy;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Application;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;


import com.example.todaybuddy.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private  Noteviewmodel noteviewmodel;
    private  List<notes>  itemlsit = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        noteviewmodel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance((Application) this.getApplicationContext())).get(Noteviewmodel.class);
        binding.searchView.clearFocus();
        //Searching logic
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterlist(newText);
                return false;
            }
        });

        //To get focus on search view when its clicked
       binding.searchView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                   binding.searchView.setIconified(false); // Open the SearchView when clicked
                   return true;
               }
               return false;
           }
       });

       //Insert button
       binding.floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,Datainsert.class);
            intent.putExtra("type","ADD");
            startActivityForResult(intent,1);
       });

       //Staggerd grid layout
        binding.RV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RV.setHasFixedSize(true);
        RVadapter adapter = new RVadapter(noteviewmodel);
        binding.RV.setAdapter(adapter);
        itemlsit = new ArrayList<>();
        noteviewmodel.fetchAll().observe(this, new Observer<List<notes>>() {
            @Override
            public void onChanged(List<notes> notes) {
                adapter.submitList(notes);
                binding.RV.scrollToPosition(0);
                itemlsit =notes;
            }
        });
        
        //Swipe features
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                      int position = viewHolder.getAdapterPosition();//gets position of adapter
                      notes notes =  adapter.getnote(position);//gets position of note
                     if (direction == ItemTouchHelper.LEFT){
                         noteviewmodel.delete(notes);
                         Snackbar snackbar = Snackbar.make(binding.getRoot(), notes.getTitle()+" deleted",Snackbar.LENGTH_LONG);
                         snackbar.setAction("Undo", new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 noteviewmodel.insert(notes);

                             }
                         });
                         snackbar.show();
                     } else  {
                         Intent intent = new Intent(MainActivity.this,Datainsert.class);
                         intent.putExtra("type","update");
                         intent.putExtra("noteid",notes.getId());
                         intent.putExtra("notetext",notes.getTitle());
                         intent.putExtra("notedisplay",notes.getDisplayText());
                         startActivityForResult(intent,2);
                        // finish();
                     }
            }
        }).attachToRecyclerView(binding.RV);


    }
    //searching
    private void filterlist(String newText) {
        List<notes> filterlist = new ArrayList<notes>();
        String query = newText.toLowerCase(); // Convert query to lowercase
        for(notes note : itemlsit){
            String title = note.getTitle().toLowerCase(); // Convert title to lowercase
            String display = note.getDisplayText().toLowerCase();

            if (title.contains(query)||display.contains(query)){
                filterlist.add(note);
            }
        }
        RVadapter adapter = (RVadapter) binding.RV.getAdapter(); // Get the existing adapter
        adapter.submitList(filterlist); // Update the adapter with filtered data
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1&&resultCode==RESULT_OK&&data!=null) {
                String title = data.getStringExtra("title");
                String display = data.getStringExtra("display");
                notes notes = new notes(title, display);
                noteviewmodel.insert(notes);
            } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
                String title = data.getStringExtra("notetext");
                String display = data.getStringExtra("notedisplay");
                notes notes = new notes(title, display);
                notes.setId(data.getIntExtra("noteid", 0));
                noteviewmodel.update(notes);
            }

            else if (resultCode == RESULT_CANCELED) {
                finish();
            }
    }
}
