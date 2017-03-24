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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import static com.google.ads.AdRequest.LOGTAG;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {


    private LocationManager locationManager;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserRef = mRootRef.child("usuario");
    private Button sendData;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //variable que obtiene la autenticacion
    private GoogleMap map;
    private Boolean requestActive = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Obtener el Usuario con el que estoy logueado en la aplicacion
    private DatabaseReference mUserData = mUserRef.child(user.getUid()); //Obtener llave unica del Usuario

    //GPS

    private GoogleApiClient apiClient;
    private TextView lblLatitud;
    private TextView lblLongitud;
    private ToggleButton btnActualizar;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    //Propiedades GPS
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //GPS

        lblLatitud = (TextView) findViewById(R.id.lblLatitud);
        lblLongitud = (TextView) findViewById(R.id.lblLongitud);
        btnActualizar = (ToggleButton) findViewById(R.id.btnActualizar);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        ///


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

        if (location != null) {

            updateLocation(location);
        }

    }

    private void updateUI(Location loc) {
        if (loc != null) {
            lblLatitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
            lblLongitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
        } else {
            lblLatitud.setText("Latitud: (desconocida)");
            lblLongitud.setText("Longitud: (desconocida)");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
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
        markerOptions.title("Usted Esta Aqui");

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

