package com.example.todaybuddy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mynotes")
public class notes {
    String title,displayText;
    @PrimaryKey(autoGenerate = true)
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public notes(String title, String displayText) {
        this.title = title;
        this.displayText = displayText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
