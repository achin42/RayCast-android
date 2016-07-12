package com.algo.raycast.sample;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.algo.raycast.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public abstract class BaseMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final int GEO_BOX_PADDING = 40;

    protected GoogleMap googleMap;
    protected float zoomLevel;
    protected abstract void handleOnMapReady();
    protected abstract void handleOnCameraChange(LatLng currentCameraTarget);
    protected abstract float getZoomLevel();

    private Polyline directionPolyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
    }

    private GoogleMap.OnCameraChangeListener onCameraChangeListener = new GoogleMap.OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition position) {
            handleOnCameraChange(position.target);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupMap();
        handleOnMapReady();
    }

    private void setupMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
    }

    public void adjustPadding(final int paddingTop, final int paddingBottom) {
        if(googleMap != null) {
            googleMap.setPadding(0, paddingTop, 0, paddingBottom);
        }
    }

    private void setCameraChangeListener() {
        if(googleMap != null) googleMap.setOnCameraChangeListener(onCameraChangeListener);
    }

    public void removeCameraChangeListener() {
        if(googleMap != null) googleMap.setOnCameraChangeListener(null);
    }

    public void reposition(LatLng latlng) {
        targetAndZoom(latlng, googleMap.getCameraPosition().zoom);
    }

    public boolean hasInitialized() {
        return (googleMap != null);
    }

    public void geoBox(LatLngBounds bounds, LatLng centerPoint) {
        doGeoBoxingAnimation(bounds, centerPoint);
    }

    public void targetAndZoomInstantly(LatLng latLng, float zoom) {
        targetAndZoom(latLng, zoom, false);
    }

    public void targetAndZoom(LatLng latlng, float zoom) {
        targetAndZoom(latlng, zoom, true);
    }

    private void targetAndZoom(LatLng latlng, float zoom, boolean isAnimated) {
        removeCameraChangeListener();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(zoom)                           // Sets the zoom
                .build();                       // Creates a CameraPosition from the builder

        if (googleMap != null) {
            if (isAnimated) {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

        setCameraChangeListener();
    }


    protected LatLngBounds getBounds(ArrayList<LatLng> boundingPoints) {
        LatLng baseLatLng = new LatLng(boundingPoints.get(0).latitude, boundingPoints.get(0).longitude);

        LatLng lowestLat  = baseLatLng;
        LatLng highestLat = baseLatLng;
        LatLng lowestLng  = baseLatLng;
        LatLng highestLng = baseLatLng;

        for(int i = 1; i< boundingPoints.size(); i++){
            if(boundingPoints.get(i).latitude < lowestLat.latitude) {
                lowestLat = new LatLng(boundingPoints.get(i).latitude, boundingPoints.get(i).longitude);
            }
            if(boundingPoints.get(i).latitude > highestLat.latitude) {
                highestLat = new LatLng(boundingPoints.get(i).latitude, boundingPoints.get(i).longitude);
            }
            if(boundingPoints.get(i).longitude < lowestLng.longitude) {
                lowestLng = new LatLng(boundingPoints.get(i).latitude, boundingPoints.get(i).longitude);
            }
            if(boundingPoints.get(i).longitude > highestLng.longitude) {
                highestLng = new LatLng(boundingPoints.get(i).latitude, boundingPoints.get(i).longitude);
            }
        }

        return new LatLngBounds.Builder()
                .include(lowestLat)
                .include(highestLat)
                .include(lowestLng)
                .include(highestLng)
                .build();
    }

    protected void doGeoBoxingAnimation(LatLngBounds bounds) throws IllegalStateException {
        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, convertDpToPixel(GEO_BOX_PADDING)));
        } catch(IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    public int convertDpToPixel(float dp){
        Resources resources = getActivity().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    private void doGeoBoxingAnimation(LatLngBounds bounds, LatLng primaryPoint) throws IllegalStateException {
        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, convertDpToPixel(GEO_BOX_PADDING)));
//            zoomLevel = googleMap.getCameraPosition().zoom;
//            if(primaryPoint != null) {
//                targetAndZoom(primaryPoint, getZoomLevel());
//            } else {
//                setCameraChangeListener();
//            }
        } catch(IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    public void clearMap() {
        if(googleMap != null) googleMap.clear();
        directionPolyline = null;
    }

    protected void doInitialSetup() {
        getView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                setCameraChangeListener();
                return false;
            }
        });
    }

    public Polyline drawPolylineWithLatLngs(ArrayList<LatLng> pathLatLngs, boolean isClosed) {
        doGeoBoxingAnimation(getBounds(pathLatLngs));

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for(int i = 0; i < pathLatLngs.size(); i++) {
            options.add(pathLatLngs.get(i));
        }
        if(isClosed) {
            options.add(pathLatLngs.get(0));
        }
        options.color(getResources().getColor(R.color.colorPrimaryDark));

        return googleMap.addPolyline(options);
    }

    public interface DrawDirectionInterface {
        void onDirection(String distance, String time);
    }
}

