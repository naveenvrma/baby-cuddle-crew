package com.example.babycuddlecrew.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.logic.LocationAddress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = "SelectLocationActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean isLocationEnabled;
    private double lati, longi, fromLat, fromLng;
    private LatLng selectedLatLng;
    private int zoomLevel;
    Circle circle;

    private GoogleMap mMap;
    private AlertDialog dialog;
    ProgressDialog loadingdialog;

    Button addLocationBtn;
    String locationAddress, comingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                if (!isLocationEnabled()) {
                    locationRequestDialog();
                }
            }
        } else {
            if (!isLocationEnabled()) {
                locationRequestDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        isLocationEnabled = true;
                        if (mMap == null)
                            initMap();
                        else if (mMap != null) {
                            mMap.clear();
                            moveCamera(new LatLng(lati, longi), "My Location", false);
                        }
                        //Request location updates:
                    }

                } else {
                    isLocationEnabled = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapClickListener(this);
        if (!comingFrom.equals("PostDetailActivity")) {
            getCurrentLocation();
        } else {
            //show saved location
            if (mMap == null)
                initMap();
            else {
                mMap.clear();
                moveCamera(new LatLng(fromLat, fromLng), "My Location", false);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (!comingFrom.equals("PostDetailActivity")) {
            if (mMap != null) {
                mMap.clear();
                try {
                    selectedLatLng = latLng;
                    lati = latLng.latitude;
                    longi = latLng.longitude;
                    moveCamera(new LatLng(lati, longi), "My Location", false);
                } catch (SecurityException e) {
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
                }
            }
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionDialoge();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            if (mMap == null)
                initMap();
            else if (mMap != null && !comingFrom.equals("PostDetailActivity")) {
                mMap.clear();
                moveCamera(new LatLng(lati, longi), "My Location", false);
            }
            return true;
        }
    }

    private void locationRequestDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showPermissionDialoge() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Location Enable")
                    .setMessage("Please Turn On Your Location")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(SelectLocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isLocationEnabled && mMap == null) {
            initMap();
        } else if (isLocationEnabled && mMap != null && !comingFrom.equals("PostDetailActivity")) {
            mMap.clear();
            moveCamera(new LatLng(lati, longi), "My Location", false);
        }
        return isLocationEnabled;
    }

    private void getBundleExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("from")) {
                comingFrom = bundle.getString("from");
                fromLat = bundle.getDouble("fromLat");
                fromLng = bundle.getDouble("fromLng");
            }
        }
    }

    private void initViews() {
        getBundleExtras();
        //getting height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        addLocationBtn = findViewById(R.id.btn_Location);
        addLocationBtn.setVisibility(comingFrom.equals("PostDetailActivity") ? View.GONE : View.VISIBLE);
        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (locationAddress != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", locationAddress);
                    returnIntent.putExtra("lat", selectedLatLng.latitude);
                    returnIntent.putExtra("lng", selectedLatLng.longitude);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                    Toast.makeText(SelectLocationActivity.this, "Please select your location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadingdialog = new ProgressDialog(this);

        dialog = new AlertDialog.Builder(this).setTitle("Enable Location")
                .setMessage("Location access is required to show your location.")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);

    }

    private void initMap() {
        Log.e(TAG, "" + isServiceOk());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (isServiceOk()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }
    }

    public boolean isServiceOk() {
        Log.d(TAG, "is ServiceOk: checking Google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SelectLocationActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "is ServiceOk: checking Google services is success working..");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "is ServiceOk: an error occured..");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SelectLocationActivity.this, available, 9001);
            dialog.show();

        } else {
            Log.d(TAG, "You can't make map request");
        }
        return false;
    }

    private void getCurrentLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (isLocationEnabled) {

                if (!loadingdialog.isShowing())
                    loadingdialog.show();
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            loadingdialog.dismiss();

                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            lati = currentLocation.getLatitude();
                            longi = currentLocation.getLongitude();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "My Location", true);


                        } else {
                            loadingdialog.dismiss();
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(SelectLocationActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            loadingdialog.dismiss();
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(final LatLng latLng, String title, boolean isDropAnomationEnable) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        LocationAddress locationAddress = new LocationAddress();
        selectedLatLng = latLng;
        locationAddress.getAddressFromLocation(latLng.latitude, latLng.longitude,
                getApplicationContext(), new GeocoderHandler());
        if (title.equals("My Location")) {

            circle = mMap.addCircle(new CircleOptions().center(latLng).radius(3).strokeColor(getResources().getColor(R.color.colorAccent)).fillColor(getResources().getColor(R.color.colorAccent)).strokeWidth(5));
            circle.setVisible(false);

            if (isDropAnomationEnable) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel(circle)), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        addMarker(latLng, false, true);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel(circle)));
                addMarker(latLng, false, false);
            }

        }

    }

    protected Marker addMarker(LatLng position, boolean draggable, boolean isDropAnomationEnable) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(draggable);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        markerOptions.position(position);
        Marker pinnedMarker = mMap.addMarker(markerOptions);
        if (isDropAnomationEnable)
            startDropMarkerAnimation(pinnedMarker);
        else
            circle.setVisible(false);
        return pinnedMarker;
    }

    private void startDropMarkerAnimation(final Marker marker) {

        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (500 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                } else {

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // circleRadarAnimation(marker);
                            circle.setVisible(false);
                        }
                    }, 200);

                }
            }
        });
    }

    public int getZoomLevel(Circle circle) {
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    Toast.makeText(SelectLocationActivity.this, "" + locationAddress, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    locationAddress = null;
            }
        }
    }
}
