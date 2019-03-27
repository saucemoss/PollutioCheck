package com.example.pollutiocheck;

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
            String stIndexLevel = obj.getJSONObject("stIndexLevel").getString("indexLevelName");
            String so2IndexLevel = obj.getJSONObject("so2IndexLevel").getString("indexLevelName");
            String no2IndexLevel = obj.getJSONObject("no2IndexLevel").getString("indexLevelName");
            String pm10IndexLevel = obj.getJSONObject("pm10IndexLevel").getString("indexLevelName");
            String pm25IndexLevel = obj.getString("pm25IndexLevel");
            dataParsed += "Ostatnia data pomiaru: " + stCalcDate +
                            " \n Air pollution index: " + stIndexLevel +
                            " \n Sulphur dioxide index: " + so2IndexLevel +
                            " \n Nitrogen dioxide index: " + no2IndexLevel +
                            " \n PM10 index: " + pm10IndexLevel +
                            " \n PM25 index: " + pm25IndexLevel;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            dataParsed += "JSON excepiton";
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.data.setText(dataParsed);
    }
}
