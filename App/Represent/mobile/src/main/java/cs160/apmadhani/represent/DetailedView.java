package cs160.apmadhani.represent;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DetailedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        Intent intent = getIntent();
        long tweet = intent.getLongExtra("Tweet", 0);
        String name = intent.getStringExtra("Name");
        String party = intent.getStringExtra("Party");
        String termend = intent.getStringExtra("Termend");
        String id = intent.getStringExtra("ID");
        final String website = intent.getStringExtra("Website");
        final String email = intent.getStringExtra("Email");
        final RelativeLayout twitter = (RelativeLayout) findViewById(R.id.twitter);
        TweetUtils.loadTweet(tweet, new Callback<Tweet>() {

            @Override
            public void success(Result<Tweet> result) {
                Tweet tweet = result.data;
                twitter.addView(new CompactTweetView(DetailedView.this, tweet));
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
        ((TextView) this.findViewById(R.id.learnmore)).setText("Term ends "+termend.substring(5, 7)+"/"+termend.substring(8, 10)+"/"+termend.substring(2,4));
        ((TextView) this.findViewById(R.id.member_name)).setText(name);
        ((ImageView) this.findViewById(R.id.mailIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("YAY", email);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.setType("message/rfc822");
                DetailedView.this.startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });
        ((ImageView) this.findViewById(R.id.webIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("YAY", "I have finished 2");
                Uri uri = Uri.parse(website);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                DetailedView.this.startActivity(intent);
            }
        });

        RelativeLayout card = (RelativeLayout) findViewById(R.id.candidate_card);
        RelativeLayout committees = (RelativeLayout) findViewById(R.id.committees);
        RelativeLayout bills = (RelativeLayout) findViewById(R.id.billview);
        StringBuffer response = new StringBuffer();
        JSONObject j;

        if (party.equals("D")) {
            card.setBackgroundColor(0xFF4099FF);
            committees.setBackgroundColor(0xFF4099FF);
            bills.setBackgroundColor(0xFF4099FF);
        } else if (party.equals("R")) {
            card.setBackgroundColor(0xFFFF6759);
            committees.setBackgroundColor(0xFFFF6759);
            bills.setBackgroundColor(0xFFFF6759);
        } else {
            card.setBackgroundColor(0xFF424242);
            committees.setBackgroundColor(0xFF424242);
            bills.setBackgroundColor(0xFF424242);
        }

        try {
            URL url = new URL("http://congress.api.sunlightfoundation.com/committees?member_ids="+id+"&apikey=5c8e96411f2049a9ad6f00f622849bbf");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            Log.wtf("SON", "\nSending 'GET' request to URL : " + url);
            Log.wtf("Response Code : ", ""+ responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.wtf("input", inputLine);
            }
            in.close();
            j = new JSONObject(response.toString());
            JSONArray results = j.getJSONArray("results");
            String committeeName = "";
            for(int i = 0; i < results.length(); i++) {
                committeeName += ((JSONObject) results.get(i)).getString("name")+"\n\n";
            }
            ((TextView) findViewById(R.id.committeeNames)).setText(committeeName);



            url = new URL("http://congress.api.sunlightfoundation.com/bills?sponsor_id="+id+"&apikey=5c8e96411f2049a9ad6f00f622849bbf");
            con = (HttpURLConnection) url.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            responseCode = con.getResponseCode();
            Log.wtf("SON", "\nSending 'GET' request to URL : " + url);
            Log.wtf("Response Code : ", ""+ responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.wtf("input", inputLine);
            }
            in.close();
            j = new JSONObject(response.toString());
            results = j.getJSONArray("results");
            String billNames = "";
            String billDates = "";
            for(int i = 0; i < results.length(); i++) {
                String shortTitle = ((JSONObject) results.get(i)).getString("short_title");
                if (shortTitle == null || shortTitle.equals("null")) {
                    shortTitle = ((JSONObject) results.get(i)).getString("official_title");
                }
                String date = ((JSONObject) results.get(i)).getString("introduced_on");
                billDates += date.substring(5, 7)+"/"+date.substring(8, 10)+"/"+date.substring(2,4)+"\n\n";
                while (shortTitle.length() > 20 && shortTitle.indexOf(" ", 20) != -1) {
                    int cutoff = shortTitle.indexOf(" ", 20);
                    billNames += shortTitle.substring(0, cutoff)+"\n";
                    shortTitle = shortTitle.substring(cutoff+1);
                    billDates += "\n";
                }
                billNames += shortTitle+"\n\n";
            }
            ((TextView) findViewById(R.id.billnames)).setText(billNames);
            ((TextView) findViewById(R.id.billdates)).setText(billDates);

        } catch (IOException |JSONException e) {
            e.printStackTrace();
        }

    }

}
