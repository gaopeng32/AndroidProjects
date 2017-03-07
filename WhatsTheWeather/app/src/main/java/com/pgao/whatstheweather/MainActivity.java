package com.pgao.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText cityName;
    private TextView resultTextView;

    // Run a task on the background thread
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                // Convert the string to url
                url = new URL(params[0]);

                // Setup url connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // Read input of data by character
                InputStream in  = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }

                return result;
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
//                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) { // called when doInBackground() method finishes
            super.onPostExecute(result);
//            Log.i("JSON content", result);

            // Process JSON
            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);

                // Extract the weather part
                String weatherInfo = jsonObject.getString("weather");

                // Convert to JSON array
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (!main.isEmpty() && !description.isEmpty()) {
                        message += main + ": " + description + "\n";
                    }
                }

                if (!message.isEmpty()) {
                    resultTextView.setText(message);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }

            } catch (JSONException e) { // invalid input
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }

        }
    }

    public void findWeather(View view) {
        // Remove keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        // API call for weather info
        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            String apiCall = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=16e8e52c2f9599eea629881d9ee85f6a", encodedCityName);
            DownloadTask task = new DownloadTask();
            task.execute(apiCall);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }
}
