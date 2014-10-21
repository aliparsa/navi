package com.graphhopper.android.Helpers;

import org.mapsforge.map.android.view.MapView;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class MapViewHelper {
    public static void clearMapView(MapView mapView) {
        while (mapView.getLayerManager().getLayers().size() > 1) {
            mapView.getLayerManager().getLayers().remove(1);
        }
    }
}
