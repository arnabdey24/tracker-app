package com.arnab.trackingsystem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.arnab.trackingsystem.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.arnab.trackingsystem.databinding.ActivityUserMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseDatabase db;
    private LocationManager locationManager;

    private static final int MIN_TIME = 1000;
    private static final int MIN_DISTANCE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=FirebaseDatabase.getInstance();
        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getLocationUpdates();

        //readLocations();

    }

    private void readLocations() {
        db.getReference("driver").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        Driver driver= snapshot.getValue(Driver.class);
                        if(driver!=null){
                            Log.e("debug",driver.getLatitude()+", "+ driver.getLongitude());
                            //marker.setPosition(new LatLng(driver.getLatitude(),driver.getLongitude()));
                            //marker.setTitle(driver.getName());
                            //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(driver.getLatitude(),driver.getLongitude())));
                            //mMap.setMinZoomPreference(12);
                            mMap.resetMinMaxZoomPreference();

                        }
                    }catch (Exception e){
                        Toast.makeText(UserMapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLocationUpdates() {
        /*if(locationManager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                }else{
                    Toast.makeText(this, "No Location provider Available", Toast.LENGTH_SHORT).show();
                }
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }*/
        HashMap<String,Marker> markers=new HashMap<>();

        db.getReference("driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Driver driver=child.getValue(Driver.class);
                    Log.e("debug",child.getKey());
                    Log.e("debug",child.toString());
                    if(!markers.containsKey(driver.getPhone())){
                        Marker marker=mMap.addMarker(new MarkerOptions().position(
                                new LatLng(driver.getLatitude(),driver.getLongitude())
                                ).title(driver.getName()));
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
                        markers.put(driver.getPhone(), marker);
                    }else{
                        markers.get(driver.getPhone()).setPosition(new LatLng(driver.getLatitude(),driver.getLongitude()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }else{
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //mMap.setMinZoomPreference(12);
        LatLng defaults = new LatLng(22.85935962, 91.09831162);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaults));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}