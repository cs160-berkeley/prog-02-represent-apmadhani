package cs160.apmadhani.represent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Akshay on 3/3/16.
 */
public class CongressmanFragment extends Fragment {
    String politician, title;
    String party;
    String callPhone;
    boolean election;
    String county;
    double romney;
    double obama;
    TextView name;
    Context ctx;
    public CongressmanFragment() {
        super();
    }

    public CongressmanFragment(Context ctx, String title, String politician, String party, String callPhone){
        super();
        this.politician = politician;
        this.party = party;
        this.election = false;
        this.ctx = ctx;
        this.title = title;
        this.callPhone = callPhone;
    }

    public CongressmanFragment(Context ctx, String county, double romney, double obama){
        this.election = true;
        this.county = county;
        this.romney = romney;
        this.obama = obama;
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (election) {
            View v = inflater.inflate(R.layout.election_card, container, false);
            ((TextView)v.findViewById(R.id.county)).setText(county);
            ((TextView)v.findViewById(R.id.romney)).setText("Romney - "+romney+"%");
            ((TextView)v.findViewById(R.id.obama)).setText("Obama - "+obama+"%");
            RelativeLayout card = (RelativeLayout)v.findViewById(R.id.card);
            if(obama > romney) {
                card.setBackgroundColor(0xFF4099FF);
            } else if (romney > obama) {
                card.setBackgroundColor(0xFFFF6759);
            } else {
                card.setBackgroundColor(0xFF424242);
            }
            return v;
        }
        View v = inflater.inflate(R.layout.congress_card, container, false);
        name = ((TextView)v.findViewById(R.id.congressman));
        name.setText(politician);
        if(title.equalsIgnoreCase("sen")) {
            ((TextView) v.findViewById(R.id.title)).setText("Senator");
        }
        ((TextView)v.findViewById(R.id.party)).setText(party);
        RelativeLayout card = (RelativeLayout)v.findViewById(R.id.card);
        if (party != null && party.equals("Democrat")) {
            card.setBackgroundColor(0xFF4099FF);
        } else if (party != null && party.equals("Republican")) {
            card.setBackgroundColor(0xFFFF6759);
        } else {
            card.setBackgroundColor(0xFF424242);
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(ctx, WatchToPhoneService.class);
                sendIntent.putExtra("congressperson", callPhone);
                ctx.startService(sendIntent);
            }
        });
        return v;
    }

}
