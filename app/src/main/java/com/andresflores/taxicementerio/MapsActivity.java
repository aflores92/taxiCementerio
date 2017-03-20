package com.andresflores.taxicementerio;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


        private LocationManager locationManager;
        private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        private DatabaseReference mUserRef = mRootRef.child("usuario");
        private Button sendData;
        private FirebaseAuth mAuth= FirebaseAuth.getInstance(); //variable que obtiene la autenticacion
        private GoogleMap map;
        private Boolean requestActive = false;
        private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Obtener el Usuario con el que estoy logueado en la aplicacion
        private DatabaseReference mUserData = mUserRef.child(user.getUid()); //Obtener llave unica del Usuario


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            sendData = (Button) findViewById(R.id.button6);

        mapFragment.getMapAsync(this);


        //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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

            if(location != null){

                updateLocation(location);
            }

        }

    public void updateLocation (Location location){

        if(requestActive) {

            DatabaseReference mUserGeo = mUserData.child("geofire");
            GeoFire geoFire = new GeoFire(mUserGeo);

            geoFire.setLocation("firabase-hq", new GeoLocation(location.getLatitude(),location.getLongitude()));


        }

    }

     public void requestUber(View view){

         user = mAuth.getCurrentUser();
         if(requestActive == false) {




                     mUserData.setValue(user); //Enviar data a Firebase
                     sendData.setText("Cancelar Taxista"); //Cambiar texto del Boton
                     requestActive = true;


         }

         else { //Si se ha pedido y se presiona otra vez el boton elimina el Perdido

             //Si se ha pedido y se presiona otra vez el boton elimina el Perdido

             sendData.setText("Conseguir Taxista"); //Cambiar texto del Boton
             requestActive = false;

             //Queary NoSql para conseguir mi Pedido
             Query myCurrentUser = mUserRef.child("usuarios").child(user.getUid()).orderByChild("driverRequest").equalTo(null);

             myCurrentUser.removeEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     dataSnapshot.getRef().setValue(null); //ELiminar Pedido

                 }

                 //Si se presenta algun error en el manejo de la base de datos este metodo permite
                 //obtener el error
                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

         }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }



    @Override
    public void onLocationChanged(Location location) {

        map.clear();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("i'm here");

        map.addMarker(markerOptions);

        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        if(requestActive) {

            DatabaseReference mUserGeo = mUserData.child("geofire");
            GeoFire geoFire = new GeoFire(mUserGeo);

            geoFire.setLocation("firabase-hq", new GeoLocation(location.getLatitude(),location.getLongitude()));


        }
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

