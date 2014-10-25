package com.graphhopper.android.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class DrawOnMapHelper {

    public static Polyline createPolyline(GHResponse response) {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setStrokeWidth(9        );

        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++) {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));


        }

        return line;
    }


    public static void showImgOnThisPoint(Context context,LatLong latlong,MapView mapView, int IMGresID) {
        Marker marker = new Marker(latlong,
                AndroidGraphicFactory.convertToBitmap((context.getResources().getDrawable(IMGresID))),
                0,
                0);
        mapView.getLayerManager().getLayers().add(marker);
    }

    public static  void drawPolylineByPoints(MapView mapView,int layerCount,PointList pointList) {


        if (layerCount == -1)
            layerCount = mapView.getLayerManager().getLayers().size();
        else {
            while (mapView.getLayerManager().getLayers().size() != layerCount) {
                mapView.getLayerManager().getLayers().remove(mapView.getLayerManager().getLayers().size() - 1);
            }
        }

        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.RED);
//        paintStroke.setDashPathEffect(new float[]
//                {
//                        25, 15
//                });
        paintStroke.setStrokeWidth(20);

        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = pointList;
        for (int i = 0; i < pointList.getSize(); i++) {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));

        }


        mapView.getLayerManager().getLayers().add(line);
        MapViewHelper.animateToPoint(mapView,pointList.getLat(0), pointList.getLon(0));

    }


    public static void drawPolyLineByLocations(MapView mapView,ArrayList<Location> locations) {

        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.RED);
//        paintStroke.setDashPathEffect(new float[]
//                {
//                        25, 15
//                });
        paintStroke.setStrokeWidth(20);

        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        //PointList tmp = pointList;
        for (int i = 0; i < locations.size(); i++) {
            geoPoints.add(new LatLong(locations.get(i).getLatitude(),locations.get(i).getLongitude()));
        }
        mapView.getLayerManager().getLayers().add(line);
    }
}
