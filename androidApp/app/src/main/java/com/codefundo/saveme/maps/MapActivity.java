package com.codefundo.saveme.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.codefundo.saveme.KillableRunnable;
import com.codefundo.saveme.R;
import com.codefundo.saveme.SaveMe;
import com.codefundo.saveme.auth.LoginActivity;
import com.codefundo.saveme.models.CampData;
import com.codefundo.saveme.models.VictimData;
import com.codefundo.saveme.models.VolunteerData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private final static int REQUEST_CODE_LOCATION_PERMISSION = 123;
    private final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private MobileServiceClient mClient;
    private MobileServiceList campLocations;
    private BitmapDescriptor victimBitmap;
    private BitmapDescriptor foodBitmap;
    private BitmapDescriptor hospitalBitmap;
    private BitmapDescriptor volunteerBitmap;
    private HashMap<String, Marker> lastLocationVictimHashmap = new HashMap<>();
    private HashMap<String, Marker> lastLocationVolunteerHashmap = new HashMap<>();
    private MobileServiceList victimLocations;
    private MobileServiceList volunteerLocations;
    private Handler handlerVictims;
    private Handler handlerVolunteers;
    private KillableRunnable runnerVictims;
    private KillableRunnable runnerVolunteers;
    private String type = ""; //"victim", "volunteer", "user"
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        prepareBitmaps();
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
        mapReady();

    }

    private void mapReady() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                askLocationPermissions();
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng coordinate = new LatLng(latitude, longitude);
            Toast.makeText(this, mMap.getMaxZoomLevel() + "", Toast.LENGTH_LONG).show();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(coordinate)
                    .zoom(16f)
                    .tilt(50)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        buildGoogleApiClient();
        fetchLocationOfVictims();
        fetchLocationOfCamps();
        fetchLocationOfVolunteers();
        mMap.setOnMarkerClickListener(this);
    }

    private void prepareBitmaps() {
        Drawable drawable = getDrawable(R.drawable.marker_victim);
        assert drawable != null;
        victimBitmap = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap());
        drawable = getDrawable(R.drawable.marker_food);
        assert drawable != null;
        foodBitmap = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap());
        drawable = getDrawable(R.drawable.marker_hospital);
        assert drawable != null;
        hospitalBitmap = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap());
        drawable = getDrawable(R.drawable.marker_volunteer);
        assert drawable != null;
        volunteerBitmap = BitmapDescriptorFactory.fromBitmap(((BitmapDrawable) drawable).getBitmap());
    }



    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                askLocationPermissions();
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static void launchMapActivity(Context mContext, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        Intent intent = new Intent(mContext, MapActivity.class);
        intent.putExtra("MAP_INTENT", bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (type.matches("victim"))
            sendLocationToDbVictim(latLng);
        else if (type.matches("volunteer"))
            sendLocationToDbVolunteer(latLng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16f)
                .tilt(50)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void sendLocationToDbVolunteer(LatLng latLng) {
        //[:TODO:] get volunteer's actual status here
        if ("working".matches("working")) {
            final MobileServiceTable<VolunteerData> table = mClient.getTable(VolunteerData.class);
            ListenableFuture<MobileServiceList<VolunteerData>> listenableFuture = table.where().field("id").eq(LoginActivity.getDeviceIMEI(this)).execute();
            Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VolunteerData>>() {
                @Override
                public void onSuccess(MobileServiceList<VolunteerData> result) {
                    VolunteerData volunteerData = new VolunteerData();
                    if (result.getTotalCount() > 0) {
                        volunteerData = result.get(0);
                        volunteerData.setCurrentLat(latLng.latitude);
                        volunteerData.setCurrentLong(latLng.longitude);
                        table.update(volunteerData);
                        Log.e("location:", "existing Volunteer");
                    } else {
                        volunteerData.setId(LoginActivity.getDeviceIMEI(MapActivity.this));
                        volunteerData.setAzureId(LoginActivity.getCurrentUserUniqueId(MapActivity.this));
                        volunteerData.setCurrentLat(latLng.latitude);
                        volunteerData.setCurrentLong(latLng.longitude);
                        //[:TODO:] replace "working" by actual status of volunteer
                        volunteerData.setCurrentStatus("working");
                        table.insert(volunteerData);
                        Log.e("location:", "new Volunteer");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("Tag", t.getMessage());
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapReady();
                } else {
                    askLocationPermissions();
                }
                break;
            }
        }
    }

    private void fetchLocationOfVictims() {
        handlerVictims = new Handler();
        runnerVictims = new KillableRunnable() {
            @Override
            public void doWork() {
                mClient = SaveMe.getAzureClient(MapActivity.this);
                MobileServiceTable<VictimData> table = null;
                if (mClient != null) {
                    table = mClient.getTable(VictimData.class);
                }

                ListenableFuture<MobileServiceList<VictimData>> listenableFuture = table.where().field("status").eq("danger").execute();
                Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VictimData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<VictimData> result) {
                        victimLocations = result;
                        for (Object victimData : victimLocations) {
                            VictimData data = (VictimData) victimData;

                            if (lastLocationVictimHashmap.containsKey(data.getId())) {
                                Marker marker = lastLocationVictimHashmap.get(data.getId());
                                marker.setPosition(new LatLng(data.getCurrentLat(), data.getCurrentLong()));
                            } else {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(data.getCurrentLat(), data.getCurrentLong()))
                                        .title("Victim")
                                        .snippet(data.getId())
                                        .icon(victimBitmap);
                                Marker marker = mMap.addMarker(markerOptions);
                                lastLocationVictimHashmap.put(data.getId(), marker);
                            }
                            Log.e("location: victim", data.getCurrentLat() + " " + data.getCurrentLong());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("MapActivity:", "getting Victim Locations Failed: ", t);
                    }
                });

                //Do something after 10 seconds
                handlerVictims.postDelayed(this, 10000);
            }
        };
        handlerVictims.post(runnerVictims);
    }

    private void fetchLocationOfVolunteers() {
        handlerVolunteers = new Handler();
        runnerVolunteers = new KillableRunnable() {
            @Override
            public void doWork() {
                mClient = SaveMe.getAzureClient(MapActivity.this);
                MobileServiceTable<VolunteerData> table = null;
                if (mClient != null) {
                    table = mClient.getTable(VolunteerData.class);
                }

                ListenableFuture<MobileServiceList<VolunteerData>> listenableFuture = table.where().field("currentStatus").eq("working").execute();
                Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VolunteerData>>() {
                    @Override
                    public void onSuccess(MobileServiceList<VolunteerData> result) {
                        volunteerLocations = result;
                        for (Object volunteerData : volunteerLocations) {
                            VolunteerData data = (VolunteerData) volunteerData;

                            if (lastLocationVolunteerHashmap.containsKey(data.getId())) {
                                Marker marker = lastLocationVolunteerHashmap.get(data.getId());
                                marker.setPosition(new LatLng(data.getCurrentLat(), data.getCurrentLong()));
                            } else {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(data.getCurrentLat(), data.getCurrentLong()))
                                        .title("Volunteer")
                                        .snippet(data.getId())
                                        .icon(volunteerBitmap);
                                Marker marker = mMap.addMarker(markerOptions);
                                lastLocationVolunteerHashmap.put(data.getId(), marker);
                            }
                            Log.e("location: Volunteers:", data.getCurrentLat() + " " + data.getCurrentLong());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("MapActivity:", "getting Victim Locations Failed: ", t);
                    }
                });
                //Do something after 10 seconds
                handlerVolunteers.postDelayed(this, 10000);
            }
        };
        handlerVolunteers.postDelayed(runnerVolunteers, 5000);
    }


    private void fetchLocationOfCamps() {

        final Handler handler = new Handler();
        Runnable r = () -> {
            mClient = SaveMe.getAzureClient(MapActivity.this);
            MobileServiceTable<CampData> table = null;
            if (mClient != null) {
                table = mClient.getTable(CampData.class);
            }

            ListenableFuture<MobileServiceList<CampData>> listenableFuture = table.where().execute();
            Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<CampData>>() {
                @Override
                public void onSuccess(MobileServiceList<CampData> result) {
                    campLocations = result;

                    for (Object campData : campLocations) {
                        CampData data = (CampData) campData;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(data.getLatitude(), data.getLongitude()))
                                .title("Camp")
                                .snippet(data.getId());
                        if (data.getType().matches("Medical Help")) {
                            markerOptions.icon(hospitalBitmap);
                        } else if (data.getType().matches("Food Camp")) {
                            markerOptions.icon(foodBitmap);
                        }

                        Log.e("location:camp: ", data.getLatitude() + " " + data.getLongitude());
                        mMap.addMarker(markerOptions);
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("MapActivity:", "getting Victim Locations Failed: ", t);
                }
            });

        };
        handler.post(r);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        switch (Objects.requireNonNull(marker.getTitle())) {
            case "Volunteer":
            case "Victim":
                if (marker.getSnippet().matches(Objects.requireNonNull(LoginActivity.getDeviceIMEI(this)))) {
                    break;
                }
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(marker.getPosition().latitude);
                location.setLongitude(marker.getPosition().longitude);
                UserDetailsMaps userDetailsMaps = UserDetailsMaps.newInstance(marker.getSnippet(), mLastLocation, location);
                userDetailsMaps.show(getSupportFragmentManager(), "Victim Fragment");
                break;
            case "Camp":
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(marker.getPosition().latitude);
                l.setLongitude(marker.getPosition().longitude);
                CampDetailsMaps campDetailsMaps = CampDetailsMaps.newInstance(marker.getSnippet(), mLastLocation, l);
                campDetailsMaps.show(getSupportFragmentManager(), "Camp Fragment");

                Log.e("Map Activty:", "Camp Clicked");
                break;
        }

        return false;
    }

    private void askLocationPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "Explanation for Permissions shown");

                new AlertDialog.Builder(this)
                        .setTitle("Access to Location")
                        .setMessage("Location Permissions are necessary for this app to function so, that someone from our rescue team can come and help you.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION))
                        .show();
            } else {
                // No explanation needed; request the permission
                Log.d(TAG, "Permission Requested");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "Permission Granted");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handlerVictims != null) {
            handlerVictims.post(runnerVictims);
            runnerVictims.restart();
            handlerVolunteers.postDelayed(runnerVolunteers, 5000);
            runnerVolunteers.restart();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handlerVictims != null) {
            handlerVictims.removeCallbacks(runnerVictims);
            runnerVictims.kill();
            handlerVolunteers.removeCallbacks(runnerVolunteers);
            runnerVolunteers.kill();
        }

    }

    private void sendLocationToDbVictim(LatLng latLng) {
        final MobileServiceTable<VictimData> table = mClient.getTable(VictimData.class);

        ListenableFuture<MobileServiceList<VictimData>> listenableFuture = table.where().field("id").eq(LoginActivity.getDeviceIMEI(this)).execute();
        Futures.addCallback(listenableFuture, new FutureCallback<MobileServiceList<VictimData>>() {
            @Override
            public void onSuccess(MobileServiceList<VictimData> result) {
                VictimData victimData = new VictimData();
                if (result.getTotalCount() > 0) {
                    victimData = result.get(0);
                    victimData.setCurrentLat(latLng.latitude);
                    victimData.setCurrentLong(latLng.longitude);
                    victimData.setStatus("danger");
                    table.update(victimData);
                    Log.e("location:", "existing User");
                } else {
                    victimData.setId(LoginActivity.getDeviceIMEI(MapActivity.this));
                    victimData.setAzureId(LoginActivity.getCurrentUserUniqueId(MapActivity.this));
                    victimData.setCurrentLat(latLng.latitude);
                    victimData.setCurrentLong(latLng.longitude);
                    victimData.setStatus("danger");
                    victimData.setSavedBy("null");
                    victimData.setSavedByUUID("null");
                    table.insert(victimData);
                    Log.e("location:", "new User");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Tag", t.getMessage());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getBundleExtra("MAP_INTENT");
        if (bundle != null)
            type = bundle.getString("type");

    }

}

