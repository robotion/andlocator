package com.jaeckel.locator;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.content.*;
import android.view.MenuItem;
import android.view.Menu;
import android.preference.PreferenceManager;

import com.google.android.maps.*;
import com.jaeckel.locator.pgp.PubKeyGenerator;

import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Map extends MapActivity {

    private final static int MENU_MY_LOCATION = 0;
    private final static int MENU_SETTINGS = 1;
    private final static int MENU_SATELLITE = 2;
    //    private final static int MENU_STREET = 3;
    private static final int MENU_SEND_LOCATION = 4;

    private Handler handler = new Handler();

    //    private LinearLayout linearLayout;
    private MapView mapView;

    private List<Overlay> mapOverlays;
    private Drawable drawable;
    private ItemizedOverlayImpl itemizedOverlay;

    private LocationManager loc;
    private Context me;

    public static Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        Map.myLocation = myLocation;

        updateIconOnMap(myLocation);
        mapView.getController().animateTo(getGeoPointFromLocation(myLocation));
    }

    private static Location myLocation;

    private SharedPreferences prefs;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startService(new Intent(this, PositioningService.class));


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!new File("/sdcard/pub.asc").exists()) {
            createKeys();
        }


        loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);

        if (mapView == null) {
            Toast.makeText(this, "mapView == null ", Toast.LENGTH_LONG).show();
        } else {
            mapView.setBuiltInZoomControls(true);
            mapView.setStreetView(true);

        }
        drawable = this.getResources().getDrawable(android.R.drawable.radiobutton_on_background);


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses = geocoder.getFromLocationName("Marienstr. 11, 10117, Berlin", 1);

            for (Address adr : addresses) {

                updateIconOnMap(adr);

                myLocation = getLocationFromAddress(adr);

                mapView.getController().animateTo(getGeoPointFromAddress(adr));
                mapView.preLoad();

            }

        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Location location = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        updateIconOnMap(location);
        mapView.getController().animateTo(getGeoPointFromLocation(location));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {

        super.onResume();

        if (appService == null) {
            bindService(new Intent(this, PositioningService.class), onService, BIND_AUTO_CREATE); // | BIND_DEBUG_UNBIND);
        }

        registerReceiver(locationReceiver, new IntentFilter(PositioningService.BROADCAST_ACTION));

        updateIconOnMap(myLocation);
        mapView.getController().animateTo(getGeoPointFromLocation(myLocation));

    }

    @Override
    public void onPause() {
        super.onPause();

//        if (appService != null) {
//            unbindService(onService);
//        }
        unregisterReceiver(locationReceiver);

    }

    private void createKeys() {

        Toast.makeText(this, "Generating RSA KeyPair...", Toast.LENGTH_LONG).show();

        PubKeyGenerator keygen = new PubKeyGenerator();

        String[] keyPair = keygen.createKeyPair();

        System.out.println("Secret Key: \n" + keyPair[0]);
        System.out.println("Public Key: \n" + keyPair[1]);

        try {

            FileOutputStream fOut = new FileOutputStream("/sdcard/sec.asc");
            fOut.write(keyPair[0].getBytes());
            fOut.close();

            FileOutputStream fOut2 = new FileOutputStream("/sdcard/pub.asc");
            fOut2.write(keyPair[1].getBytes());
            fOut2.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Location getLocationFromAddress(Address adr) {

        Location result = new Location(LocationManager.GPS_PROVIDER);

        result.setLatitude(adr.getLatitude());
        result.setLongitude(adr.getLongitude());

        return result;
    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            Location location = (Location) intent.getExtras().get("location");
            myLocation = location;
//            Toast.makeText(Map.this, "Received location: " + location, Toast.LENGTH_LONG).show();

            updateIconOnMap(myLocation);
            mapView.getController().animateTo(getGeoPointFromLocation(myLocation));
        }
    };


    private void updateIconOnMap(Location location) {

        GeoPoint geopoint = getGeoPointFromLocation(location);
        drawIcon(geopoint);
    }


    private void updateIconOnMap(Address adr) {

        GeoPoint geopoint = getGeoPointFromAddress(adr);
        drawIcon(geopoint);
    }

    private GeoPoint getGeoPointFromAddress(Address adr) {

        if (adr == null) {
            return null;
        }

        return getGeoPointFromLatLon(adr.getLatitude(), adr.getLongitude());
    }

    private void drawIcon(GeoPoint geopoint) {

//        Toast.makeText(this, "drawIcon", Toast.LENGTH_SHORT).show();

        mapOverlays = mapView.getOverlays();
        itemizedOverlay = new ItemizedOverlayImpl(drawable);


        OverlayItem overlayitem = new OverlayItem(geopoint, "", "");
        mapOverlays.clear();
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }

    private GeoPoint getGeoPointFromLocation(Location location) {

        if (location == null) {
            return null;
        }

        return getGeoPointFromLatLon(location.getLatitude(), location.getLongitude());
    }

    private GeoPoint getGeoPointFromLatLon(Double lat, Double lon) {

        Double lon_E6 = lon * 1E6;
        Double lat_E6 = lat * 1E6;

        GeoPoint geopoint = new GeoPoint(lat_E6.intValue(), lon_E6.intValue());
        return geopoint;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_MY_LOCATION, Menu.NONE, "My Location");
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
        menu.add(Menu.NONE, MENU_SATELLITE, Menu.NONE, "Toggle Satellite");
        menu.add(Menu.NONE, MENU_SEND_LOCATION, Menu.NONE, "Send Location");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_MY_LOCATION:
                mapView.getController().animateTo(getGeoPointFromLocation(myLocation));
                return true;

            case MENU_SETTINGS:

                startActivity(new Intent(this, EditPreferences.class));
                return true;

            case MENU_SATELLITE:

                if (mapView.isSatellite()) {
                    mapView.setSatellite(false);

                } else {
                    mapView.setSatellite(true);
                    mapView.setStreetView(true);
                }
                return true;

            case MENU_SEND_LOCATION:
                Toast.makeText(this, "appService: " + appService, Toast.LENGTH_SHORT).show();

                if (appService != null) {
                    appService.sendLocationAsync(myLocation);

                }
                return true;
        }

        return false;
    }

    private PositioningService appService = null;

    private ServiceConnection onService = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            appService = ((PositioningService.LocalBinder) iBinder).getService();
//            try {
//
//                throw new RuntimeException("onServiceConnected...");
//
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
//            try {
//
//                throw new RuntimeException("onServiceDisconnected...");
//
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            }
            appService = null;
        }
    };

}
