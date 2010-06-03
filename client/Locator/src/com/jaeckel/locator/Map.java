package com.jaeckel.locator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;

import com.google.android.maps.*;

import java.util.List;

public class Map extends MapActivity {


    LinearLayout linearLayout;
    MapView mapView;

    List<Overlay> mapOverlays;
    Drawable drawable;
    ItemizedOverlayImpl itemizedOverlay;


    GeoPoint point = new GeoPoint(13240000, 53120000);
    OverlayItem overlayitem = new OverlayItem(point, "", "");

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.marker);
        itemizedOverlay = new ItemizedOverlayImpl(drawable);


        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

}
