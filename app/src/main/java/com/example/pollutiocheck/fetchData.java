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
    String data = "";
    String dataParsed = "";
    ArrayList<Integer> ids = new ArrayList<Integer>();
    ArrayList<Double> zz = new ArrayList<Double>();



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
            Double[] lat = new Double[2000];
            Double[] lon = new Double[2000];
            JSONArray JA = new JSONArray(data);
            for(int i = 0; i < JA.length(); i++){
                Integer id = JA.getJSONObject(i).getInt("id");
                String stationName = JA.getJSONObject(i).getString("stationName");
                Double gegrLat = JA.getJSONObject(i).getDouble("gegrLat");
                Double gegrLon = JA.getJSONObject(i).getDouble("gegrLon");

                ids.add(i, id);
                lat[i]=gegrLat;
                lon[i]=gegrLon;

                double x1 = MainActivity.latitude;
                double y1 = MainActivity.longitude;
                double x3 = 0;
                double y3 = 0;
                double x4 = 0;
                double y4 = 0;

                x3 = (x1 + lat[i])/2;
                y3 = (y1 + lon[i])/2;

                x4=x1-x3;
                y4=y1-y3;

                if(x4 < 0) x4 = x4 * -1;
                if(y4 < 0) y4 = y4 * -1;
                double z = x4 + y4;

                zz.add(i, z);


                dataParsed = dataParsed +
                                        "Station No: " +
                                        id + "\n" +
                                        "Station name: " +
                                        stationName + "\n" +
                                        "Latitude, Longitude: " +
                                        gegrLat + ", " + gegrLon + "\n" + "\n";

            }

            int minIndex = zz.indexOf(Collections.min(zz));
            dataParsed = dataParsed + "\n" + "Nearest station, id: " + ids.get(minIndex);

            for(int i = 0; i < JA.length(); i++) {
                Integer id = JA.getJSONObject(i).getInt("id");
                String stationName = JA.getJSONObject(i).getString("stationName");
                if(ids.get(minIndex) == JA.getJSONObject(i).getInt("id")){
                    dataParsed = dataParsed +  ", Name: " + stationName;
                }


            }




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.data.setText(dataParsed);
    }
}
