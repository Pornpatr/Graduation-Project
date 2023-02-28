package com.example.projectestimoteapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projectestimoteapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class Homefragment extends Fragment {
    static String MQTTHOST = "tcp://hairdresser.cloudmqtt.com:15651";
    static String USERNAME = "kfrpnxgl";
    static String PASSWORD = "uEjmd1OJPzER";
    String topicStr = "LED";
    MqttAndroidClient client;
    TextView txtStatus;
    String topicStatus = "State";
    static String Status="";


    Button autoon,autooff,manualstay,manualout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

              View v =  inflater.inflate(R.layout.fragment_homefragment, container, false);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getActivity().getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        autoon=v.findViewById(R.id.autoon);
        autoon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Status="ON";
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("bluetooth");
                myRef.child("Auto").setValue("ON");

            }
        });
        autooff=v.findViewById(R.id.autooff);
        autooff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Status="OFF";
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("bluetooth");
                myRef.child("Auto").setValue("OFF");
            }
        });
        manualstay=v.findViewById(R.id.manualstay);
        manualstay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicStr;
                String message = "L1";
                String message2 =  "D2";
                try {
                    client.publish(topic, message.getBytes(), 0, false);
                    client.publish(topic, message2.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
        manualout=v.findViewById(R.id.manualout);
        manualout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = topicStr;
                String message = "D1" ;
                String message2 = "L2" ;
                try {
                    client.publish(topic, message.getBytes(), 0, false);
                    client.publish(topic, message2.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        txtStatus = v.findViewById(R.id.Status);


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getActivity(), "connected", Toast.LENGTH_LONG).show();
                    setSubscrition();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getActivity(), "not connected", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {



                txtStatus.setText(new String(message.getPayload()));
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        return v;
    }
    private void setSubscrition(){
        try{
            client.subscribe(topicStatus,0);
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

}