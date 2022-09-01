package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.LocationSource;
import com.example.googlemap.databinding.ActivityMapsActivitytestBinding;

public class MapsActivitytest extends FragmentActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener, LocationSource {

    private GoogleMap mMap;
    private ActivityMapsActivitytestBinding binding;
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;
    private LocationManager mLocationMgr;
    private OnLocationChangedListener mLocationChangedListener;

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

        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mMap != null) {
            checkLocationPermissionAndEnableIt(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        checkLocationPermissionAndEnableIt(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION) {  //送出之編號是否和要求之權限編號相同
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissionAndEnableIt(true);
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setLocationSource(this);

        if(ContextCompat.checkSelfPermission(MapsActivitytest.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location location = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null)
                location = mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null){
                Toast.makeText(MapsActivitytest.this ,"成功取得上一次定位", Toast.LENGTH_SHORT).show();
                onLocationChanged(location);
            }else
                Toast.makeText(MapsActivitytest.this, "沒有上一次訂位資料", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(mLocationChangedListener != null)
            mLocationChangedListener.onLocationChanged(location);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String s,int i,Bundle bundle){
        String str = s;
        switch (i){
            case LocationProvider.OUT_OF_SERVICE:
                str += "定位無法使用";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                str += "暫時無法定位";
                break;
        }

        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String s){
        Toast.makeText(MapsActivitytest.this, s + "定位功能開啟", Toast.LENGTH_LONG).show();
        checkLocationPermissionAndEnableIt(true);
    }

    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
        mLocationChangedListener = onLocationChangedListener;
        checkLocationPermissionAndEnableIt(true);
        Toast.makeText(MapsActivitytest.this, "map的my-location layer已經啟用", Toast.LENGTH_LONG).show();
    }

    @Override
    public void deactivate() {
        mLocationChangedListener = null;
        checkLocationPermissionAndEnableIt(false);
        Toast.makeText(MapsActivitytest.this, "map的my-location layer已經關閉", Toast.LENGTH_LONG).show();
    }

    private void checkLocationPermissionAndEnableIt(boolean on){

        if(ContextCompat.checkSelfPermission(MapsActivitytest.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){ //未取得使用者全限
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    MapsActivitytest.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder altDlgBuilder = new AlertDialog.Builder(MapsActivitytest.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("APP需要啟動定位功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ActivityCompat.requestPermissions(MapsActivitytest.this,
                                new String[]{
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

        if(on){
            if(mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                mLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
                Toast.makeText(MapsActivitytest.this, "使用GPS定位", Toast.LENGTH_LONG).show();
            }else
                if(mLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,this);
                    Toast.makeText(MapsActivitytest.this, "使用網路定位", Toast.LENGTH_LONG).show();
                }
                }else {
            mLocationMgr.removeUpdates(this);
            Toast.makeText(MapsActivitytest.this, "定位功能已經停用", Toast.LENGTH_LONG).show();
        }
    }
}