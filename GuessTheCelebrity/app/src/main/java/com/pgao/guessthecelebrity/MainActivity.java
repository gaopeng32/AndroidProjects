package com.pgao.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    HTMLDownloader htmlDownloader;
    ImageDownloader imageDownloader;
    HashMap<String, String> celebNamesToURLs;
    List<String> keys;
    Random rand;
    int chosenCeleb;
    int locationOfCorrectAnswer = 0; // 0-3
    String[] answers = new String[4];
    ImageView imageView;

    public void celebChosen(View view) {
        int buttonTagged = Integer.parseInt(view.getTag().toString());

        // Check answer
        if (buttonTagged == locationOfCorrectAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Wrong! It was " + keys.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        // Create a new question
        createNewQuestion();
    }


    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private class HTMLDownloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in  = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    result += (char) data;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }


    private void startGame() {
        String rawHTML = null;
        try {
            // Download the HTML resource
            rawHTML = htmlDownloader.execute("http://www.posh24.se/kandisar").get();
//            Log.i("HTML", htmlContent);
            String formerSplit = rawHTML.split("<div class=\"sidebarContainer\">")[0]; // not a great way for realiability

            // Get the name and url of each image
            Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\""); // image source
            Matcher m = p.matcher(formerSplit);
            while (m.find()) {
                // Store in celebNamesToURLs
                celebNamesToURLs.put(m.group(2), m.group(1));
            }

            keys = new ArrayList<String>(celebNamesToURLs.keySet());


            // Create new question
            createNewQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewQuestion() {
        // Generate answers
        chosenCeleb = rand.nextInt(keys.size());
        locationOfCorrectAnswer = rand.nextInt(4);
        for (int i = 0; i < answers.length; i++) {
            if (i == locationOfCorrectAnswer) {
                answers[i] = keys.get(chosenCeleb);
            }
            else {
                int tmp = rand.nextInt(keys.size());
                while (tmp == chosenCeleb) {
                    tmp = rand.nextInt(keys.size());
                }
                answers[i] = keys.get(tmp);
            }
        }

        // Chosen celebrity
        String celebName = keys.get(chosenCeleb);
        String celebURL = celebNamesToURLs.get(celebName);

        // Update buttons
        for (int i = 0; i < 4; i++) {
            getButton(i).setText(answers[i]);
        }

        // Download the bitmap image
        Bitmap celebImage = null;
        try {
            imageDownloader = new ImageDownloader(); // async task can be executed only once
            celebImage = imageDownloader.execute(celebURL).get();
            imageView.setImageBitmap(celebImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Button getButton(int index) {
        int[] buttonIDArray = {R.id.button0, R.id.button1, R.id.button2, R.id.button3};
        Button button = (Button) findViewById(buttonIDArray[index]);
        return button;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebNamesToURLs = new HashMap<>();
        rand = new Random();
        htmlDownloader = new HTMLDownloader();
        imageView = (ImageView) findViewById(R.id.imageView);

        startGame();

    }



}
