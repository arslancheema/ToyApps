package com.example.aarshad.toyapps;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class EggTimer extends AppCompatActivity {

    SeekBar timerSeekbar;
    TextView timerTextView;
    Button controllerButton;
    Boolean counterIsActive = false ;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_timer);

        timerSeekbar = (SeekBar) findViewById(R.id.timerSeekbar);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        controllerButton = (Button) findViewById(R.id.controllerButton);
        timerSeekbar.setMax(600);
        timerSeekbar.setProgress(30);

        timerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                updateTimer (progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void controlTimer (View view){

        if (counterIsActive == false) {
            counterIsActive = true;
            timerSeekbar.setEnabled(false);
            controllerButton.setText("Stop");

            // new CountDownTimer(timeToRun,TimeToTick) & +100 is to fix the delay
            countDownTimer = new CountDownTimer(timerSeekbar.getProgress() * 1000 + 100, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    timerTextView.setText("0:00");
                    Log.i("EggTimer", "Timer Done ! ");

                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.air_horn);
                    mediaPlayer.start();
                }

            }.start();
        } else {
            timerTextView.setText("0:30");
            timerSeekbar.setProgress(30);
            timerSeekbar.setEnabled(true);
            countDownTimer.cancel();
            controllerButton.setText("Go");
            counterIsActive = false;
        }
    }

    public void updateTimer (int secondsLeft){
        int minutes = (int) secondsLeft/60;
        // Remaining seconds
        int seconds = secondsLeft - minutes * 60 ;

        timerTextView.setText(Integer.toString(minutes) + ":" + Integer.toString(seconds));
    }
}
