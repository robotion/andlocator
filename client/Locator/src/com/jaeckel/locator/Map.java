package com.jaeckel.locator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.location.*;
import android.content.Intent;
import android.content.Context;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.maps.*;

import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class Map extends MapActivity {

    private final static int MENU_MY_LOCATION = 0;
    private final static int MENU_SETTINGS = 1;
    private final static int MENU_SATELLITE = 2;
    private final static int MENU_STREET = 3;

    private Handler handler = new Handler();

    private LinearLayout linearLayout;
    private MapView mapView;

    private List<Overlay> mapOverlays;
    private Drawable drawable;
    private ItemizedOverlayImpl itemizedOverlay;

    private LocationManager loc;
    private Context me;

    private Location myLocation;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);

        if (mapView == null) {
            Toast.makeText(this, "mapView == null ", Toast.LENGTH_LONG).show();
        } else {
            mapView.setBuiltInZoomControls(true);
        }
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);

        startService(new Intent(this, PositioningService.class));


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

    public void onResume() {

        super.onResume();

        float meters = 1;
        long millis = 10000; // one minute

        if (loc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, millis, meters, listener);

        } else {

            loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, millis, meters, listener);
        }

        boolean enabledOnly = false;
        List<String> providers = loc.getProviders(enabledOnly);

        for (String pName : providers) {


            if (loc.isProviderEnabled(pName)) {
                // LocationProvider provider = loc.getProvider(pName);

                Location location = loc.getLastKnownLocation(pName);
                if (location != null) {


                    Toast.makeText(this, "Last known location: Provider " + pName + ": " + location.getLatitude()
                            + "/" + location.getLongitude()
                            + "{" + location.getAccuracy() + "m}", Toast.LENGTH_LONG).show();

                    myLocation = location;


                } else {

                    Toast.makeText(this, "No LastKnownLocation for Provider: " + pName
                            + " available", Toast.LENGTH_LONG).show();

                }


            } else {
                Toast.makeText(this, "LocationProvider: " + pName + " not enabled", Toast.LENGTH_SHORT).show();
            }


        }
    }


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

        Toast.makeText(this, "drawIcon", Toast.LENGTH_SHORT).show();

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


    public void onPause() {
        super.onPause();

        loc.removeUpdates(listener);


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_MY_LOCATION, Menu.NONE, "My Location");
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");


        if (mapView.isStreetView()) {
            menu.add(Menu.NONE, MENU_SATELLITE, Menu.NONE, "Satellite View");
        } else {
            menu.add(Menu.NONE, MENU_STREET, Menu.NONE, "Street View");

        }


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_MY_LOCATION:
                Toast.makeText(this, "MENU_MY_LOCATION", Toast.LENGTH_SHORT).show();
                mapView.getController().animateTo(getGeoPointFromLocation(myLocation));
                return true;

            case MENU_SETTINGS:
                Toast.makeText(this, "MENU_SETTINGS", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, EditPreferences.class));
                return true;

            case MENU_SATELLITE:
                Toast.makeText(this, "MENU_SATELLITE", Toast.LENGTH_SHORT).show();

                mapView.setSatellite(true);

                return true;

            case MENU_STREET:
                Toast.makeText(this, "MENU_STREET", Toast.LENGTH_SHORT).show();

                mapView.setStreetView(true);

                return true;
        }

        return false;
    }

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(final Location location) {
            System.out.println("----< onLocationChanged: " + location);
//            Toast.makeText(Map.this, "onLocationChanged: " + location, Toast.LENGTH_LONG).show();


            handler.post(new Runnable() {

                public void run() {
                    updateIconOnMap(location);
                    mapView.getController().animateTo(getGeoPointFromLocation(location));
                    myLocation = location;
                }
            });

        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
            System.out.println("----< onStatusChanged: " + s);
//            Toast.makeText(Map.this, "onStatusChanged", Toast.LENGTH_LONG).show();

        }

        public void onProviderEnabled(String s) {
            System.out.println("----< onProviderEnabled: " + s);
//            Toast.makeText(Map.this, "onProviderEnabled", Toast.LENGTH_LONG).show();

        }

        public void onProviderDisabled(String s) {
            System.out.println("----< onProviderDisabled: " + s);
//            Toast.makeText(Map.this, "onProviderDisabled ", Toast.LENGTH_LONG).show();

        }
    };
}
