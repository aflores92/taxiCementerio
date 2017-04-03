package com.andresflores.taxicementerio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private LocationManager locationManager;
    boolean puerta = false;
    private Button boton6;



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
        boton6 = (Button) findViewById(R.id.button6);


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
                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(pasajeroLatiud,pasajeroLongitud)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Ubicacion Pasajero")));

                for(Marker marker : markers){

                    builder.include(marker.getPosition());

                }
                LatLngBounds bounds = builder.build();
            int padding = 250;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
            mMap.animateCamera(cu);
        }
        });

      /*  if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
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

*/



      /*  DatabaseReference buscar = FirebaseDatabase.getInstance().getReference()
                .child("Confirmacion")*//*.child(itemID)*//*;
        buscar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children){

                    Confirmacion c = child.getValue(Confirmacion.class);

                    if ("En_Progreso".equals(c.getCompletado())){

                        puerta = true;
                        boton6.setVisibility(View.INVISIBLE);

                    }

                    if ("Listo".equals(c.getCompletado())){

                        puerta= true;
                        boton6.setVisibility(View.VISIBLE);

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/





    }


    public void aceptarPasajero(View view) {

        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("Pedidos")
                .child(data).child("Conductor");
        reference.setValue(user.getUid());



        DatabaseReference aceptacion = FirebaseDatabase.getInstance().getReference()
                .child("Confirmacion").child("Localizacion");

        GeoFire geoFire = new GeoFire(aceptacion);
        geoFire.setLocation(data, new GeoLocation(usuarioLatitud,usuarioLongitud));

        DatabaseReference  aceptacion2 = FirebaseDatabase.getInstance().getReference().child("Confirmacion").child(data);
        aceptacion2.child("Completado").setValue("En_Progreso");




       /* DatabaseReference  aceptacion2 = FirebaseDatabase.getInstance().getReference().child("Confirmacion");
        aceptacion2.child(data).child("USER_ID");*/

        /*aceptacion.child(data).setValue("USER_ID");
        aceptacion.*/




        Intent intent = new Intent(
                Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?daddr="+usuarioLatitud+","+usuarioLongitud+""

        )


        );

        startActivity(intent);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();




    }

    public void listoPasajero (View view){

        DatabaseReference  aceptacion2 = FirebaseDatabase.getInstance().getReference().child("Confirmacion").child(data);
        aceptacion2.child("Completado").setValue("Listo");



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
