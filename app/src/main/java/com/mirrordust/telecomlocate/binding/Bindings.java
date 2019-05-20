package com.mirrordust.telecomlocate.binding;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.mapbox.core.utils.ColorUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mirrordust.telecomlocate.entity.Sample;
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
    public static void setMapStyple(MapView mapView, List<Sample> pointList) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                if (pointList.size() == 0) return;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        //mapbox:mapbox_styleUrl="mapbox://styles/mapbox/streets-v10"
                        //init route points
                        LineManager lineManager = new LineManager(mapView, mapboxMap, style);

                        List<LatLng> latLngs = new ArrayList<>();
//                        List<LineOptions> lineOptionsList = new ArrayList<>();
                        for (Sample point : pointList) {
                            latLngs.add(new LatLng(point.getLatLng().getLatitude(), point.getLatLng().getLongitude()));
                        }
                        LineOptions lineOptions = new LineOptions()
                                .withLatLngs(latLngs)
                                .withLineColor("#e55e5e")
                                .withLineWidth(5.0f);
                        lineManager.create(lineOptions);


                        LatLng latLng = new LatLng(pointList.get(0).getLatLng().getLatitude(), pointList.get(0).getLatLng().getLongitude());
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
        });
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("signalchart")
    public static void setSignalChart(LineChart lineChart, LineData lineData) {
        lineChart.setData(lineData);
    }
}
