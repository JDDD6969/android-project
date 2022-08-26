package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemap.databinding.ActivityMapsActivitytestBinding;

public class MapsActivitytest extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private ActivityMapsActivitytestBinding binding;
    private static final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsActivitytestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkLocationPermissionAndEnableIt(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkLocationPermissionAndEnableIt(true);
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void checkLocationPermissionAndEnableIt(boolean on){
        if(ContextCompat.checkSelfPermission(MapsActivitytest.this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    MapsActivitytest.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder altDlgBuilder = new AlertDialog.Builder(MapsActivitytest.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("APP需要啟動功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapsActivitytest.this,new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                    }
                });
                altDlgBuilder.show();

                return;
            }else{
                ActivityCompat.requestPermissions(MapsActivitytest.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);

                return;
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
        LatLng TKU = new LatLng(25.174204, 121.447306);
        mMap.addMarker(new MarkerOptions().position(TKU).title("Marker in TKU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(TKU));
        mMap.setMinZoomPreference(14.0f);
    }


}