package com.example.gymbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class SelectLocActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    RelativeLayout searchRl;
    CameraUpdate update = null;
    Button proceedBtn;
    Geocoder geocoder;
    String lat=null;
    String lng=null;
    String address=null;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loc);

        searchRl = findViewById(R.id.searchRl);
        proceedBtn = findViewById(R.id.proceedBtn);
        progressBar = findViewById(R.id.progressBar);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);
        supportMapFragment.getMapAsync(this);
        requestPermissionLocation();

        Places.initialize(getApplicationContext(), getString(R.string.places_key));
        searchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(SelectLocActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("lat",lat);
                returnIntent.putExtra("lng",lng);
                returnIntent.putExtra("ahd",address);
                setResult(Activity.RESULT_OK,returnIntent);
                onBackPressed();
                finish();

            }
        });
    }


    private void requestPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(SelectLocActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(SelectLocActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1122);
        }
    }

    private void getCurrentLocation() {

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(SelectLocActivity.this, "" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                        //TODO: UI updates.
                    }
                }
            }
        };
        //  LocationServices.getFusedLocationProviderClient(HomeActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        LocationServices.getFusedLocationProviderClient(SelectLocActivity.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        if (location!=null){

                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            //currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions);
                        }


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SelectLocActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1122) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    progressBar.setVisibility(View.VISIBLE);
                        mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("");
                    mMap.addMarker(markerOptions);

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude) + "&key=AIzaSyCNe9fFGxRRWhHDCjMh6n0faXprtziilYs";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jObj = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONArray("address_components");

                                       // Intent intent = new Intent(getApplicationContext(), LocationDetailsActivity .class);

                                        for (int i = 0; i < jObj.length(); i++) {
                                            String componentName = new JSONObject(jObj.getString(i)).getJSONArray("types").getString(0);
                                            if (componentName.equals("postal_code") || componentName.equals("locality")) {
                                               // intent.putExtra(componentName, new JSONObject(jObj.getString(i)).getString("short_name"));
                                               String area=new JSONObject(jObj.getString(i)).getString("short_name");
                                               if (area!=null){
                                                   progressBar.setVisibility(View.GONE);
                                                   lat= String.valueOf(latLng.latitude);
                                                   lng= String.valueOf(latLng.longitude);
                                                   address=area;
                                                   Toast.makeText(SelectLocActivity.this, ""+area, Toast.LENGTH_SHORT).show();
                                               }
                                               else {
                                                   progressBar.setVisibility(View.GONE);
                                                   Toast.makeText(SelectLocActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                                               }

                                            }
                                        }



                                    } catch (JSONException e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SelectLocActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SelectLocActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            int x = 1;
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 & resultCode == RESULT_OK) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            update = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10f);

            lat=String.valueOf(place.getLatLng().latitude);
            lng=String.valueOf(place.getLatLng().longitude);
            address=String.valueOf(place.getAddress());



            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap=googleMap;
                    mMap.clear();
                    googleMap.animateCamera(update);
                    LatLng latLng = place.getLatLng();
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(place.getAddress());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    googleMap.addMarker(markerOptions);
                }
            });

        }
        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }






}