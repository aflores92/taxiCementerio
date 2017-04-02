package com.andresflores.taxicementerio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,LocationListener{

    private GoogleMap mMap;
    Intent i ;
    Double usuarioLatitud;
    Double usuarioLongitud;
    Double pasajeroLatiud;
    Double pasajeroLongitud;
    String data;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Obtener el Usuario con el que estoy logueado en la aplicacion




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            usuarioLatitud = null;
            usuarioLongitud = null;
        }
        else {
            usuarioLatitud = extras.getDouble("usuarioLatitud");
            usuarioLongitud = extras.getDouble("usuarioLongitud");
            pasajeroLatiud = extras.getDouble("pasajeroLatitud");
            pasajeroLongitud = extras.getDouble("pasajeroLongitud");
            data = extras.getString("data");
        }

        Log.v("Prueba",usuarioLatitud.toString());
        Log.v("Prueba",usuarioLongitud.toString());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                ArrayList<Marker> markers = new ArrayList<Marker>();
                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(usuarioLatitud,usuarioLongitud)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("Usted Esta Aqui Sr.Taxista")));
                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(pasajeroLatiud,pasajeroLongitud)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Te Amo Erika Att:Tu Esposo")));

                for(Marker marker : markers){

                    builder.include(marker.getPosition());

                }
                LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
            mMap.animateCamera(cu);
        }
        });




    }


    public void aceptarPasajero(View view) {

        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("Pedidos")
                .child(data).child("Conductor");

        reference.setValue(user.getUid());

        Intent intent = new Intent(
                Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?daddr="+pasajeroLatiud+","+pasajeroLongitud+""

        )


        );

        startActivity(intent);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();




    }




    @Override
    public void onLocationChanged(Location location) {

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
