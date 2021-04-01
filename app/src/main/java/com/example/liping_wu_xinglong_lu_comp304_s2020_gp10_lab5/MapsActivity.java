package com.example.liping_wu_xinglong_lu_comp304_s2020_gp10_lab5;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback, OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {
    private static final LatLng CentennialCollege = new LatLng( 43.786048, -79.226284);
    private static final LatLng SenecaCollege = new LatLng(43.796176 , -79.348562);
    private static final LatLng GeorgeCollege = new LatLng(43.651463 , -79.370312);

    private Marker mCollege;

    private static final String TAG ="GROUP10Lab6 Map" ;
    private GoogleMap mMap;
    private TextView mTextView;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private Marker currentLocationMark;

    //widgets
    private EditText mSearchText;
    HashMap<String, String> markerMap = new HashMap<String, String>(); //markid = > action id
    HashMap<String, String> markerUrl = new HashMap<String, String>();     // action id =>  makerUrl                                                        //action id = > website url
    //vars
    private FusedLocationProviderClient mFusedLocationClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);
       setTitle("LWu&XLuCOMP304_G10Lab05");

       mTextView = findViewById(R.id.myLocationText);

       mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
       // Obtain the SupportMapFragment and request the Google Map object.
       SupportMapFragment mapFragment =
               (SupportMapFragment) getSupportFragmentManager()
                       .findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);

       mSearchText = (EditText) findViewById(R.id.input_search);

   }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest=new LocationRequest();
        //mLocationRequest.setInterval(10000);
        //mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);

        // Set a listener for map click.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("MapsActivity::", "onMapClick: Latitude: "+ latLng.latitude + " / Longtitude " + latLng.longitude);
             /*   if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE ){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Toast.makeText(getApplicationContext(),  "change to Normal View" , Toast.LENGTH_SHORT).show();
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    Toast.makeText(getApplicationContext(), " change to satellite view " , Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        init();
        initCollegeData();
    }

    private void initCollegeData() {
        Drawable schoolMarker = getResources().getDrawable(R.drawable.school);
        BitmapDescriptor schoolIcon = getMarkerIconFromDrawable(schoolMarker);
        ArrayList<LatLng> collegePosition=new ArrayList<LatLng>(Arrays.asList(CentennialCollege, SenecaCollege, GeorgeCollege));
        ArrayList<String> collegeTitle=new ArrayList<String>(Arrays.asList("Centennial College", "Seneca College", "George College"));
        ArrayList<String> collegeAction=new ArrayList<String>(Arrays.asList("action_centennial", "action_seneca", "action_george"));
        ArrayList<String> collegeUrl=new ArrayList<String>(Arrays.asList("https://www.centennialcollege.ca/", "https://www.senecacollege.ca/","https://www.georgebrown.ca/"));
        for(int i= 0; i < collegePosition.size(); i++){

            mCollege = mMap.addMarker(new MarkerOptions()
                    .position(collegePosition.get(i))
                    .title(collegeTitle.get(i))
                    .snippet("This is an Ontario College.")
                    .icon(schoolIcon));
            String id = mCollege.getId();
            markerMap.put(id, collegeAction.get(i));

            mCollege.showInfoWindow();
            mCollege.setTag(0);

            markerUrl.put(collegeAction.get(i),collegeUrl.get(i) );
        }
    }
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(mFusedLocationClient!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                updateTextView(location);
                lastLocation=location;
                if(currentLocationMark !=null){
                    currentLocationMark.remove();
                }
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                currentLocationMark =mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION=99;
    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Location Permission Needed")
                                                        .setMessage("This app needs the Location permission")
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                ActivityCompat.requestPermissions(MapsActivity.this,
                                                                        new String[]{ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
                                                            }
                                                        }).create().show();
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                        getLastLocation();
                    }
                }
                else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private void getLastLocation() {
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                        == PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            updateTextView(location);
                        }
                    });
        }
    }

    private void updateTextView(Location location) {
        String latLongString = "No location found";
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "\nLatitude: " + lat + "\nLongtitude: " + lng;
        }

        String address = geocodeLocation(location);
        Log.d(TAG, "updateTextView: address: " + address + ", location="+location);

        String outputText = "Your Current Location is: " + latLongString ;
        if (!address.isEmpty())
            outputText +=  address;
        Log.d(TAG, "updateTextView: outputText: " + outputText);

        mTextView.setText(outputText);
    }

    private String geocodeLocation(Location location) {
        String returnString = "";

        if (location == null) {
            return returnString;
        }

        if (!Geocoder.isPresent()) {
            return returnString;
        } else {
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses
                        = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(),
                        1); // One Result
                Log.d(TAG, "geocodeLocation: gc.getFromLocation response: " + addresses);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    Log.d(TAG, "geocodeLocation: " + address.toString());
                    returnString += "\n"+ address.getLocality() +", " + address.getAdminArea() +", " +address.getPostalCode() + ", " + address.getCountryName();
                    Log.d(TAG, "geocodeLocation: returnString: " + returnString);

                }
              //  returnString = sb.toString();
            } catch (IOException e) {
                Log.d(TAG, "geocodeLocation: got a exception when query location : " + e);
            }
            return returnString;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle()+" Selected" , Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.type_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.type_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.type_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.type_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //init search
    private  void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });
    }
    private void geoLocate(){
        String searchString = mSearchText.getText().toString();

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try{
            addresses = gc.getFromLocationName(searchString, 99);
            Log.d(TAG, "geoLocate: " +  "You are looking for: "+searchString);
            Log.d(TAG, "geoLocate: " +  "Addresses size: "+addresses.size());
            Toast.makeText(this, addresses.size() + " results found", Toast.LENGTH_SHORT ).show();
        }catch (IOException e){
            Log.d(TAG, "geoLocate: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT ).show();
        }

        if(addresses.size() > 0){
            int count = Math.min(3, addresses.size() ) ;
            for(int i = 0; i <count; i++){
                Address address = addresses.get(i);
                Log.d(TAG, "found address by searchtext: " + address);
                LatLng latLng=new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(searchString);
                markerOptions.snippet(address.getAddressLine(0) + "\n" + address.getUrl());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                currentLocationMark = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
                currentLocationMark.showInfoWindow();
            }

        } else {
            Toast.makeText(this, searchString + " not found!" , Toast.LENGTH_SHORT ).show();
        }
    }

    //search button eventhandler
    public void SearchColleges(View view) {
        geoLocate();
    }

    //marker click handler
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: " + mMap.getMapType() );

            if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE ){
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this,marker.getTitle() + " is normal view " , Toast.LENGTH_SHORT).show();
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this,marker.getTitle() + " is satellite view " , Toast.LENGTH_SHORT).show();
            }


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    //window click handler
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick: on marker " + marker.getTitle());
        String actionId = markerMap.get(marker.getId());
        String locationUrl = markerUrl.get(actionId);
        Log.d(TAG, "onInfoWindowClick:  actionId: " +actionId+ ", locationUrl=" + locationUrl);
        //locationUrl exist in our seeddata
        if (locationUrl != null){
            Toast.makeText(this,"will open website " + locationUrl , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl)));
            return;
        }

        //locationUrl not exist in our seeddata, then try to get from google api by Geocoder
        // currently the Url is null for free services
        Log.d(TAG, "onInfoWindowClick: " + "no school web available.");
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses
                    = gc.getFromLocation(marker.getPosition().latitude,
                    marker.getPosition().longitude,
                    1); // One Result
            Log.d(TAG, "onInfoWindowClick: gc.getFromLocation response: " + addresses);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d(TAG, "onInfoWindowClick : " + address.toString());
                locationUrl = address.getUrl();
                Log.d(TAG, "onInfoWindowClick: locationUrl found in google api: " + locationUrl);
                if (locationUrl != null ) {
                    Toast.makeText(this,"will open website " + locationUrl , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl)));
                    return;
                }

            }
        } catch (IOException e) {
            Log.d(TAG, "onInfoWindowClick: got a exception when query address by latitude and longitude : " + e);
        }
        Toast.makeText(this,"no website found for this location" , Toast.LENGTH_SHORT).show();
        return;

    }
}
