package com.sudrives.sudrives.direction;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;


import com.sudrives.sudrives.utils.Config;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by motionmagik on 1/25/17.
 */

public class DrawPolylineTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private Context mContext;
    private onDrawPolylineTaskComplete taskComplete;
    private String origin="", destination="";


    public DrawPolylineTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            origin=jsonData[1];
            destination=jsonData[2];
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        // Traversing through all the routes

        try {
            if(result.size()>0) {
                for (int i = 0; i < 1; i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);


                    lineOptions.color(Config.PATH_TRACKING ? Color.BLUE : Color.BLACK);
                }
            }
            taskComplete = (onDrawPolylineTaskComplete) mContext;
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions!=null) {
                taskComplete.drawPolyline(lineOptions, origin,destination);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public interface onDrawPolylineTaskComplete {
        public void drawPolyline(PolylineOptions lineOptions, String origin, String destination);


    }
}