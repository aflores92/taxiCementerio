package com.andresflores.taxicementerio;

import android.app.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;


public class Main5Activity extends Activity implements LocationListener {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPedidosRef = mRootRef.child("Pedidos");
    private DatabaseReference mPedidoU = mPedidosRef;
    ListView listView;
    ArrayList<String> listViewContent;
    ArrayAdapter arrayAdapter;
    private LocationManager locationManager;
    ArrayList<String> data = new ArrayList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        listView = (ListView) findViewById(R.id.listView);
        listViewContent = new ArrayList<String>();
        listViewContent.add("Prueba");
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listViewContent);
        listView.setAdapter(arrayAdapter);





        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //  if (location != null) {

        updateLocation(location);
        // }0



    }

    public void updateLocation(Location location) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pedidos");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String,String> map = dataSnapshot.getValue(Map.class);


                String values = dataSnapshot.getValue(String.class);
                data.add(values);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (String datos : data){

            Log.v("Prueba",datos);

        }


        GeoFire geoFire = new GeoFire(ref);


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(37.7832, -122.4056), 0.6);


      //  DatabaseReference mUserGeo = mPedidoU.orderByChild();

       // GeoFire geoFire = new GeoFire(mUserGeo);

       // geoFire.setLocation("Localizacion", new GeoLocation(location.getLatitude(),location.getLongitude()));
        geoFire.getLocation("Localizacion", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onLocationChanged(Location location) {

        updateLocation(location);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
