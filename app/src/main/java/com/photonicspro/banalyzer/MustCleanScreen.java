package com.photonicspro.banalyzer;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class MustCleanScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_must_clean_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeButtonEnabled(true

        toolbar.setTitle("Clean screen");

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent activity2Intent22 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent22);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity2Intent = new Intent(getApplicationContext(), CleanScreenActivity.class);
                startActivity(activity2Intent);
            }
        });

    }

}
