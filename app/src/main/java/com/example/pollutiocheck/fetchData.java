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
    private String data = "";
    private String stationData = "";
    String dataParsed = "";
    private ArrayList<Integer> ids = new ArrayList<Integer>();
    private ArrayList<Double> offsetScore = new ArrayList<Double>();

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/station/findAll");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            Double[] lat = new Double[500];
            Double[] lon = new Double[500];
            JSONArray JA = new JSONArray(data);

            for(int i = 0; i < JA.length(); i++){
                Integer id = JA.getJSONObject(i).getInt("id");
                String stationName = JA.getJSONObject(i).getString("stationName");
                Double gegrLat = JA.getJSONObject(i).getDouble("gegrLat");
                Double gegrLon = JA.getJSONObject(i).getDouble("gegrLon");
                ids.add(i, id);
                lat[i]=gegrLat;
                lon[i]=gegrLon;
                double offsetPointsLat = MainActivity.latitude - lat[i];
                double offsetPointsLong = MainActivity.longitude - lon[i];
                if(offsetPointsLong < 0) offsetPointsLong = offsetPointsLong * -1;
                if(offsetPointsLat < 0) offsetPointsLat = offsetPointsLat * -1;
                offsetScore.add(i, (offsetPointsLong + offsetPointsLat));

                /*  output info about all stations
                dataParsed = dataParsed +
                                        "Station No: " +
                                        id + "\n" +
                                        "Station name: " +
                                        stationName + "\n" +
                                        "Latitude, Longitude: " +
                                        gegrLat + ", " + gegrLon + "\n" + "\n";
                */
            }

            int minIndex = offsetScore.indexOf(Collections.min(offsetScore));
            dataParsed = dataParsed + "\n" + "Nearest station id: " + ids.get(minIndex);

            for(int i = 0; i < JA.length(); i++) {
                Integer id = JA.getJSONObject(i).getInt("id");
                String stationName = JA.getJSONObject(i).getString("stationName");
                if(ids.get(minIndex) == JA.getJSONObject(i).getInt("id")){
                    dataParsed +=  ", Name: " + stationName + "\n";
                }
            }

            getPollutionInfo(ids.get(minIndex));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    void getPollutionInfo(int index){

        try {
            URL urlStation = new URL("http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + index);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlStation.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null) {
                line = bufferedReader.readLine();
                stationData += line;
            }

            JSONObject obj = new JSONObject(stationData);
            String stCalcDate = obj.getString("stCalcDate");
            dataParsed += "Data collection date: " + stCalcDate;
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.data.setText(dataParsed);

    }
}
