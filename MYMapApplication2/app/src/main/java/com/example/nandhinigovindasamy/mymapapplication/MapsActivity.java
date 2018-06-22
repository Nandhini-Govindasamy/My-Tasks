package  com.example.nandhinigovindasamy.mymapapplication;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.nandhinigovindasamy.mymapapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Context context;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mView=(View) findViewById(R.id.f_view);




        mView.setDrawingCacheEnabled(true);
        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache(true);
        context=this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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



        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String locationProvider = LocationManager.GPS_PROVIDER;
                String fullAddress = "";
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    fullAddress = "No Permission available";
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION },
                            1);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                {

                    try {
                        Looper.prepare();
                        manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                System.out.println("Location Changed ");
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {
                                System.out.println("Location Status Changed ");
                            }

                            @Override
                            public void onProviderEnabled(String s) {
                                System.out.println("Provider Enabled ");
                            }

                            @Override
                            public void onProviderDisabled(String s) {
                                System.out.println("Provider Disabled");
                            }
                        }, null);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Location currentLocation = manager.getLastKnownLocation(locationProvider);
                    if(currentLocation==null){
                        return "No Permission Available";
                    }
                    Geocoder coder = new Geocoder(MapsActivity.this);
                    try {
                      LatLng addresses = new LatLng(currentLocation.getLatitude() , currentLocation.getLongitude() );
                      return  addresses;
                        //List<Address> addresses = coder.getFromLocation(10.828547,78.666821,20);
                    }
                    catch (Exception e) {
                        fullAddress+="Error "+e;
                        e.printStackTrace();
                    }

                }
                return fullAddress;
            }
        };

        task.execute((Object[])null);
        LatLng address = null;
        try {
            Object result = task.get();
            if( result instanceof  LatLng ){
                address = (LatLng) result;

                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                Bitmap b = Bitmap.createBitmap(mView.getDrawingCache());
                mView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                //Bitmap bitmapImageLocal = BitmapFactory.decodeResource(R.layout.building_view);

                mMap.addMarker(new MarkerOptions().position(address).icon(BitmapDescriptorFactory.fromBitmap(b)));

               // mMap.addCircle(new CircleOptions().)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address,100));

            }
            else if( result instanceof  String ) {
                String errorText = (String) task.get();
                //TODO UnitNumber setError.
            }
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }


    }
    public static Bitmap loadBitmapFromView(View v) {

        if (v.getMeasuredHeight() <= 0) {
            v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        return null;
    }





}



