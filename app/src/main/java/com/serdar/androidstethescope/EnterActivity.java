package com.serdar.androidstethescope;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
    }

    public void Make_Record(View view) {
        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void Records(View view) {
        Intent intent = new Intent(EnterActivity.this, SoundListActivity.class);
        startActivity(intent);
    }

    public void Privacy(View view) {
    }
}