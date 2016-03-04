package cs160.apmadhani.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CongressionalMobile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Hey", "This was created");
        setContentView(R.layout.activity_congressional_mobile);
        Intent i = getIntent();
        if (i.hasExtra("congressmen")) {
            String[] c = i.getStringArrayExtra("congressmen");
            ((TextView) findViewById(R.id.member_name)).setText(c[0]);
            ((TextView) findViewById(R.id.member_name2)).setText(c[1]);
            ((TextView) findViewById(R.id.member_name3)).setText(c[2]);
        }

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        String congresspeople = "Dianne Feinstein;Independent/Barbara Boxer;Democrat/Barbara Lee;Republican";
        sendIntent.putExtra("congresspeople", congresspeople);
        startService(sendIntent);
    }

    public void goDetailed(View v) {
        Intent intent = new Intent(this, DetailedView.class);
        startActivity(intent);
    }
}
