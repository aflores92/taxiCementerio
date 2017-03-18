package com.andresflores.taxicementerio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {


    private EditText mNameField;
    private EditText mEmailFiedl;
    private EditText mPasswordField;
    private Button mRegisterButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private TextView mTextLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        mNameField = (EditText) findViewById(R.id.etxt_name);
        mEmailFiedl = (EditText) findViewById(R.id.etxt_email);
        mPasswordField = (EditText) findViewById(R.id.etxt_password);
        mRegisterButton = (Button) findViewById(R.id.btn_register);
        mProgress = new ProgressDialog(this);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            } });


        mLoginButton = (Button) findViewById(R.id.button3);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, Main4Activity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        String email = mEmailFiedl.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Registering, please wait...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();


                                Toast.makeText(Main2Activity.this, user_id, Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

        }

    }


    }
