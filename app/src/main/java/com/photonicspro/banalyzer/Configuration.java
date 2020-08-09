package com.photonicspro.banalyzer;

public class Configuration {


    // temps d'attente pour commencer l'analyse en millisecondes

    static final int LogFrequencyMs= 500;
    static final long TotalMesureDurationMs = 40000;

  //  static final Double BorneSup = 0.1d;
  //  static final Double BorneInf = 0.01d;

    static final Double BorneSup = 280d;
    static final Double BorneInf = 140d;


    static final String functionId_YoctoVolt = "voltage1";
    static final String functionId_YoctoMilliVolt = "voltage2";

    static final String ANEMAIL = "BreathAlyzer.Support@gmail.com";


    public static Double Humidity;
    public static Double Temperature;



}
