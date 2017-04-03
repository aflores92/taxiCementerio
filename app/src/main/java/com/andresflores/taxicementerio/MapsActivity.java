package com.andresflores.taxicementerio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private LocationManager locationManager;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPedidosRef = mRootRef.child("Pedidos");
    private Button sendData;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //variable que obtiene la autenticacion
    private GoogleMap map;
    private Boolean requestActive = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Obtener el Usuario con el que estoy logueado en la aplicacion
    private DatabaseReference mUserData = mPedidosRef.child(user.getUid()); //Obtener llave unica del Usuario
    private DatabaseReference mLocation = mRootRef.child("Localizacion");
    private DatabaseReference mPedidoU;
    String itemID;
    String guardarllave;
    Handler handler = new Handler();
    //AlertDialog alert = null;
    private Button confirmacion;
    private TextView mensaje;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

      /*  if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }*/

        sendData = (Button) findViewById(R.id.button6);
        confirmacion = (Button) findViewById(R.id.button4);
        confirmacion.setVisibility(View.INVISIBLE);

        mensaje = (TextView) findViewById(R.id.textView3);
        mensaje.setVisibility(View.INVISIBLE);


        mapFragment.getMapAsync(this);

        //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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

        itemID = mPedidosRef.push().getKey();
            updateLocation(location);
        //GPS
    }

/*    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        alert = builder.create();
        alert.show();
    }*/

/*
    @Override
    protected void onDestroy() {
        if(alert != null)
        {
            alert.dismiss ();
        }
    }
*/

    public void updateLocation (final Location location){

        if (requestActive==false){

            DatabaseReference verificar = FirebaseDatabase.getInstance().getReference()
                    .child("Pedidos")/*.child(itemID)*/;
            verificar.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot child : children){


                        Pedido p = child.getValue(Pedido.class);

                        if ( user.getUid().equals(p.getUSER_ID())){

                            requestActive = true;
                            itemID = child.getKey();
                        }


                    }


                  /*  Log.v("Verficando1",dataSnapshot.exists()?"Si_Existe":"NoExiste");
                    Log.v("Verficando2",dataSnapshot.hasChildren()?"Si_Existe":"NoExiste");
                    Log.v("Verficando3",dataSnapshot.getValue()!= null?"Si_Existe":"NoExiste");
*/


             /*       if (dataSnapshot.exists())
                    {

                        Pedido p = dataSnapshot.getValue(Pedido.class);

                        if ( user.getUid().equals(p.getUSER_ID())){

                            requestActive = true;
                        }
                    }*/

            }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference buscar = FirebaseDatabase.getInstance().getReference()
                    .child("Confirmacion")/*.child(itemID)*/;
            buscar.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot child : children){

                        Confirmacion c = child.getValue(Confirmacion.class);

                        if ("En_Progreso".equals(c.getCompletado())&& user.getUid().equals(c.getUSER_ID())){

                            requestActive = false;
                            sendData.setVisibility(View.INVISIBLE);
                            itemID = child.getKey();
                            mensaje.setVisibility(View.VISIBLE);


                        }

                        if ("Listo".equals(c.getCompletado()) && user.getUid().equals(c.getUSER_ID())){

                            requestActive= false;
                            confirmacion.setVisibility(View.VISIBLE);
                            itemID = child.getKey();
                            sendData.setVisibility(View.INVISIBLE);



                        }




                    }


                  /*  Log.v("Verficando5",dataSnapshot.exists()?"Si_Existe":"NoExiste");
                    Log.v("Verficando6",dataSnapshot.hasChildren()?"Si_Existe":"NoExiste");
                    Log.v("Verficando7",dataSnapshot.getValue()!= null?"Si_Existe":"NoExiste");


                Confirmacion c = dataSnapshot.getValue(Confirmacion.class);



                    if (dataSnapshot.exists()){

                        if ("En_Progreso".equals(c.getCompletado())){

                            requestActive = true;
                            sendData.setVisibility(View.INVISIBLE);

                        }

                    }
*/

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if(requestActive) {

            //DatabaseReference mUserGeo = mPedidoU.child("geofire");
          //mUserGeo.child("ID").setValue(user.getUid());
            GeoFire geoFire = new GeoFire(mPedidosRef.child("Localizacion"));
            geoFire.setLocation(itemID, new GeoLocation(location.getLatitude(),location.getLongitude()));
            //mUserGeo.child(user.getUid());


        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocation(location);
            }
        },2000);

    }

    public void requestUber(View view){

         /*user = mAuth.getCurrentUser();*/
         if(requestActive == false) {



             DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
             Date date = new Date();
             System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43




                        /*mUserData.setValue(user.getUid());*/

                      //  mPedidoU = mPedidosRef.push();
                        mPedidosRef.child(itemID).child("USER_ID").setValue(user.getUid());
                          mPedidosRef.child(itemID).child("Creado").setValue(dateFormat.format(date));
                             mPedidosRef.child(itemID).child("Conductor").setValue("Sin_Asignar");

             DatabaseReference aceptacion = FirebaseDatabase.getInstance().getReference()
                     .child("Confirmacion").child(itemID);

             aceptacion.child("USER_ID").setValue(user.getUid());
             aceptacion.child("Completado").setValue("Por_Atender");



             // mData.setValue(user.getDisplayName()); //Enviar data a Firebase
                  //   mData.setValue(user.getEmail()); //Enviar data a Firebase
                  //   mUserData.child("tipo").setValue("pasajero");
                     sendData.setText("Cancelar Taxista"); //Cambiar texto del Boton
                     requestActive = true;




         }

         else { //Si se ha pedido y se presiona otra vez el boton elimina el Perdido

             //Si se ha pedido y se presiona otra vez el boton elimina el Perdido

             sendData.setText("Conseguir Taxista"); //Cambiar texto del Boton
             requestActive = false;
             mPedidosRef.child(itemID).removeValue();
             mPedidosRef.child("Localizacion").child(itemID).removeValue();
             DatabaseReference borraAceptacion = FirebaseDatabase.getInstance().getReference()
                     .child("Confirmacion").child(itemID);
             borraAceptacion.removeValue();

             //Queary NoSql para conseguir mi Pedido
            // Query myCurrentUser = mPedidosRef.child("usuarios").child(user.getUid()).orderByChild("driverRequest").equalTo(null);
             //Query myCurrentUser = mPedidosRef.orderByChild("USER_ID").equalTo(user.getUid());

            /* myCurrentUser.removeEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                    String remover = dataSnapshot.getKey() ; //ELiminar Pedido
                     dataSnapshot.child(remover);

                 }

                 //Si se presenta algun error en el manejo de la base de datos este metodo permite
                 //obtener el error
                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });*/

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

          /*  DatabaseReference mUserGeo = mUserData.child("geofire");
            GeoFire geoFire = new GeoFire(mUserGeo);

            geoFire.setLocation("firabase-hq", new GeoLocation(location.getLatitude(),location.getLongitude()));*/

            updateLocation(location);

        }





    }

    public void listoConductor(View view){


        requestActive = false;
        mPedidosRef.child(itemID).removeValue();
        mPedidosRef.child("Localizacion").child(itemID).removeValue();
        DatabaseReference borraAceptacion = FirebaseDatabase.getInstance().getReference()
                .child("Confirmacion").child(itemID);
        borraAceptacion.removeValue();




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

