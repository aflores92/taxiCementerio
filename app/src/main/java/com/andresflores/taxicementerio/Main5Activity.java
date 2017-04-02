package com.andresflores.taxicementerio;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main5Activity extends Activity implements LocationListener {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPedidosRef = mRootRef.child("Pedidos");
    private DatabaseReference mPedidoU = mPedidosRef;
    ListView listView;
    ArrayList<String> listViewContent;
    ArrayAdapter arrayAdapter;
    private LocationManager locationManager;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> usuario;
    ArrayList<Double> latitud;
    ArrayList<Double> longitud;
    ArrayList<Location> posiciones;
    Location location;
    ArrayList<Double> distancia;
    Location pasajeroLocation;
    ArrayList<String> transformacion;
    boolean sinasignar = false;
    private ValueEventListener userValueListener;
    private ArrayList<Pedido> pedidos = new ArrayList<>();
    private Adapter adapter;
    private Set<String> userIdsWithListener = new HashSet<>();
    ArrayList<String> userIds = new ArrayList<>();
    boolean puerta = false;
    final Location locacionC =  new Location("locacionC") ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        usuario = new ArrayList<String>();
        latitud = new ArrayList<Double>();
        longitud = new ArrayList<Double>();
        posiciones = new ArrayList<Location>();
        distancia = new ArrayList<Double>();
        transformacion = new ArrayList<String>();


        listView = (ListView) findViewById(R.id.listView);

        listViewContent = new ArrayList<String>();
        //listViewContent.add("Prueba");
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
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        locacionC.setLatitude(location.getLatitude());
        locacionC.setLongitude(location.getLongitude());

        setupListener();
        updateLocation();

      /*  if (puerta){

            verificandoPuerta();
        }

*/






        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Main5Activity.this,MapsActivity2.class);
                i.putExtra("usuarioLatitud",location.getLatitude());
                i.putExtra("usuarioLongitud",location.getLongitude());
                i.putExtra("pasajeroLatitud",posiciones.get(position).getLatitude());
                i.putExtra("pasajeroLongitud",posiciones.get(position).getLongitude());
                i.putExtra("data",data.get(position));
                startActivity(i);


            }
        });




    }

   /* private void verificandoPuerta() {


        for (Location posicion : posiciones ) {
            Log.i("Posicion  Saliendo ",posicion.toString() );
        }

        Location conductorLocation = new Location("LocationB");
        conductorLocation.setLatitude(location.getLatitude());
        conductorLocation.setLongitude(location.getLongitude());

        for (Location posicion: posiciones){

            transformacion.add(

                    String.valueOf(
                            Math.round(((posicion.distanceTo(conductorLocation))*10)/10)) + " Metros");

        }


        listViewContent.addAll(transformacion);
        arrayAdapter.notifyDataSetChanged();


    }*/


    private void setupListener() {

        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Pedido p = dataSnapshot.getValue(Pedido.class);
                String keyPedidos = dataSnapshot.getKey();
                Log.v("prueba5",keyPedidos);
                if (p.getConductor().equals("Sin_Asignar")){

                    pedidos.add(p);
                    userIds.add(keyPedidos);
                    puerta = true;

                   // listViewContent.add(keyPedidos);
                 //   arrayAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



    }





    public void updateLocation() {




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Pedidos").child("Localizacion");
       // ref.child("geofire");
        GeoFire geoFire = new GeoFire(ref);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), 0.6 );
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {



                Log.v("llave",key);
                //userIds.add(key);
                addUserListener(key);

               if (userIds.contains(key))
                {
                    pasajeroLocation = new Location("LocationA");
                    pasajeroLocation.setLatitude(location.latitude);
                    pasajeroLocation.setLongitude(location.longitude);
                    posiciones.add(pasajeroLocation);
                    data.add(key);

/*
                    Location conductorLocation = new Location("LocationB");
                    conductorLocation.setLatitude();
                    conductorLocation.setLongitude();*/


                 if (listViewContent.size()<=2)

                 {

                    listViewContent.add(

                            String.valueOf(
                                    Math.round(((pasajeroLocation.distanceTo(locacionC))*10)/10)) + " Metros");

                   // listViewContent.addAll(transformacion);
                    arrayAdapter.notifyDataSetChanged();

                    }


                }


/*
                for (Location posicion : posiciones ) {
                    Log.i("Posicion Informacion",posicion.toString() );

                }*/



            }



            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }

            private void  addUserListener (String userId){
                Log.v("usuariosIdLlaves",userId);
                mPedidosRef.child(userId).addValueEventListener(userValueListener);
                userIdsWithListener.add(userId);


            }


        });


        //puerta = false;

        /*ref.addChildEventListener(new ChildEventListener() {
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
        });*/

        /*for (String datos : data){

            Log.v("Prueba",datos);

        }

*/


      //  DatabaseReference mUserGeo = mPedidoU.orderByChild();

       // GeoFire geoFire = new GeoFire(mUserGeo);

       // geoFire.setLocation("Localizacion", new GeoLocation(location.getLatitude(),location.getLongitude()));
        /*geoFire.getLocation("Localizacion", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    public void onLocationChanged(Location location) {

        updateLocation();


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

   /* public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radio de la tierra en  kilómetros
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);



    }*/




}

/*

mPedidosRef.child(key).orderByChild("Conductor").addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {

        String key = dataSnapshot.getKey();

        //  Map<String,String> map = dataSnapshot.getValue();
        //Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
        Map <String, String> map = (Map)dataSnapshot.getValue();
        String conductor = map.get("Conductor");
        String creado = map.get("Creado");
        String user_ide = map.get("USER_ID");

        Log.v("InformacionNueva",conductor);
        Log.v("llave",key);

        if ( conductor.equals("Sin_Asignar")){

        sinasignar = true;


        }

        else {

        sinasignar=false;

        }

        }

@Override
public void onCancelled(DatabaseError databaseError) {

        }
        });

        if (sinasignar){


        pasajeroLocation = new Location("LocationA");
        pasajeroLocation.setLatitude(location.latitude);
        pasajeroLocation.setLongitude(location.longitude);
        posiciones.add(pasajeroLocation);

        for (Location posicion : posiciones ) {
        Log.i("Posicion Informacion",posicion.toString() );

        }
        //listViewContent.add(key);
        // arrayAdapter.notifyDataSetChanged();

        Log.i("prueba",String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

        }
*/
