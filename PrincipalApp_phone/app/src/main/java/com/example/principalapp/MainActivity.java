package com.example.principalapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity implements DialogParam.DialogListener {
    private static final String TAG = "MainActivity";

    MQTThelper mqttHelper;

    String last_porteur_name="";
    TextView porteur;
    String data;

    TextView my_temps;
    TextView my_pression;
    Gauge my_humidity_gauge;
    Gauge my_temp_gauge;

    LineChart my_lineChart_1;
    LineChart my_lineChart_2;
    LineChart my_lineChart_3;

    ChartHelper mChart_1;
    ChartHelper mChart_2;
    ChartHelper mChart_3;

    int cnt=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");Log.d(TAG, "onCreate: started");

        porteur = (TextView) findViewById(R.id.porteurName);


        /*###################################GAUGES###################################################*/
        my_pression = (TextView) findViewById(R.id.pressureDisplaying);
        my_humidity_gauge = (Gauge) findViewById(R.id.gauge_humidity);
        my_temp_gauge = (Gauge) findViewById(R.id.gauge_temp);
        my_temps = (TextView) findViewById(R.id.txtTime);

        my_temp_gauge.setUpperText("°C");
        my_humidity_gauge.setUpperText("%");
        my_temp_gauge.setTextSize(25);
        my_humidity_gauge.setTextSize(25);

        /*###################################GRAPHS###################################################*/
        my_lineChart_1 = (LineChart) findViewById(R.id.graph1);
        mChart_1 = new ChartHelper(my_lineChart_1, Color.parseColor("#2ecc71"));
        my_lineChart_2 = (LineChart) findViewById(R.id.graph2);
        mChart_2 = new ChartHelper(my_lineChart_2, Color.parseColor("#f1c40f"));
        my_lineChart_3 = (LineChart) findViewById(R.id.graph3);
        mChart_3 = new ChartHelper(my_lineChart_3, Color.parseColor("#e74c3c"));



        /*################################fab_BUTTON########################################*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        } );


        startMqtt();    //on écoute le serveur



    }

    public void openAlerteDialog(){
        DialogWarning my_alerteDialog = new DialogWarning();
        my_alerteDialog.show(getSupportFragmentManager(), "this my dialog alerte");
    }


    /*###################################communication_avec_dialog_settings###################################################*/
    public  void openDialog(){
        DialogParam my_dialogParam = new DialogParam();
        my_dialogParam.show(getSupportFragmentManager(), "this my dialog settings");
    }

    /*#####################################reception des datas#################################################*/
    public void startMqtt(){
        mqttHelper = new MQTThelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }
            
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                data = mqttMessage.toString();

                //System.out.println("topic de réception: " + topic);
                //System.out.println("données recuent : "+ data); //affichage des données brutes
                JSONObject jsonObject = new JSONObject(data); //on dit que ce qu'on recoit(data) est au format JSON



                /*##########extraction de donnée du JSON data############*/
                /*porteur*/
                String NameOfThePorteur = jsonObject.getString("porteurNom");
                System.out.println("PORTEUR:" + NameOfThePorteur);
                porteur.setText(NameOfThePorteur);
                /*gas data*/
                float gas_data1 = Float.valueOf(jsonObject.getString("gas1"));
                float gas_data2 = Float.valueOf(jsonObject.getString("gas2"));
                float gas_data3 = Float.valueOf(jsonObject.getString("gas3"));
                /*humidity, pressure, temp*/
                float humidity_data = Float.valueOf(jsonObject.getString("humidity"));
                float temperature_data = Float.valueOf(jsonObject.getString("temperature"));
                float pressure_data = Float.valueOf(jsonObject.getString("pressure"));
                /*definition du nbr de decimal*/
                DecimalFormat df = new DecimalFormat("###.#");
                humidity_data=Float.valueOf(df.format(humidity_data));
                temperature_data=Float.valueOf(df.format(temperature_data));
                pressure_data=Float.valueOf(df.format(pressure_data));

                /*############################Affichage#####################################*/
                my_pression.setText(String.valueOf(pressure_data));
                my_humidity_gauge.setValue(humidity_data);
                my_temp_gauge.setValue(temperature_data);
                my_humidity_gauge.setLowerText(String.valueOf(humidity_data));
                my_temp_gauge.setLowerText(String.valueOf(temperature_data));
                /*Affichage du temps*/
                String temps_data = jsonObject.getString("time");
                my_temps.setText(temps_data);


                /*################Ajout des données des capteurs dans les graphs#################*/
                mChart_1.addEntry(gas_data1, "MQ2");
                mChart_2.addEntry(gas_data2, "MQ5");
                mChart_3.addEntry(gas_data3, "MQ9");

                /*##############ouverture de la pop-up d'alerte en cas de chute####################*/
                int fall_data = jsonObject.getInt("fall");
                if (fall_data == 1){
                    openAlerteDialog();
                }else{
                    System.out.println("le porteur se porte bien");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    /*#########################################################*/
    @Override
    public void applyTexts(String porteur_name, String server, String usr, String pwd) {
        mqttHelper.setServerUri(server);
        mqttHelper.setUsername(usr);
        mqttHelper.setPassword(pwd);
        setPorteur(porteur_name);
    }

    /*########################modif du nom du laborantin###############################""*/
    private void setPorteur(String porteur_name){
        if(porteur_name.length()!=0){
            System.out.println("ancien nom:" + porteur_name);
            System.out.println("nom de porteur à traiter:" + porteur_name);
            JSONObject wearer_json = new JSONObject();
            try {
                wearer_json.put("porteurNom", porteur_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mqttHelper.setPayload(wearer_json);
            mqttHelper.publishToTopic();
            last_porteur_name=porteur_name;
        }
        else {
            System.out.println("Nom du porteur inchangé: "+last_porteur_name);
        }
    }

}










