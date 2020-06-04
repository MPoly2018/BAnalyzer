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

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class AnalyzeActivity extends AppCompatActivity {

    private String serial = "";                 // contient le numero de serie du yocto
    String voltage = "";
    private Handler handler = null;
    YGenericSensor sensor1;
    double YoctoCurrentValue;
    private boolean YoctoMilliVolt = false;
    private YSensor ysensor;                    // contient le yocto
   private ArrayList<Ameasure> SerieV;            // contient les mesures
    private  long Tf = 0;
    private  long Ti = 0;
    private long T_total =0;

    private int curBrightnessValue;


    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }

    @Override
    protected void onStart() {


       SetFullBrightness();


        // initializing the sensor
        super.onStart();
        try {

            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
            YModule module = YModule.FirstModule();
            while (module != null) {
                if (module.get_productName().equals("Yocto-milliVolt-Rx")) {

                    YoctoMilliVolt = true;
                    serial = module.get_serialNumber();
                } else if (module.get_productName().equals("Yocto-Volt")) {
                    serial = module.get_serialNumber();
                    YoctoMilliVolt = false;

                }
                module = module.nextModule();
            }
        } catch (YAPI_Exception e) {
            e.printStackTrace();
        }



        // get the current value of the sensor
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

        if (serial != null) {


            // sensor1 = YGenericSensor.FindGenericSensor(serial + ".genericSensor1");

            boolean notYocto = true;
            YSensor ysensor2 = YSensor.FirstSensor();
            while (ysensor2 != null ) {

                try {
                    String serial1 = ysensor2.get_module().get_serialNumber();
                    //Log.d(TAG,"DeviceRemoval:" + serial);

                    if (serial1.equals(serial)) {
                        ysensor= ysensor2;

                        String functionId = ysensor.get_functionId();

                        if (functionId.equals("voltage2") || functionId.equals("voltage222")){

                            Log.d("BAnalyzerLog", "----+++ " + functionId + "CurrentVoltage : " + ysensor.get_currentValue());



                        }

                    }


                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }

                ysensor2 = ysensor2.nextSensor();
            }


            // get the current value of the sensor


                    if (serial != null) {

                        try {

//                            YoctoCurrentValue = ysensor.get_currentValue();
//                            voltage = String.format("%.3f %s", YoctoCurrentValue, ysensor.get_unit());
//                            Log.d("===>BAnalyzer", serial + "voltage: " + YoctoCurrentValue);

                            Boolean StopMeasuring = false;
                            long  TempsEcouleMesure = 0;
                            SerieV = new ArrayList<Ameasure>();

                            long t0 = System.currentTimeMillis();


                            Boolean BonneMesure = false;

                            long currentimeMs = 0;

                            while (ysensor.isOnline() && !StopMeasuring && (System.currentTimeMillis()-t0) < Configuration.TotalMesureDurationMs) {


                                YoctoCurrentValue = ysensor.get_currentValue();
                                currentimeMs = System.currentTimeMillis();

                                SerieV.add(new Ameasure(currentimeMs, YoctoCurrentValue));

                                //Log.d("Measure == ",currentimeMs +" " + YoctoCurrentValue );


                                if (YoctoCurrentValue > Configuration.BorneSup )  {

                                    BonneMesure = true;


                                }

                                if( BonneMesure && YoctoCurrentValue<  Configuration.BorneInf){

                                    StopMeasuring = true;
                                    Tf= currentimeMs;



                                }


                                YAPI.Sleep(Configuration.LogFrequencyMs);
                            }

                            if (StopMeasuring==false){

                                msg = "Your breath could not be analyzed, please see How to proceed";


                            }else {

                                int nbMeasures = SerieV.size();

                                for (int i = nbMeasures-1 ; i >0; i--){


                                    if (SerieV.get(i).V >   Configuration.BorneSup){


                                        Ti= SerieV.get(i).T;
                                        i=0;

                                    }
                                }

                                T_total = Tf- Ti;

                                if (T_total > 7000) {
                                    msg =  "Your blood alcohol content is 0.00";

                                } else if ((6500 < T_total) && (T_total <= 7000)){
                                    msg =  "Your blood alcohol content is 0.01";

                                } else if ((6300 < T_total) && (T_total  <= 6500)){

                                    msg =  "Your blood alcohol content is 0.02";

                                } else if ((5900 < T_total) && (T_total  <= 6300)){

                                    msg =  "Your blood alcohol content is 0.03";

                                } else if ((5500 < T_total) && (T_total  <= 5900)){

                                    msg =  "Your blood alcohol content is 0.04";

                                } else if ((5100 < T_total) && (T_total  <= 5500)){

                                    msg =  "Your blood alcohol content is 0.05";

                                } else if ((4700 < T_total) && (T_total  <= 5100)){

                                    msg =  "Your blood alcohol content is 0.06";

                                } else if ((4200 < T_total) && (T_total <= 4700)){

                                    msg =  "Your blood alcohol content is 0.07";

                                } else if ((3900 < T_total) && (T_total  <= 4200)){

                                    msg =  "Your blood alcohol content is 0.08";

                                } else if (T_total <= 3900){

                                    msg =  "Your blood alcohol content is over 0.08";

                                }




                            PrintData();



                            }


                        } catch (YAPI_Exception e) {
                            e.printStackTrace();

                        }
                    }



                    Context context = getApplicationContext();
                   // CharSequence text = "The voltage = ";
                    int duration = Toast.LENGTH_LONG;

                    //WriteToFile();
                    Toast toast = Toast.makeText(context, "Tf-Ti= " +T_total + " ms "  , duration);
                    toast.show();


            Intent activity2Intent = new Intent(AnalyzeActivity.this,  Result.class);
            activity2Intent.putExtra("Res",msg);
            startActivity(activity2Intent);


        }
            }
            }, 50);   //ms




        }


    private void SetFullBrightness() {


        curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);


        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1;
        getWindow().setAttributes(lp);

    }

        private void WriteToFile() {


        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(Environment.getExternalStorageDirectory() +"/Download/LogVoltage.csv", Context.MODE_APPEND));

            for(int i =0 ; i<SerieV.size(); i++){

                outputStreamWriter.write(SerieV.get(i).T + " ; " + SerieV.get(i).V);

            }

            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void PrintData(){


        Log.d("MeasureVolt2 == ","Ti = " + Ti);

        Log.d("MeasureVolt2 == ","TF = " + Tf);

        Log.d("MeasureVolt2 == ","duration = " + T_total);

        for(int i =0 ; i<SerieV.size(); i++){

            Log.d("MeasureVolt2 == ",SerieV.get(i).T + "   " + SerieV.get(i).V);

        }

    }


        @Override
        protected void onStop ()
        {

            // handler.removeCallbacks(r);
            YAPI.FreeAPI();

            super.onStop();
        }

    }
