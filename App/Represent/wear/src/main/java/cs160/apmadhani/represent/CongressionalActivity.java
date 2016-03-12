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
    private TextView party;
    private String[] members, parties, titles, callPhone;
    private GridViewPager pager;
    private int screen;
    private int lastUpdate;
    boolean shaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shaken = false;
        Log.wtf("ONCREATE", "HAVE CREATED WATCH");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        makeAdapter(getIntent().getStringExtra("congressmen"));
    }

//    @Override
//    protected void onNewIntent(Intent intent){
//        Log.wtf("ONCREATE", "HAVE CREATED WATCH");
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        setContentView(R.layout.activity_congressional);
//        makeAdapter(getIntent().getStringExtra("congressmen"));
//    }

    protected void makeAdapter(String congressmen) {
        if (congressmen == null) {
            congressmen="";
        }
        String[] tokens =  congressmen.split(";");
        for (int i =0; i <tokens.length; i++) {
            Log.wtf("WOHOO", tokens[i]);
        }
        titles = new String[(tokens.length-4)/3];
        members = new String[(tokens.length-4)/3];
        parties = new String[(tokens.length-4)/3];
        for (int i =0; i<tokens.length-4; i+=3) {
            titles[i/3] = tokens[i];
            members[i/3] = tokens[i+1];
            parties[i/3] = tokens[i+2];
        }
        final double obama = Double.parseDouble(tokens[tokens.length-3]);
        final double romney = Double.parseDouble(tokens[tokens.length-2]);
        final String county = tokens[tokens.length-1];
        callPhone = tokens[tokens.length-4].split("\\~");
        Log.wtf("callPhone", ""+callPhone.length);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final Context c = this;
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                pager = (GridViewPager) findViewById(R.id.pager);
                pager.setAdapter(new CongressmenAdapter(c, getFragmentManager(), titles, members, parties, county, obama, romney, callPhone));
            }
        });
//        try {
//
//            pager = (GridViewPager) findViewById(R.id.pager);
//            pager.setAdapter(new CongressmenAdapter(c, getFragmentManager(), titles, members, parties, county, obama, romney));
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if (!shaken && pager != null &&  Math.pow(Math.pow(event.values[0], 2)+ Math.pow(event.values[1], 2)+ Math.pow(event.values[2], 2), .5)>500) {
                shaken = true;
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("congressperson", "new");
                this.startService(sendIntent);
//                finish();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
