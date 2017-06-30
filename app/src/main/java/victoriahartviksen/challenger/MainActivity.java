package victoriahartviksen.challenger;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;


public class MainActivity extends AppCompatActivity {

    private Chronometer main_timer;
    private long elapsed_time;
    private long ten_seconds = 10000;
    private long two_minutes = 120000; //120000
    private boolean end_beeped = false;
    private boolean warning_beep = false;
    private long get_elapsed_time() {
        return SystemClock.elapsedRealtime() - main_timer.getBase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        elapsed_time = 0;


        main_timer = (Chronometer)findViewById(R.id.mainTimer);
        main_timer.setBase(SystemClock.elapsedRealtime());
        Button startbutton = (Button)findViewById(R.id.start_button);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_timer.setBase(SystemClock.elapsedRealtime() - elapsed_time);
                main_timer.start();
            }
        });


        Button stopbutton = (Button)findViewById(R.id.stop_button);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_timer.stop();
                elapsed_time = SystemClock.elapsedRealtime() - main_timer.getBase();

            }
        });

        Button restartbutton = (Button)findViewById(R.id.restart_button);
        restartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_timer.stop();
                elapsed_time = 0;
                main_timer.setBase(SystemClock.elapsedRealtime());
                end_beeped = false;
                warning_beep = false;

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elapsed_time = get_elapsed_time();

                if (elapsed_time >= two_minutes) {
                    SendPIntent();

                } else {
                    Snackbar.make(view, "You have to do 2 mins", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        main_timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                elapsed_time = get_elapsed_time();
                //2 mins have passed
                if (!end_beeped) {
                    if (elapsed_time >= two_minutes) {

                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
                        try {
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                            Thread.sleep(200);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                            Thread.sleep(200);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                            end_beeped = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (elapsed_time >= (two_minutes - ten_seconds) && !warning_beep) {
                        warning_beep = true;
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected  void SendPIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "P");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }
}
