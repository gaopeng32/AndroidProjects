package com.pgao.eggtimer;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SeekBar timerSeekBar;
    private TextView timerTextView;
    private Button controllerButton;
    private CountDownTimer countDownTimer;
    private boolean counterIsActive = false;


    private void updateTimer(int secondsLeft) {
        // Convert from "seconds" to "minutes:seconds"
        int minutes = (int) secondsLeft / 60;
        int seconds = (int) secondsLeft % 60;

        // Set the time information
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void resetTimer() {
        // Disable the counter
        counterIsActive = false;

        // Enable the timer seekbar
        timerSeekBar.setEnabled(true);
        timerSeekBar.setProgress(0);

        // Update the button text
        controllerButton.setText("Go!");

        // Stop the timer
        countDownTimer.cancel();

        // Reset the time info
        updateTimer(0);
    }


    public void controlTimer(View view) {

        if (!counterIsActive) {
            // Enable the counter
            counterIsActive = true;

            // Disable the timer seekbar
            timerSeekBar.setEnabled(false);

            // Update the button text
            controllerButton.setText("Stop");

            // Count-down timer
            countDownTimer = new CountDownTimer(timerSeekBar.getProgress() * 1000 + 100, 1000) {
                // The reason why we add 100 is that onTick is called a few milliseconds after,
                // thus, the rounding to an int makes it reduce one second

                @Override
                public void onTick(long millisUntilFinished) {
                    // Update timer
                    updateTimer((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    // Reset timer
                    resetTimer();

                    // Play the sound
                    Log.i("Finished", "Timer done");
                    MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.air_horn);
                    mPlayer.start();

                }
            };
            countDownTimer.start();
        }
        else {
            resetTimer();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Timer textview (shown on the egg)
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        // Controller button
        controllerButton = (Button) findViewById(R.id.controllerButton);

        // Timer seekbar
        timerSeekBar = (SeekBar)findViewById(R.id.timerSeekBar);
        timerSeekBar.setMax(10 * 60); // 10 min
        timerSeekBar.setProgress(0); // start

        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateTimer(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
}
