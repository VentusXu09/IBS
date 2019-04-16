package com.mirrordust.telecomlocate.binding;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mirrordust.telecomlocate.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ventus0905 on 04/14/2019
 */
public class Bindings {
    private static final String TAG = "Bindings";

    @SuppressWarnings("unchecked")
    @BindingAdapter("mapstyle")
    public static void setMapStyple(MapView mapView, List<Point> pointList) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                if (pointList.size() == 0) return;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        //mapbox:mapbox_styleUrl="mapbox://styles/mapbox/streets-v10"
                        //init route points
                        List<Point> routeCoordinates = new ArrayList<>();
                        routeCoordinates.addAll(pointList);

                        GeoJsonSource source = (GeoJsonSource) style.getSource("line-source");
                        if (null != source) {
                            source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                    LineString.fromLngLats(routeCoordinates))}));

                        } else {

                            style.addSource(new GeoJsonSource("line-source",
                                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                            LineString.fromLngLats(routeCoordinates)
                                    )})));
                            style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                                    PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                    PropertyFactory.lineWidth(5f),
                                    PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                            ));
                        }


                    }
                });
                if (pointList.size() == 0) return;
                LatLng latLng = new LatLng(pointList.get(0).latitude(), pointList.get(0).longitude());
                if (Constants.FAKE_API) {
                    latLng = new LatLng(31.286437, 121.50239);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(17)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }
        });
    }
}
