package com.jaeckel.locator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Address;

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

        KeyBasedFileProcessor kbfp = new KeyBasedFileProcessor();


        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> locations = null;

        try {
            locations = geocoder.getFromLocationName("Marienstr. 11, 10117, Berlin", 1);

            Double lon = locations.get(0).getLongitude() * 1E6;
            Double lat = locations.get(0).getLatitude() * 1E6;

            Projection projection = mapView.getProjection();



            mapOverlays = mapView.getOverlays();
            drawable = this.getResources().getDrawable(R.drawable.androidmarker);
            itemizedOverlay = new ItemizedOverlayImpl(drawable);

            GeoPoint geopoint = new GeoPoint(lat.intValue(), lon.intValue());
       
            OverlayItem overlayitem = new OverlayItem(geopoint, "lat: " + lat, "lon: " + lon);
            System.out.println("lat: " + lat + " lon: " + lon);
            itemizedOverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedOverlay);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

}
