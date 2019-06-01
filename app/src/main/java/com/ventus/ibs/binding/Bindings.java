package com.ventus.ibs.binding;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngQuad;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.ImageSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.util.Constants;
import com.ventus.ibs.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

/**
 * Created by ventus0905 on 04/14/2019
 */
public class Bindings {
    private static final String TAG = "Bindings";

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"mapstyleRecord", "endmarker"}, requireAll = false)
    public static void setMapStypleRecord(MapView mapView, List<Sample> pointList, Drawable endMarker) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                if (pointList.size() == 0) return;
                if (mapboxMap.getStyle() == null) {
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            int index = pointList.size();
                            double lat = pointList.get(pointList.size() -1).getLatLng().getLatitude();
                            double lng = pointList.get(pointList.size() -1).getLatLng().getLongitude();

                            LatLng eLatLng = new LatLng(pointList.get(pointList.size() -1).getLatLng().getLatitude(), pointList.get(pointList.size() - 1).getLatLng().getLongitude());
                            if (Constants.FAKE_API) {
                                eLatLng = new LatLng(31.286437, 121.50239);
                            }

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(eLatLng)
                                    .zoom(17)
                                    .build();
                            mapboxMap.setCameraPosition(cameraPosition);

                            //Add End Marker
                            if (null == mapboxMap.getStyle().getImage("my-marker-image")) {
                                mapboxMap.getStyle().addImage("my-marker-image", BitmapUtils.getBitmapFromDrawable(endMarker));
                            }


                            LatLngQuad quad = new LatLngQuad(
                                    new LatLng(lat+0.0001, lng-0.0001),
                                    new LatLng(lat+0.0001, lng+0.0001),
                                    new LatLng(lat-0.0001, lng+0.0001),
                                    new LatLng(lat-0.0001, lng-0.0001));
                            ImageSource imageSource = (ImageSource) mapboxMap.getStyle().getSource("source-id");
                            if (null != imageSource) {
                                imageSource.setCoordinates(quad);
                            } else {
                                imageSource = new ImageSource("source-id", quad, BitmapUtils.getBitmapFromDrawable(endMarker));
                                mapboxMap.getStyle().addSource(imageSource);
                            }

                            SymbolLayer symbolLayer = new SymbolLayer("layer-id" + index, "source-id");
                            symbolLayer.setProperties(
                                    PropertyFactory.iconImage("my-marker-image")
                            );

                            RasterLayer layer = new RasterLayer("layer-id" + index, "source-id");
                            RasterLayer oldLayer = (RasterLayer) mapboxMap.getStyle().getLayer("layer-id" + (index-1));
                            if (null != oldLayer) {
                                oldLayer.setProperties(visibility(Property.NONE));
                                mapboxMap.getStyle().removeLayer(oldLayer);
                            }


                            mapboxMap.getStyle().addLayer(layer);
                        }
                    });
                } else {
                    int index = pointList.size();

                    double lat = pointList.get(pointList.size() -1).getLatLng().getLatitude();
                    double lng = pointList.get(pointList.size() -1).getLatLng().getLongitude();
                    LatLng eLatLng = new LatLng(pointList.get(pointList.size() -1).getLatLng().getLatitude(), pointList.get(pointList.size() - 1).getLatLng().getLongitude());
                    if (Constants.FAKE_API) {
                        eLatLng = new LatLng(31.286437, 121.50239);
                    }

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(eLatLng)
                            .zoom(17)
                            .build();
                    mapboxMap.setCameraPosition(cameraPosition);

                    //Add End Marker
                    LatLngQuad quad = new LatLngQuad(
                            new LatLng(lat+0.0001d, lng-0.0001d),
                            new LatLng(lat+0.0001d, lng+0.0001d),
                            new LatLng(lat-0.0001d, lng+0.0001d),
                            new LatLng(lat-0.0001d, lng-0.0001d));

                    ImageSource imageSource = (ImageSource) mapboxMap.getStyle().getSource("source-id");
                    if (null != imageSource) {
                        imageSource.setCoordinates(quad);
                    } else {
                        imageSource = new ImageSource("source-id",quad, BitmapUtils.getBitmapFromDrawable(endMarker));
                        mapboxMap.getStyle().addSource(imageSource);
                    }

                    if (null == mapboxMap.getStyle().getImage("my-marker-image")) {
                        mapboxMap.getStyle().addImage("my-marker-image", BitmapUtils.getBitmapFromDrawable(endMarker));
                    }
                    SymbolLayer symbolLayer = new SymbolLayer("layer-id" + index, "source-id");
                    symbolLayer.setProperties(
                            PropertyFactory.iconImage("my-marker-image")
                    );
                    RasterLayer layer = new RasterLayer("layer-id" + index, "source-id");
                    RasterLayer oldLayer = (RasterLayer) mapboxMap.getStyle().getLayer("layer-id" + (index-1));
                    if (null != oldLayer) {
                        oldLayer.setProperties(visibility(Property.NONE));
                        mapboxMap.getStyle().removeLayer(oldLayer);
                    }

                    mapboxMap.getStyle().addLayer(layer);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"mapstyle", "startmarker", "endmarker"}, requireAll = false)
    public static void setMapStyleGallery(MapView mapView, List<Sample> pointList, Drawable startMarker, Drawable endMarker) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                if (pointList.size() == 0) return;
                if (mapboxMap.getStyle() == null) {
                    mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            //mapbox:mapbox_styleUrl="mapbox://styles/mapbox/streets-v10"
                            //init route points
                            LineManager lineManager = new LineManager(mapView, mapboxMap, style);

                            List<LatLng> latLngs = new ArrayList<>();
                            for (Sample point : pointList) {
                                latLngs.add(new LatLng(point.getLatLng().getLatitude(), point.getLatLng().getLongitude()));
                            }
                            LineOptions lineOptions = new LineOptions()
                                    .withLatLngs(latLngs)
                                    .withLineColor("#346187")
                                    .withLineWidth(5.0f);
                            lineManager.create(lineOptions);


                            LatLng latLng = new LatLng(pointList.get(0).getLatLng().getLatitude(), pointList.get(0).getLatLng().getLongitude());
                            LatLng eLatLng = new LatLng(pointList.get(pointList.size() - 1).getLatLng().getLatitude(), pointList.get(pointList.size() - 1).getLatLng().getLongitude());
                            if (Constants.FAKE_API) {
                                latLng = new LatLng(31.286437, 121.50239);
                                eLatLng = new LatLng(31.286437, 121.50239);
                            }

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(eLatLng)
                                    .zoom(17)
                                    .build();
                            mapboxMap.setCameraPosition(cameraPosition);

                            GeoJsonOptions geoJsonOptions = new GeoJsonOptions();
                            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);
                            symbolManager.setIconAllowOverlap(true);
                            if (null != startMarker) {
                                style.addImage(UIUtils.ID_ICON_MARKER, BitmapUtils.getBitmapFromDrawable(startMarker));
                                SymbolOptions symbolOptions = new SymbolOptions()
                                        .withLatLng(latLng)
                                        .withIconImage(UIUtils.ID_ICON_MARKER)
                                        .withIconSize(0.6f)
                                        .withZIndex(10)
                                        .withDraggable(false);

                                Symbol symbol = symbolManager.create(symbolOptions);
                            }

                            if (null != endMarker) {
                                style.addImage(UIUtils.ID_CHECKED_FLAG, BitmapUtils.getBitmapFromDrawable(endMarker));
                                SymbolOptions symbolOptions = new SymbolOptions()
                                        .withLatLng(eLatLng)
                                        .withIconImage(UIUtils.ID_CHECKED_FLAG)
                                        .withIconSize(0.6f)
                                        .withZIndex(10)
                                        .withDraggable(false);

                                Symbol symbol = symbolManager.create(symbolOptions);
                            }
                        }
                    });
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("signalchart")
    public static void setSignalChart(LineChart lineChart, LineData lineData) {
        lineChart.clear();
        lineChart.setData(lineData);
    }
}
