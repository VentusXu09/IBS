package com.ventus.ibs.gui.sample.sampledetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.ventus.ibs.R;
import com.ventus.ibs.gui.sample.sampledetail.SampleDetailActivity;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.model.DataHelper;
import com.ventus.ibs.util.UIUtils;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private double longitude;
    private double latitude;
    private MapView mMapView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) frameLayout.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        final SampleDetailActivity parentActivity = (SampleDetailActivity) getActivity();
        setPoint(parentActivity);
        return frameLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setPoint(SampleDetailActivity parentActivity) {
        String mID = parentActivity.getSampleId();
        Sample sample = DataHelper.getSample(parentActivity.getRealm(), mID);
        longitude = sample.getLatLng().getLongitude();
        latitude = sample.getLatLng().getLatitude();
    }

    private void showPoints(MapboxMap mapboxMap) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .build();
        mapboxMap.setCameraPosition(position);
        mapboxMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(longitude+", "+latitude));
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        LatLng latLng = new LatLng(latitude, longitude);

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)
                                .zoom(17)
                                .build();
                        mapboxMap.setCameraPosition(cameraPosition);

                        style.addImage(UIUtils.ID_ICON_MARKER, BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.marker_64dp, null)), true);
                        GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
                        SymbolManager symbolManager = new SymbolManager(mMapView, mapboxMap, style, null, geoJsonOptions);
                        symbolManager.setIconAllowOverlap(true);
                        SymbolOptions symbolOptions = new SymbolOptions()
                                .withLatLng(latLng)
                                .withIconImage(UIUtils.ID_ICON_MARKER)
                                .withIconSize(1.3f)
                                .withZIndex(10)
                                .withDraggable(false);

                        Symbol symbol = symbolManager.create(symbolOptions);
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
