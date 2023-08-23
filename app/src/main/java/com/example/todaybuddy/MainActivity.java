package com.example.todaybuddy;

import static android.provider.SyncStateContract.Helpers.update;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todaybuddy.databinding.ActivityDatainsertBinding;
import com.example.todaybuddy.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private  Noteviewmodel noteviewmodel;
    private  List<notes>  itemlsit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        noteviewmodel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance((Application) this.getApplicationContext())).get(Noteviewmodel.class);
        binding.searchView.setQueryHint("Search your notes...");
       binding.searchView.clearFocus();
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

        //to open keybord when click on searc view
//        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasfocus) {
//                if (!hasfocus){
//                    binding.searchView.setQuery("Search ",false);
//
//
//                }
//
//            }
//        });


        binding.floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,Datainsert.class);
            intent.putExtra("type","ADD");
            startActivityForResult(intent,1);


        });
        binding.RV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //binding.RV.setHasFixedSize(true);
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
                       //  Toast.makeText(MainActivity.this, notes.getTitle()+" Deleted",Toast.LENGTH_LONG).show();
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

                     }
            }
        }).attachToRecyclerView(binding.RV);


    }

//    private void showinputmethod(View view) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm!=null) {
//            imm.showSoftInput(view,0);
//        }
//    }


//    public void openupdate(notes notes) {
//
//    }

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return  true;
//
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.DELETE){
//            int position = item.getGroupId(); // Get the position of the long-pressed item
//          //  notes note = adapter.getNote(position);
//           // noteviewmodel.delete(note);
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // RVadapter adapter = new RVadapter();
        if (data != null) {
            if (requestCode == 1) {
                String title = data.getStringExtra("title");
                String display = data.getStringExtra("display");
                notes notes = new notes(title, display);
                noteviewmodel.insert(notes);
//            binding.RV.scrollToPosition(0);//after inserting new data it will automatically  scropp to top
                //Toast.makeText(this, "Data Inserted", Toast.LENGTH_LONG).show();
            } else if (requestCode == 2 ) {
                String title = data.getStringExtra("notetext");
                String display = data.getStringExtra("notedisplay");
                notes notes = new notes(title, display);
                notes.setId(data.getIntExtra("noteid", 0));
                noteviewmodel.update(notes);
                // Notify the existing adapter attached to the RecyclerView
               // RVadapter adapter = (RVadapter) binding.RV.getAdapter();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Update Canceled", Toast.LENGTH_LONG).show();
            }
        }


    }




}
