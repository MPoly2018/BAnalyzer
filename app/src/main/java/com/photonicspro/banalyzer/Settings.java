package com.photonicspro.banalyzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    EditText Humidity_Texte ;

    EditText Temperature_Texte ;

    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setTitle("Settings");

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent activity2Intent22 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent22);
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        Humidity_Texte= (EditText) findViewById(R.id.Humidity);
        Temperature_Texte= (EditText) findViewById(R.id.Temperature);
        sharedpreferences = getSharedPreferences("mypreference",
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("humidity_value")) {
            Humidity_Texte .setText(sharedpreferences.getString("humidity_value", ""));
        }
        if (sharedpreferences.contains("Temperature_value")) {
            Temperature_Texte.setText(sharedpreferences.getString("Temperature_value", ""));

        }









        Button buttonSave = findViewById(R.id.Save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("humidity_value", Humidity_Texte.getText().toString());
                editor.putString("Temperature_value", Temperature_Texte.getText().toString());
                editor.commit();



               Configuration.Temperature= Double.parseDouble(Temperature_Texte.getText().toString());
               Configuration.Humidity= Double.parseDouble(Humidity_Texte.getText().toString());




//               Context context = getApplicationContext();
//               int duration = Toast.LENGTH_LONG;
//               Toast toast = Toast.makeText(context, getResources().getString(R.string.saved_humidity)  + " ...  " + Humidity_Texte.getText().toString(), duration);
//               toast.show();


            }
        });

    }







}
