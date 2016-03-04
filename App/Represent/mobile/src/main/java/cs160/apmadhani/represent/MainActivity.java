package cs160.apmadhani.represent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("REPRESENT!");
    }

    public void goCongressional(View v) {
        Intent intent = new Intent(this, CongressionalMobile.class);
        startActivity(intent);
    }
}
