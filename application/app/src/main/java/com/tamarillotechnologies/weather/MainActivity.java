package com.tamarillotechnologies.weather;
// Tamarillo Technologies Private Limited is a Private Tech. Company owned by Shubhadeep Mandal, Sapnanil Das and Saptorshi Majumder.

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText weatherEditText;
    TextView resultTextView,cityTextView,statusTextView,temperatureTextView;
    ImageView iconImageView;
    View view1;


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                String message1 = "",message2 = "",message3 = "", message4 = "", icon = "",main = "",description = "",maxtemp = "",mintemp = "",feelslike = "",pressure = "",humidity = "",windspeed = "";
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String mainInfo = jsonObject.getString("main");
                String windInfo = jsonObject.getString("wind");

                JSONArray weatherData = new JSONArray(weatherInfo);
                for (int i=0; i < weatherData.length(); i++) {

                    JSONObject jsonPart1 = weatherData.getJSONObject(i);
                    main = jsonPart1.getString("main");
                    description = jsonPart1.getString("description");
                    icon = jsonPart1.getString("icon");
                }

                ImageDownloader imageTask = new ImageDownloader();
                Bitmap myImage;
                try {
                    myImage = imageTask.execute("https://openweathermap.org/img/wn/" + icon + "@2x.png").get();
                    iconImageView.setImageBitmap(myImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject jsonPart2 = new JSONObject(mainInfo);
                maxtemp = jsonPart2.getString("temp_max");
                mintemp = jsonPart2.getString("temp_min");
                feelslike = jsonPart2.getString("feels_like");
                pressure = jsonPart2.getString("pressure");
                humidity = jsonPart2.getString("humidity");

                JSONObject jsonPart3 = new JSONObject(windInfo);
                windspeed = jsonPart3.getString("speed");

                double doublemax = Double.parseDouble(maxtemp);  /**Converting the Maximum Temperature from Kelvin (default unit) to Celsius*/
                doublemax = doublemax - 273.15;
                int intmax = (int) doublemax;
                String finalmaxtemp = String.valueOf(intmax);

                double doublemin = Double.parseDouble(mintemp);  /**Converting the Minimum Temperature from Kelvin to Celsius*/
                doublemin = doublemin - 273.15;
                int intmin = (int) doublemin;
                String finalmintemp = String.valueOf(intmin);

                double doublefeel = Double.parseDouble(feelslike);  /**Converting the Average (Feels Like) Temperature from Kelvin to Celsius*/
                doublefeel = doublefeel - 273.15;
                int intfeel = (int) doublefeel;
                String finalfeelslike = String.valueOf(intfeel);

                message1 = weatherEditText.getText().toString();
                message2 = main;
                message3 = finalfeelslike + "°C";
                message4 = "Description : " + description + "\nTemperature : " + finalmaxtemp + "°C / " + finalmintemp +
                        "°C\nFeels Like  : " + finalfeelslike + "°C\nPressure    : " + pressure + "hPa\nHumidity    : " + humidity + "%\nWind Speed  : " + windspeed + "m/s";

                if(!message1.equals("")){
                    cityTextView.setText(message1);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
                }
                if(!message2.equals("")){
                    statusTextView.setText(message2);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
                }
                if(!message3.equals("")){
                    temperatureTextView.setText(message3);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
                }
                if(!message4.equals("")){
                    resultTextView.setText(message4);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getWeather(View view){
        try {
           cityTextView.setVisibility(View.VISIBLE);
            statusTextView.setVisibility(View.VISIBLE);
            temperatureTextView.setVisibility(View.VISIBLE);
            resultTextView.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(weatherEditText.getText().toString(), "UTF-8");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=5825332f8b8671d5b0805fd5c288f8d9");
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(weatherEditText.getWindowToken(), 0);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry !! couldn't access weather data :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        weatherEditText = (EditText) findViewById(R.id.weatherEditText);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        cityTextView.setVisibility(View.INVISIBLE);
        statusTextView.setVisibility(View.INVISIBLE);
        temperatureTextView.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.INVISIBLE);
        view1 = (View) findViewById(R.id.view1);
        view1.setVisibility(View.INVISIBLE);
    }
}