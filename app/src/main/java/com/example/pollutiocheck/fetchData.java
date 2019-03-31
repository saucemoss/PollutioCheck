package com.example.pollutiocheck;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class fetchData extends AsyncTask<Void,Void,Void> {
    private String data = "";
    private ArrayList<Integer> ids = new ArrayList<Integer>();
    private ArrayList<Double> offsetScore = new ArrayList<Double>();
    private Integer id = 0;


    @Override
    protected Void doInBackground(Void... voids) {
        try {

            //set url for all statnios
            URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/station/findAll");

            //set up url connection and get all input to string
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            //set up variable for json array based on input from http
            JSONArray JA = new JSONArray(data);
            getPollutionInfoList(JA, 10);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    // A method for displaying a list of stations nearest to user location
    private void getPollutionInfoList(JSONArray JA, int iterations) throws JSONException {

        // first calculate new offset score array
        getStationsOffsetScore(JA);



        int i = 0;
        while(i != iterations){

            // in list of ids, get index position of minimum value from offset score list
            id = ids.get(offsetScore.indexOf(Collections.min(offsetScore)));

            // add to station list station info based on id
            MainActivity.stationList.add(new StationItems(R.drawable.ic_launcher_foreground, checkForName(JA, id), getPollutionInfo(id)));
            Log.d(checkForName(JA, id), getPollutionInfo(id));
            // remove previous minimum value from offset score and ids list for the next iteration
            offsetScore.remove(offsetScore.indexOf(Collections.min(offsetScore)));
            ids.remove(id);

            i++;

        }

    }

    // A method for calculating offset scoring system between user location and stations.
    // The closer the offset score of a station is to 0, the closer it is to user location.
    private void getStationsOffsetScore(JSONArray JA) throws JSONException {

        // Set up arrays for each coordinates
        Double[] lat = new Double[500];
        Double[] lon = new Double[500];

        // Iterate json array for coordinates and IDs of each station
        for(int i = 0; i < JA.length(); i++){

            // Get the id and add it to array list "ids"
            id = JA.getJSONObject(i).getInt("id");
            ids.add(i, id);

            // subtract coordinates of each station from user current location coordinates and store them as "offsetPoints"
            double offsetPointsLat = MainActivity.latitude - JA.getJSONObject(i).getDouble("gegrLat");
            double offsetPointsLong = MainActivity.longitude - JA.getJSONObject(i).getDouble("gegrLon");

            //  revers sign (-) if offset points are lower than 0, so the score will be always a positive double
            if(offsetPointsLong < 0) offsetPointsLong = offsetPointsLong * -1;
            if(offsetPointsLat < 0) offsetPointsLat = offsetPointsLat * -1;

            // add offset points of latitude and longitude to make up an final score for each station.
            offsetScore.add(i, (offsetPointsLong + offsetPointsLat));
        }
    }


    // A method that will return a string with pollution indexes based on station id
    static String getPollutionInfo(int id){
        String collectedInfo = "";
        try {

            // new URL setup for API request with desired id number of a station
            URL urlStation = new URL("http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlStation.openConnection();

            // parsing all the data to string stationData
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String stationData = "";
                while(line != null) {
                line = bufferedReader.readLine();
                stationData += line;
                }

            // get JSONobject from stationData string
            JSONObject obj = new JSONObject(stationData);

            // look for date/time of pollution reading
            String stCalcDate = obj.getString("stCalcDate");

            // add it to string
            collectedInfo += "Data collection time: " + stCalcDate;

            // create two string arrays data for parsing
            String[] indexList = {"stIndexLevel","so2IndexLevel","no2IndexLevel","pm10IndexLevel","pm25IndexLevel", "o3IndexLevel", "c6h6IndexLevel" };
            String[] paramsList = {"\nAir pollution index: ","\nSulphur dioxide index: ","\nNitrogen dioxide index: ","\nPM10 index: ","\nPM25 index: ","\nOzone index: ","\nBenzene index: "};

            for(int i = 0; i < indexList.length; i++){

                //add the pollution indexes to final string
                collectedInfo += paramsList[i] + checkForNull(obj, indexList[i]);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return collectedInfo;
    }

    // A method for checking if the data exists for pollution index (string name)
    static String checkForNull(JSONObject obj, String name) throws JSONException {
        if(!obj.getString(name).equals("null")) {
            name = obj.getJSONObject(name).getString("indexLevelName");
            return name;
        } else {
            name = "No data available";
            return name;
        }
    }

    // A method that will return string with the name of the station based on id
    static String checkForName(JSONArray arr, int id) throws JSONException {
        String stationName = "";
        for (int i = 0; i < arr.length(); i++) {
            if (arr.getJSONObject(i).getInt("id") == id) {
                if (!arr.getJSONObject(i).getString("stationName").equals("null")) {
                    stationName = arr.getJSONObject(i).getString("stationName");
                } else {
                    stationName = "No data available";
                }
            }
        }
    return stationName;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.listUpdate();

    }
}
