package com.jaeckel.locator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationManager;
import android.location.Location;
import android.content.Intent;
import android.content.Context;

import com.google.android.maps.*;

import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class Map extends MapActivity {


    LinearLayout linearLayout;
    MapView mapView;

    List<Overlay> mapOverlays;
    Drawable drawable;
    ItemizedOverlayImpl itemizedOverlay;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startService(new Intent(this, PositioningService.class));

        KeyBasedFileProcessor kbfp = new KeyBasedFileProcessor();

        kbfp.doIt();

        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onResume() {
        super.onResume();

        LocationManager loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean enabledOnly = false;
        List<String> providers = loc.getProviders(enabledOnly);

        for (String pName : providers) {


            if (loc.isProviderEnabled(pName)) {
                // LocationProvider provider = loc.getProvider(pName);

                Location location = loc.getLastKnownLocation(pName);
                if (location != null) {
                    Toast.makeText(this, "Provider " + pName + ": " + location.getLatitude() + "/" + location.getLongitude() +
                            "{" + location.getAccuracy() + "m}", Toast.LENGTH_LONG).show();
                    Double lon = location.getLongitude() * 1E6;
                    Double lat = location.getLatitude() * 1E6;

                    mapOverlays = mapView.getOverlays();
                    drawable = this.getResources().getDrawable(R.drawable.androidmarker);
                    itemizedOverlay = new ItemizedOverlayImpl(drawable);

                    GeoPoint geopoint = new GeoPoint(lat.intValue(), lon.intValue());

                    OverlayItem overlayitem = new OverlayItem(geopoint, "lat: " + lat, "lon: " + lon);
                    Toast.makeText(this, "lat: " + lat + " lon: " + lon, Toast.LENGTH_LONG).show();
                    itemizedOverlay.addOverlay(overlayitem);
                    mapOverlays.add(itemizedOverlay);


                    MapController controller = mapView.getController();

                    controller.animateTo(geopoint);

                    // use first non null location
//                    break;

                } else {

                    Toast.makeText(this, "No LastKnownLocation for Provider: " + pName + " available", Toast.LENGTH_LONG).show();

                }


            } else {
                Toast.makeText(this, "LocationProvider: " + pName + " not enabled", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
