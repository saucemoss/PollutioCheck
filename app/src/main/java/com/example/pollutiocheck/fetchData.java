package com.example.pollutiocheck;

import android.graphics.Color;
import android.os.AsyncTask;

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
    String data = "";
    String dataParsed = "";
    ArrayList<Integer> ids = new ArrayList<Integer>();
    ArrayList<Double> offsetScore = new ArrayList<Double>();
    Integer id = 0;

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

            // get the lowest offsets score location in offset score array and store it as minIndex.
            //int minIndex = offsetScore.indexOf(Collections.min(offsetScore));

            // get and printout ID from "ids" array based on location of lowest score in offset score array.
            // dataParsed = dataParsed + "Nearest station id: " + ids.get(minIndex);

            // printout the nearest station name
            // dataParsed += checkForName(JA, ids.get(minIndex));

            // printout pollution of nearest station
            //getPollutionInfo(ids.get(minIndex));

            // get 10 station ids nearest to user
            //
            getPollutionInfoList(JA, 10);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            dataParsed += e;
        }
        return null;
    }

    private void getStationsOffsetScore(JSONArray JA) throws JSONException {
        //set up arrays for each coordinates
        Double[] lat = new Double[500];
        Double[] lon = new Double[500];

        //iterate json array for coordinates data and ID of each polution reading station
        for(int i = 0; i < JA.length(); i++){
            // get the id and add it to array list "ids"
            id = JA.getJSONObject(i).getInt("id");
            ids.add(i, id);

            //get coordinates and add them to arrays
            Double gegrLat = JA.getJSONObject(i).getDouble("gegrLat");
            Double gegrLon = JA.getJSONObject(i).getDouble("gegrLon");
            lat[i]=gegrLat;
            lon[i]=gegrLon;

            // subtract coordinates of each station from user current location cooridnates and store them as "offsetPoints"
            double offsetPointsLat = MainActivity.latitude - lat[i];
            double offsetPointsLong = MainActivity.longitude - lon[i];

            //  revers sign (-) if offset points are lower than 0
            if(offsetPointsLong < 0) offsetPointsLong = offsetPointsLong * -1;
            if(offsetPointsLat < 0) offsetPointsLat = offsetPointsLat * -1;

            // add offset points of latitude and longitude to make up an final score for each station.
            // The lower the offset score is, the closer a station is to user location
            offsetScore.add(i, (offsetPointsLong + offsetPointsLat));
        }
    }

    private void getPollutionInfoList(JSONArray JA, int iterations) throws JSONException {
        getStationsOffsetScore(JA);
        int i = 0;
        while(i != iterations) {
            // in list of ids, get index position of minimum value from offset score list
            id = ids.get(offsetScore.indexOf(Collections.min(offsetScore)));
            dataParsed += "Next closest station id " + id + ", " + checkForName(JA, id);

            getPollutionInfo(id);

            //remove previous id from ids list and minimum value from offset score list for next iteration
            ids.remove(id);
            offsetScore.remove(offsetScore.indexOf(Collections.min(offsetScore)));

            dataParsed += "\n\n";
            i++;
        }
    }

    void getPollutionInfo(int id){

        try {
            URL urlStation = new URL("http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlStation.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String stationData = "";
                while(line != null) {
                line = bufferedReader.readLine();
                stationData += line;
                }

            JSONObject obj = new JSONObject(stationData);

            String stCalcDate = obj.getString("stCalcDate");
            dataParsed += "\nData collection date: " + stCalcDate;
            String[] indexList = {"stIndexLevel","so2IndexLevel","no2IndexLevel","pm10IndexLevel","pm25IndexLevel", "o3IndexLevel", "c6h6IndexLevel" };
            String[] paramsList = {"\nAir pollution index: ","\nSulphur dioxide index: ","\nNitrogen dioxide index: ","\nPM10 index: ","\nPM25 index: ","\nOzone index: ","\nBenzene index: "};
            for(int i = 0; i < indexList.length; i++){

                dataParsed += paramsList[i] + checkForNull(obj, indexList[i]);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            dataParsed += e;
        }
    }

    String checkForNull(JSONObject obj, String name) throws JSONException {
        if(!obj.getString(name).equals("null")) {
            name = obj.getJSONObject(name).getString("indexLevelName");
            return name;
        } else {
            name = "No data available";
            return name;
        }
    }

    String checkForName(JSONArray arr, int id) throws JSONException {
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
        MainActivity.data.setText(dataParsed);

    }
}
