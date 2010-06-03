package com.jaeckel.locator;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * User: biafra
 * Date: Jun 3, 2010
 * Time: 11:45:17 PM
 */
public class ItemizedOverlayImpl extends ItemizedOverlay {

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();


    public ItemizedOverlayImpl(Drawable drawable) {
        super(boundCenterBottom(drawable));
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }
}
