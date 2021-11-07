package com.arnab.trackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private ImageView logout;
    private ImageView mapStart;
    private TextView mapText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout=findViewById(R.id.logout);
        mapStart=findViewById(R.id.go_map);
        mapText=findViewById(R.id.maptext);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, AuthenticationActivity.class));
                finish();
            }
        });

        mapStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String state= (String) snapshot.getValue();
                        Log.e("debug",state);
                        if(state.equals("driver")){
                            startActivity(new Intent(HomeActivity.this, DriverMapsActivity.class));
                        }else{
                            startActivity(new Intent(HomeActivity.this, UserMapsActivity.class));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}