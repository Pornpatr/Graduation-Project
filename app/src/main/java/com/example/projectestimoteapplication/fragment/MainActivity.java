package com.example.projectestimoteapplication.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectestimoteapplication.R;
import com.github.pwittchen.reactivebeacons.library.rx2.Beacon;
import com.github.pwittchen.reactivebeacons.library.rx2.Proximity;
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.example.projectestimoteapplication.fragment.Homefragment.Status;

//import androidx.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import androidx.annotation.NonNull;
//import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {
    //

    static String MQTTHOST = "tcp://hairdresser.cloudmqtt.com:15651";
    static String USERNAME = "kfrpnxgl";
    static String PASSWORD = "uEjmd1OJPzER";
    String topicStr = "LED";
    MqttAndroidClient client;
    //

    int Rs;
    private static final boolean IS_AT_LEAST_ANDROID_M =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;
    private static final String ITEM_FORMAT = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s";
    private ReactiveBeacons reactiveBeacons;
    private Disposable subscription;
    //  private ListView lvBeacons;
    private Map<String, Beacon> beacons;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("bluetooth");

    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        //
        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        NavController navController;
        navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.Homefragment, R.id.Historyfragment, R.id.Settingfragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        reactiveBeacons = new ReactiveBeacons(this);
        beacons = new HashMap<>();


    }
    @Override protected void onResume() {
        super.onResume();

        if (!canObserveBeacons()) {
            return;
        }

        startSubscription();
    }

    private void startSubscription() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestCoarseLocationPermission();
            return;
        }

        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Beacon>() {
                    @Override public void accept(@NonNull Beacon beacon) throws Exception {
                        beacons.put(beacon.device.getName(), beacon);
                        refreshBeaconList();
                    }
                });
    }

    private boolean canObserveBeacons() {
        if (!reactiveBeacons.isBleSupported()) {
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!reactiveBeacons.isBluetoothEnabled()) {
            reactiveBeacons.requestBluetoothAccess(this);
            return false;
        } else if (!reactiveBeacons.isLocationEnabled(this)) {
            reactiveBeacons.requestLocationAccess(this);
            return false;
        } else if (!isFineOrCoarseLocationPermissionGranted() && IS_AT_LEAST_ANDROID_M) {
            requestCoarseLocationPermission();
            return false;
        }

        return true;
    }
    private void refreshBeaconList() {
        List<String> list = new ArrayList<>();

        for (Beacon beacon : beacons.values()) {
            list.add(getBeaconItemString(beacon));
        }

        int itemLayoutId = android.R.layout.simple_list_item_1;
//    lvBeacons.setAdapter(new ArrayAdapter<>(this, itemLayoutId, list));
    }
    private String getBeaconItemString(Beacon beacon) {


        String mac = beacon.device.getAddress();
        int rssi = beacon.rssi;
        double distance = beacon.getDistance();
        Proximity proximity = beacon.getProximity();
        String name = beacon.device.getName();
        if(name!=null){
//            Rs=rssi;

            myRef.child("mac").setValue(mac);
            myRef.child("rssi").setValue(rssi);
            myRef.child("proximity").setValue(proximity);
            myRef.child("distance").setValue(distance);
            myRef.child("name").setValue(name);
            checkRssi(rssi);
        }

        return String.format(ITEM_FORMAT, mac, rssi, distance, proximity, name);
    }

    private void checkRssi(int rssi) {

//        myRef.child("Auto").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Status = dataSnapshot.getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//
//            }
//        });
        if (Status == "ON") {
            // Near
            if (((rssi)) > (-65)) {
                String topic = topicStr;
                String message = "L1";
                String message2 = "D2";
                try {
                    client.publish(topic, message.getBytes(), 0, false);
                    client.publish(topic, message2.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            } else if (((rssi)) < (-65)) { // Far
                String topic = topicStr;
                String message = "L2";
                String message2 = "D1";
                try {
                    client.publish(topic, message.getBytes(), 0, false);
                    client.publish(topic, message2.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        }
    }









    @Override protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode,

                                                     @androidx.annotation.NonNull String[] permissions,
                                                     @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final boolean isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION;
        final boolean permissionGranted = grantResults[0] == PERMISSION_GRANTED;

        if (isCoarseLocation && permissionGranted && subscription == null) {
            startSubscription();
        }
    }

    private void requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] { ACCESS_COARSE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }

    private boolean isFineOrCoarseLocationPermissionGranted() {
        boolean isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        boolean isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION);
        boolean isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION);

        return isAndroidMOrHigher && (isFineLocationPermissionGranted
                || isCoarseLocationPermissionGranted);
    }

    private boolean isGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
    }


}