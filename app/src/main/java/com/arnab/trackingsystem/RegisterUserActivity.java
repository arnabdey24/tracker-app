package com.arnab.trackingsystem;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Activity for User Registration.
 *
 */
public class RegisterUserActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton driver, student;
    private EditText email, password, password1, name, phone;
    private Button register;
    private int tmp = -1;
    private FirebaseAuth auth;
    private ImageView goBack;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        database = FirebaseDatabase.getInstance();
        radioGroup = findViewById(R.id.radiogrp);
        driver = findViewById(R.id.radioDriver);
        student = findViewById(R.id.radioStudent);
        email = findViewById(R.id.resEmail);
        password = findViewById(R.id.resPass);
        password1 = findViewById(R.id.resConfirmPass);
        name = findViewById(R.id.resName);
        phone = findViewById(R.id.resPhone);
        register = findViewById(R.id.resbutton);
        goBack = findViewById(R.id.go_back);
        auth = FirebaseAuth.getInstance();

        student.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tmp = 2;
                Log.e("debug", String.valueOf(tmp));
            }
        });
        driver.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tmp = 1;
                Log.e("debug", String.valueOf(tmp));
            }
        });

        register.setOnClickListener(v -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            String txt_password1 = password1.getText().toString();
            String txt_name = name.getText().toString();
            String txt_phn = phone.getText().toString();

            if (tmp == -1) {
                Toast.makeText(RegisterUserActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_password1) || TextUtils.isEmpty(txt_name)
                    || TextUtils.isEmpty(txt_phn)) {
                Toast.makeText(RegisterUserActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            } else if (!txt_password.equals(txt_password1)) {
                Toast.makeText(RegisterUserActivity.this, "Password not matched!", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6) {
                Toast.makeText(RegisterUserActivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("debug", "calling");
                register(txt_email,txt_password,txt_name,txt_phn);
            }
        });

        goBack.setOnClickListener(v -> {
            startActivity(new Intent(RegisterUserActivity.this, AuthenticationActivity.class));
            finish();
        });

    }


    /**
     * register by email and password.
     *
     * @param email: email
     * @param password: password
     */
    private void register(String email, String password, String name,String phone) {
        Log.e("debug", "registering");
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterUserActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterUserActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                HashMap<String,Object> data=new HashMap<>();
                data.put("name",name);
                data.put("email",email);
                data.put("phone",phone);
                data.put("longitude",5);
                data.put("latitude",5);


                if(tmp==1){
                    database.getReference(FirebaseAuth.getInstance().getUid()).setValue("driver");
                    database.getReference("driver").child(FirebaseAuth.getInstance().getUid()).setValue(data);
                }
                else{
                    database.getReference(FirebaseAuth.getInstance().getUid()).setValue("student");
                    database.getReference("student").child(FirebaseAuth.getInstance().getUid()).setValue(data);
                }

                startActivity(new Intent(RegisterUserActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterUserActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterUserActivity.this, AuthenticationActivity.class));
                finish();
            }
        });
    }
}
