package com.arnab.trackingsystem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.arnab.trackingsystem.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseDatabase db;
    private LocationManager locationManager;

    private static final int MIN_TIME = 1000;
    private static final int MIN_DISTANCE = 1;

    private Marker marker;

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

        readLocation();

    }

    private void readLocation() {
        db.getReference("driver").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        Driver driver= snapshot.getValue(Driver.class);
                        if(driver!=null){
                            Log.e("debug",driver.getLatitude()+", "+ driver.getLongitude());
                            marker.setPosition(new LatLng(driver.getLatitude(),driver.getLongitude()));
                            marker.setTitle(driver.getName());
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(driver.getLatitude(),driver.getLongitude())));
                            //mMap.setMinZoomPreference(12);
                            mMap.resetMinMaxZoomPreference();

                        }
                    }catch (Exception e){
                        Toast.makeText(DriverMapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLocationUpdates() {
        if(locationManager!=null){
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
        }
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
        LatLng defaults = new LatLng(22.85935962, 91.09831162);
        marker=mMap.addMarker(new MarkerOptions().position(defaults).title("defaults"));
        mMap.setMinZoomPreference(12);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaults));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
            saveLocation(location);
        }else{
            Toast.makeText(this, "No Location Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(Location location) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){

            FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String state= (String) snapshot.getValue();

                    db.getReference(state).child(FirebaseAuth.getInstance().getUid()).child("longitude").setValue(location.getLongitude());
                    db.getReference(state).child(FirebaseAuth.getInstance().getUid()).child("latitude").setValue(location.getLatitude());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }

    }
}