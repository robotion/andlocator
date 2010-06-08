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

import com.google.android.maps.*;

import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class Map extends MapActivity {

    Handler handler = new Handler();

    LinearLayout linearLayout;
    MapView mapView;

    List<Overlay> mapOverlays;
    Drawable drawable;
    ItemizedOverlayImpl itemizedOverlay;

    LocationManager loc;
    Context me;

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(final Location location) {
            System.out.println("----< onLocationChanged: " + location);
//            Toast.makeText(Map.this, "onLocationChanged: " + location, Toast.LENGTH_LONG).show();


            handler.post(new Runnable() {

                public void run() {
                    updateIconOnMap(location);
                    mapView.getController().animateTo(getGeoPointFromLocation(location));

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);

        startService(new Intent(this, PositioningService.class));


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses = geocoder.getFromLocationName("Marienstr. 11, 10117, Berlin", 1);

            for (Address adr : addresses) {

                updateIconOnMap(adr);

                mapView.getController().animateTo(getGeoPointFromAddress(adr));

            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

//        KeyBasedFileProcessor kbfp = new KeyBasedFileProcessor();
//
//        kbfp.doIt();

        Location location = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateIconOnMap(location);
        mapView.getController().animateTo(getGeoPointFromLocation(location));
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

//                    updateIconOnMap(location);

//                    mapView.getController().animateTo(getGeoPointFromLocation(location));
                    // use first non null location
//                    break;

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
}
