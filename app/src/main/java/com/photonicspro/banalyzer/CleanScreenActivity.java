package com.photonicspro.banalyzer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YGenericSensor;
import com.yoctopuce.YoctoAPI.YModule;
import com.yoctopuce.YoctoAPI.YSensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CleanScreenActivity extends AppCompatActivity {

    CountDownTimer MyTimer;
    ProgressBar progress_countdown;
    int TimerLengthSeconds=0;
    int SecondsRemaining=0;

    String TimerStatus = "Paused";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent activity2Intent22 = new Intent(getApplicationContext(), MustCleanScreen.class);
                startActivity(activity2Intent22);
            }
        });




        progress_countdown = (ProgressBar) findViewById(R.id.progress_countdown);



        MyTimer =new CountDownTimer(9000, 100) {
            TextView timestart = (TextView) findViewById(R.id.text_view_CountDown);




            public void onTick(long millisUntilFinished) {
                timestart.setText("" + (millisUntilFinished + 1000)/1000);

                progress_countdown.setProgress((int)((millisUntilFinished+ 1000)/1000)*10);

                TimerStatus = "Running";
            }

            public void onFinish() {

                TimerStatus = "Finished";

                progress_countdown.setVisibility(View.INVISIBLE);
                timestart.setVisibility(View.INVISIBLE);






                Intent activity2Intent = new Intent(getApplicationContext(), AnalyzeActivity.class);
                startActivity(activity2Intent);

            }
        }.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MyTimer.start();


    }





    @Override
    protected void onStop ()
    {
        super.onStop();
        MyTimer.cancel();


    }





}
