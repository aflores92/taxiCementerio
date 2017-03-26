package com.andresflores.taxicementerio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class Main4Activity extends Activity  {

    private FirebaseAuth mAuth;
    private EditText textEmail;
    private EditText textPass;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserRef = mRootRef.child("usuario");
    private String tipo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);


        progressDialog = new ProgressDialog(this);
        textEmail = (EditText) findViewById(R.id.etxt_email);
        textPass = (EditText) findViewById(R.id.etxt_password);
        btnRegister = (Button) findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });


    }



    private void doLogin() {
        String email = textEmail.getText().toString().trim();
        String password = textPass.getText().toString().trim();






        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) )  {
            progressDialog.setMessage("Ingresando , Espere Por Favor");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();

                                DatabaseReference mData = mUserRef.child(user_id);
                                mData.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {




                                            Map <String, String> map = (Map)dataSnapshot.getValue();
                                           // Log.v("E_VALUE","DATA : " + dataSnapshot.getValue());
                                        // Map<String,String> map = dataSnapshot.getValue(Map.class);
                                             tipo = map.get("Tipo");


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                if (tipo.equals("Pasajero")) {

                                    Toast.makeText(Main4Activity.this, "Ingreso Correcto Pasajero", Toast.LENGTH_SHORT).show();


                                    btnRegister.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Main4Activity.this, MapsActivity.class);
                                            startActivity(intent);

                                        }
                                    });

                                }

                                else if(tipo.equals("Conductor"))
                                    {


                                        Toast.makeText(Main4Activity.this, "Ingreso Correcto Conductor", Toast.LENGTH_SHORT).show();


                                        btnRegister.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(Main4Activity.this, Main5Activity.class);
                                                startActivity(intent);

                                            }
                                        });



                                }

                            } else
                                Toast.makeText(Main4Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}