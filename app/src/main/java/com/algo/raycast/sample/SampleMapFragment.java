package com.algo.raycast.sample;

import android.app.AlertDialog;
import android.os.Handler;
import android.content.DialogInterface;

import com.algo.raycast.RaycastHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Infernus on 05/07/16.
 */
public class SampleMapFragment extends BaseMapFragment {
    ArrayList<LatLng> latLngs = new ArrayList<>();
    LatLng latLng1 = new LatLng(28.635789, 77.215029);
    LatLng latLng2 = new LatLng(28.638086, 77.220952);
    LatLng latLng3 = new LatLng(28.634621, 77.225758);
    LatLng latLng4 = new LatLng(28.634771, 77.221467);
    LatLng latLng5 = new LatLng(28.632662, 77.225114);
    LatLng latLng6 = new LatLng(28.629724, 77.220265);
    LatLng latLng7 = new LatLng(28.631796, 77.215265);

    Marker marker;

    @Override
    protected void handleOnMapReady() {
        latLngs.add(latLng1);
        latLngs.add(latLng2);
        latLngs.add(latLng3);
        latLngs.add(latLng4);
        latLngs.add(latLng5);
        latLngs.add(latLng6);
        latLngs.add(latLng7);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawPolylineWithLatLngs(latLngs, true);
            }
        }, 100);


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker != null) marker.remove();
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                new AlertDialog.Builder(getActivity())
                        .setTitle("Ray Cast:")
                        .setMessage(RaycastHelper.isLatLngInside(latLngs, latLng) ? "Location is inside the polygon" : "Location is outside the polygon")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    protected void handleOnCameraChange(LatLng currentCameraTarget) {

    }

    @Override
    protected float getZoomLevel() {
        return 0;
    }
}
