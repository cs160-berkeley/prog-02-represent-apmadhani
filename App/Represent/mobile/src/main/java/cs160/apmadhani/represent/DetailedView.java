package cs160.apmadhani.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DetailedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        if(getIntent().hasExtra("congressmen")) {
            String member = getIntent().getStringExtra("congressmen");
            TextView tv = (TextView) findViewById(R.id.member_name_detailed);
            if(member.equals("Dianne Feinstein") || member.equals("Marco Rubio")) {
                tv.setText(member + " (I)");
                findViewById(R.id.candidate_card_detailed).setBackgroundColor(0xFF424242);
                findViewById(R.id.billview).setBackgroundColor(0xFF424242);
                findViewById(R.id.committees).setBackgroundColor(0xFF424242);
            } else if(member.equals("Bernie Sanders") || member.equals("Barbara Boxer")) {
                tv.setText(member + " (D)");
                findViewById(R.id.candidate_card_detailed).setBackgroundColor(0xFF2979FF);
                findViewById(R.id.billview).setBackgroundColor(0xFF2979FF);
                findViewById(R.id.committees).setBackgroundColor(0xFF2979FF);
            } else {
                tv.setText(member + " (R)");
                findViewById(R.id.candidate_card_detailed).setBackgroundColor(0xFFF50057);
                findViewById(R.id.billview).setBackgroundColor(0xFFF50057);
                findViewById(R.id.committees).setBackgroundColor(0xFFF50057);
            }
        }
    }

}
