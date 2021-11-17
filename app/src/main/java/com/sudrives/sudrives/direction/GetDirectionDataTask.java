package com.sudrives.sudrives.direction;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by motionmagik on 1/25/17.
 */

public class GetDirectionDataTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private String origin="", destination="";



    public GetDirectionDataTask(Context context){
        mContext=context;
    }

    // Downloading data in non-fragment thread
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            data = downloadUrl(url[0]);
            origin=url[1];
            destination=url[2];
        }catch(Exception e){
           // Log.d("Background Task",e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

       // Log.e("Results", ""+result);

        DrawPolylineTask parserTask = new DrawPolylineTask(mContext);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result, origin, destination);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
         //   Log.d("Error in Request", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    public String getgoogleDirectionUrl(String origin, String destination, String serverKey){
        String GOOGLE_DIRECTIONURL="https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&key="+serverKey+"&sensor=false&mode=driving&alternatives=true";
        Log.e("googledirectionapi", GOOGLE_DIRECTIONURL);
        return GOOGLE_DIRECTIONURL;
    }
}
