package com.photonicspro.banalyzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.lang.Math.abs;


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
    long TempsBreath = 0;

    private int curBrightnessValue;




    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedpreferences= getSharedPreferences("mypreference",
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("humidity_value")) {
            Configuration.Humidity= Double.parseDouble(sharedpreferences.getString("humidity_value", "")) ;
        }
        if (sharedpreferences.contains("Temperature_value")) {
            Configuration.Temperature= Double.parseDouble(sharedpreferences.getString("Temperature_value", "")) ;
        }


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

                Log.d("BAnalyzerLogP", "--------------------------- " + module.get_productName());


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

                        Log.d("BAnalyzerLogP", "---------------FunctionID------------ " + functionId);


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



                            double YoctoPrecedentValue= 0d;

                            while (ysensor.isOnline() && !StopMeasuring && (System.currentTimeMillis()-t0) < Configuration.TotalMesureDurationMs) {

                                Log.d("BAnalyzerLogP", "--------------------------- " + "Entered");


                                YoctoPrecedentValue =  YoctoCurrentValue;
                                YoctoCurrentValue = ysensor.get_currentValue();
                                currentimeMs = System.currentTimeMillis();

                                SerieV.add(new Ameasure(currentimeMs, YoctoCurrentValue));

                                //Log.d("Measure == ",currentimeMs +" " + YoctoCurrentValue );



                                Log.d("BAnalyzerLogP", "Mesure == "+ currentimeMs +" " + YoctoCurrentValue + "  TempsBreath "  + TempsBreath  );

                                if (YoctoCurrentValue > Configuration.BorneSup )  {

                                    BonneMesure = true;


                                    // si ca depasse la borne sup et la courbe commence à redescendre  de 2 millivolts on mesure le temps de breath
                                    if (  (YoctoPrecedentValue-YoctoCurrentValue >= 2)  && (TempsBreath == 0 )){

                                        TempsBreath = currentimeMs - t0; // Temps du breath en MS

                                        Log.d("BAnalyzerLogP", "-----TempsBreath------------- " + TempsBreath);

                                    }


                                }

                                if( BonneMesure && YoctoCurrentValue <  Configuration.BorneInf){

                                    Log.d("BAnalyzerLogP", "-----Stop BonneMesure------------- " + "   TempsBreath : " +  TempsBreath);


                                    StopMeasuring = true;
                                    Tf= currentimeMs;


                                    Log.d("BAnalyzerLogP", "-----Tff------------- " + Tf);


                                }else{


                                    YAPI.Sleep(Configuration.LogFrequencyMs);

                                }



                            }

                            if (StopMeasuring==false){

                                msg = "Your breath could not be analyzed, please see How to proceed";
                                Log.d("BAnalyzerLogP", "-----stop 1 ------------- " );



                            }else {

                                Log.d("BAnalyzerLogP", "-----stop 2 ------------- " );
                                int nbMeasures = SerieV.size();

                                for (int i = nbMeasures - 1; i > 0; i--) {


                                    if (SerieV.get(i).V > Configuration.BorneSup) {


                                        Ti = SerieV.get(i).T;
                                        i = 0;



                                    }
                                }

                                T_total = Tf - Ti;

                                Log.d("BAnalyzerLogP", "-----Ti ------------- " + Ti + "   T_total  " + T_total);



                                if ((15 < Configuration.Temperature) && (Configuration.Temperature < 20) && (30 < Configuration.Humidity) && (Configuration.Humidity < 40) && 1000 < TempsBreath) {


                                    Log.d("BAnalyzerLogP", "-----sucess Evaluation ------------- " );

                                    if (T_total > 7000) {
                                        msg = "Your blood alcohol content is 0.00";

                                    } else if ((6500 < T_total) && (T_total <= 7000)) {
                                        msg = "Your blood alcohol content is 0.01";

                                    } else if ((6300 < T_total) && (T_total <= 6500)) {

                                        msg = "Your blood alcohol content is 0.02";

                                    } else if ((5900 < T_total) && (T_total <= 6300)) {

                                        msg = "Your blood alcohol content is 0.03";

                                    } else if ((5500 < T_total) && (T_total <= 5900)) {

                                        msg = "Your blood alcohol content is 0.04";

                                    } else if ((5100 < T_total) && (T_total <= 5500)) {

                                        msg = "Your blood alcohol content is 0.05";

                                    } else if ((4700 < T_total) && (T_total <= 5100)) {

                                        msg = "Your blood alcohol content is 0.06";

                                    } else if ((4200 < T_total) && (T_total <= 4700)) {

                                        msg = "Your blood alcohol content is 0.07";

                                    } else if ((3900 < T_total) && (T_total <= 4200)) {

                                        msg = "Your blood alcohol content is 0.08";

                                    } else if (T_total <= 3900) {

                                        msg = "Your blood alcohol content is over 0.08";

                                    }


                                } else {

                                    msg = "Your breath could not be analyzed, please see How to proceed2";
                                }

                            }

                            PrintData();


                        } catch (YAPI_Exception e) {
                            e.printStackTrace();

                        }
                    }



                    writeStringAsFile("Output3.csv");
                   // WriteToFile();
                    Context context = getApplicationContext();

                    int duration = Toast.LENGTH_LONG;


                    Toast toast = Toast.makeText(context, "Tf-Ti= " +T_total + " ms " + "  TF " + Tf+ "  TI " + Ti+ "  TempsBreath " + TempsBreath  , duration);
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

            String path =
                    Environment.getExternalStorageDirectory() + File.separator  + "yourFolder";
            // Create the folder.
            File folder = new File(path);
            folder.mkdirs();

            Log.e("Manel2", "File Path: " + path);

            // Create the file.
            File file = new File(folder, "Voltage2.csv");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(path + "/Voltage.csv", Context.MODE_APPEND));

            for(int i =0 ; i<SerieV.size(); i++){

                outputStreamWriter.write(SerieV.get(i).T + " ; " + SerieV.get(i).V + "\n");

            }

            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



    public void writeStringAsFile( String fileName) {
        Context context = getApplicationContext();

        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));


            out.write("Ti " + " ; "+  Ti + "\n");

            out.write("Tf " + " ; "+  Tf + "\n");

            long dur = Tf-Ti;

            out.write("duree " + " ; "+ dur  + "\n");

            out.write("TempsBreath " + " ; "+ TempsBreath + "\n");


            for(int i =0 ; i<SerieV.size(); i++){

                out.write(SerieV.get(i).T + " ; " + SerieV.get(i).V + "\n");

            }

            Log.d("File path", context.getFilesDir().getPath());
            out.close();
        } catch (IOException e) {
            Log.e("File path", "error in file processing");
        }
    }

    private void PrintData(){


        Log.d("MeasureVolt2 == ","Ti = " + Ti);

        Log.d("MeasureVolt2 == ","TF = " + Tf);

        Log.d("MeasureVolt2 == ","duration = " + T_total);

        Log.d("MeasureVolt2 == ","TempsBreath = " + TempsBreath);

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
