package com.algo.raycast;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Created by Infernus on 05/07/16.
 */
public class RaycastHelper {
    public static boolean isLatLngInside(ArrayList<LatLng> latLngs, LatLng latLng) {
        return normalizeLatLngsAndProceed(latLngs, latLng);
    }

    public static boolean isPointInside(ArrayList<Point> points, Point point) {
        return isPointInsideEdges(getEdgesFromPoints(points), point);
    }

    private static boolean normalizeLatLngsAndProceed(ArrayList<LatLng> latLngs, LatLng point) {
        if(latLngs.size() < 3) throw new RuntimeException("At least 3 latlngs are required");

        double smallestLongitude = Double.MAX_VALUE, highestLongitude = Double.MIN_VALUE;

        for(LatLng latLng : latLngs) {
            if(latLng.longitude < smallestLongitude) {
                smallestLongitude = latLng.longitude;
            } else if(latLng.longitude > highestLongitude) {
                highestLongitude = latLng.longitude;
            }
        }

        if(point.longitude < smallestLongitude) {
            smallestLongitude = point.longitude;
        } else if(point.longitude > highestLongitude) {
            highestLongitude = point.longitude;
        }

        if((highestLongitude - smallestLongitude) > 180) {
            ArrayList<LatLng> normalizedLatlngs = new ArrayList<>();
            for(LatLng latLng: latLngs) {
                normalizedLatlngs.add(new LatLng(latLng.latitude, latLng.longitude < 0 ? latLng.longitude + 180 : latLng.longitude));
            }
            point = new LatLng(point.latitude, point.longitude < 0 ? point.longitude + 180 : point.longitude);
            return isPointInsideEdges(getEdgesFromLatLngs(normalizedLatlngs), new Point(point));
        } else {
            return isPointInsideEdges(getEdgesFromLatLngs(latLngs), new Point(point));
        }
    }

    private static ArrayList<Edge> getEdgesFromLatLngs(ArrayList<LatLng> latLngs) {
        ArrayList<Edge> edges = new ArrayList<>();
        for(int i = 0; i < latLngs.size(); i++) {
            edges.add(new Edge(latLngs.get(i), i < latLngs.size()-1 ? latLngs.get(i+1) : latLngs.get(0)));
        }

        return edges;
    }

    private static ArrayList<Edge> getEdgesFromPoints(ArrayList<Point> points) {
        ArrayList<Edge> edges = new ArrayList<>();
        for(int i = 0; i < points.size(); i++) {
            edges.add(new Edge(points.get(i), i < points.size()-1 ? points.get(i+1) : points.get(0)));
        }

        return edges;
    }

    /**
     * Assuming that the passed edges form a closed polygon, this method
     * tells whether the given point lies inside or outside the polygon
     *
     * @param edges
     * @param point
     * @return
     */
    private static boolean isPointInsideEdges(ArrayList<Edge> edges, Point point) {
        Log.d("RAY CAST", "Point: " + point.getX() + "; " + point.getY());
        Log.d("RAY CAST", "Lines ******************");
        for(Edge edge : edges) {
            Log.d("RAY CAST", edge.getStartX() + "," + edge.getStartY() + "; " + edge.getEndX() + "," + edge.getEndY());
        }
        Log.d("RAY CAST", "Lines ******************");
        int intersectionCount = 0;

        for(Edge edge : edges) {
            if(LineUtil.linesIntersect(edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY(), point.getX(), point.getY(), Double.MAX_VALUE, Double.MAX_VALUE)) {
                intersectionCount++;
            }
        }
        Log.d("RAY CAST", "intersections: " + intersectionCount);

        return (intersectionCount % 2 != 0);
    }
}
