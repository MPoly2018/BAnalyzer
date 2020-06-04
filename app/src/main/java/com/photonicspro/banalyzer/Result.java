package com.photonicspro.banalyzer;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    String Res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        toolbar.setTitle("AlcoTest Result");

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent activity2Intent22 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent22);
            }
        });


        Res= getIntent().getStringExtra("Res");

        TextView Resultat = findViewById(R.id.text_result);
        Resultat.setText(Res);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("*/*");
//                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.putExtra(Intent.EXTRA_EMAIL, "BreathAlyzer.Support@gmail.com");
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Need Help");
//                intent.putExtra(Intent.EXTRA_TEXT, Res);
//
//
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }



                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Need Help");
                intent.putExtra(Intent.EXTRA_TEXT, Res);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
