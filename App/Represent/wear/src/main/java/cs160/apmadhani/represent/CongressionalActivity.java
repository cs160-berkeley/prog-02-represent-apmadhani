package cs160.apmadhani.represent;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

public class CongressionalActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView member;
    private RelativeLayout watchface;
    private TextView party;
    private String[] members;
    private String[] parties;
    private GridViewPager pager;
    private int screen;
    private int lastUpdate;
    boolean shaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shaken = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        members = new String[]{"Dianne Feinstein", "Barbara Boxer", "Barbara Lee"};
        parties = new String[]{"Independent", "Democrat", "Republican"};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final Context c = this;
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                watchface = (RelativeLayout) findViewById(R.id.congressionalwatch);
                pager = (GridViewPager) findViewById(R.id.pager);
                pager.setAdapter(new CongressmenAdapter(c, getFragmentManager(), members, parties, "Santa Clara County", 54, 46));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("Hello", event.sensor.getStringType());
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            Log.e("YEE", event.values[0]+" "+event.values[1]+" "+event.values[2]);
            Log.e("YEE2", ""+ (pager == null));
            if (!shaken && pager != null &&  Math.pow(Math.pow(event.values[0], 2)+ Math.pow(event.values[1], 2)+ Math.pow(event.values[2], 2), .5)>500) {
                pager.setAdapter(new CongressmenAdapter(this, getFragmentManager(), new String[]{"John McCain", "Bernie Sanders", "Marco Rubio"},
                        new String[]{"Republican", "Democrat", "Independent"},
                        "Alameda County", 30, 69));
                shaken = true;
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("congressperson", "new");
                this.startService(sendIntent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
