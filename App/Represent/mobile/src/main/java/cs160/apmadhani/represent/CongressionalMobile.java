package cs160.apmadhani.represent;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Result;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.OAuthSigning;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;
import retrofit.http.GET;

public class CongressionalMobile extends AppCompatActivity {

    ListView congresslist;
    Tweet[] tweets;
    int amt;
    int tweetsRecieved = 0;
    String[] people;
    String[] parties;
    String[] emails;
    String[] websites;
    String congresspeople;
    String[] termend;
    String[] id;
    final Context ctx = this;



    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "HnBID3H7Z4366yw4YPoVgghrM";
    private static final String TWITTER_SECRET = "2pHS4usLGs7DXkFaA0ELKkXwy4ITA8dDRU7C17wd8SXGZhl2yj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StringBuffer response = new StringBuffer();
        JSONObject j;

        setContentView(R.layout.activity_congressional_mobile);
        Intent intent = getIntent();
        congresslist = (ListView) findViewById(R.id.congresslist);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        BufferedReader in;
        String countyfinder="";
        try {
            URL url = null;
            if(intent.hasExtra("congressmen") && intent.getStringExtra("congressmen").equals("new")) {
                Log.wtf("IN NEW", "MAKING NEW");
                InputStream is = getAssets().open("election-county-2012.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                JSONArray election = new JSONArray(new String(buffer, "UTF-8"));
                String s = "http://maps.googleapis.com/maps/api/geocode/json?address="+election.getJSONObject((int) (Math.random()*election.length())).getString("county-name").replace(" ", "")+"county";
                Log.wtf("ADDRESS", s);
                url = new URL(s);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                response = new StringBuffer();
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject loc = new JSONObject(response.toString()).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                double latitude = loc.getDouble("lat");
                double longitude = loc.getDouble("lng");
                countyfinder = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=false";
                url = new URL("http://congress.api.sunlightfoundation.com/legislators/locate?latitude="+latitude+"&longitude="+longitude+"&apikey=5c8e96411f2049a9ad6f00f622849bbf");
            } else if (intent.hasExtra("zipcode")) {
                countyfinder = "http://maps.googleapis.com/maps/api/geocode/json?address="+intent.getStringExtra("zipcode")+"&sensor=false";
                url = new URL("http://congress.api.sunlightfoundation.com/legislators/locate?zip="+intent.getStringExtra("zipcode").toString()+"&apikey=5c8e96411f2049a9ad6f00f622849bbf");
            } else if (intent.hasExtra("longitude")) {
                countyfinder = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+intent.getStringExtra("latitude")+","+intent.getStringExtra("longitude")+"&sensor=false";
                url = new URL("http://congress.api.sunlightfoundation.com/legislators/locate?latitude="+intent.getStringExtra("latitude").toString()+"&longitude="+intent.getStringExtra("longitude").toString()+"&apikey=5c8e96411f2049a9ad6f00f622849bbf");
            }
            Log.wtf("HOST", url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            Log.wtf("SON", "\nSending 'GET' request to URL : " + url);
            Log.wtf("Response Code : ", ""+ responseCode);
            response = new StringBuffer();
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.wtf("input", inputLine);
            }
            in.close();
            j = new JSONObject(response.toString());
            JSONArray results = j.getJSONArray("results");
            people = new String[results.length()];
            parties = new String[results.length()];
            emails = new String[results.length()];
            websites = new String[results.length()];
            tweets = new Tweet[results.length()];
            termend = new String[results.length()];
            id = new String[results.length()];
            amt = results.length();
            congresspeople="";
            for (int i = 0 ; i < results.length(); i++) {
                JSONObject person = results.getJSONObject(i);
                String party = person.getString("party");
                if(party.equals("D")) {
                    congresspeople += person.getString("title")+";" +person.getString("first_name") + " " + person.getString("last_name") + ";Democrat;";
                } else if (party.equals("R")) {
                    congresspeople += person.getString("title")+";" +person.getString("first_name") + " " + person.getString("last_name") + ";Republican;";
                } else {
                    congresspeople += person.getString("title")+";" +person.getString("first_name") + " " + person.getString("last_name") + ";Independent;";
                }
                Log.wtf("CHECKTHIS", congresspeople);
                people[i] = person.getString("title")+". " + person.getString("first_name")+" "+person.getString("last_name")+" ("+person.getString("party")+")";
                parties[i] = person.getString("party");
                websites[i] = person.getString("website");
                emails[i] = person.getString("oc_email");
                termend[i] = person.getString("term_end");
                id[i] = person.getString("bioguide_id");
                getTweetID(person.getString("twitter_id"), authConfig, i, countyfinder);
            }
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }
    }

    public void launchWatch(String link, String congresspeople) throws IOException, JSONException {
        URL url = new URL(link);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        StringBuffer response = new StringBuffer();

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
        String county = "";
        String state = "";
        JSONObject j = new JSONObject(response.toString());
        JSONArray results = ((JSONObject) j.getJSONArray("results").get(0)).getJSONArray("address_components");
        for(int i = 0; i < results.length(); i++) {
            String s = results.getJSONObject(i).getJSONArray("types").getString(0);
            if(s.equals("administrative_area_level_2")) {
                county = results.getJSONObject(i).getString("short_name");
            }
            if(s.equals("administrative_area_level_1")) {
                state = results.getJSONObject(i).getString("short_name");
            }
        }
        Log.wtf("COUNTY", county);
        Log.wtf("STATE", state);
        InputStream is = getAssets().open("election-county-2012.json");
        int size = is.available();
        double obama = 0, romney = 0;
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        JSONArray election = new JSONArray(new String(buffer, "UTF-8"));
        for(int i = 0 ; i < election.length(); i++) {
            if(election.getJSONObject(i).getString("state-postal").equals(state) && (election.getJSONObject(i).getString("county-name") + " county").equalsIgnoreCase(county)) {
                obama = election.getJSONObject(i).getDouble("obama-percentage");
                romney = election.getJSONObject(i).getDouble("romney-percentage");
            }
        }
        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        Log.wtf("GOINGTOWATCH", congresspeople+obama+";"+romney);
        sendIntent.putExtra("congressmen", congresspeople+obama+";"+romney+";"+county+";");
        startService(sendIntent);
    }

    public void getTweetID(final String twitter_id, final TwitterAuthConfig authConfig, final int pos, final String link) throws IOException, JSONException {
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(com.twitter.sdk.android.core.Result<AppSession> result) {
                AppSession session = result.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                StatusesService s = twitterApiClient.getStatusesService();
                s.userTimeline(null, twitter_id, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(com.twitter.sdk.android.core.Result<List<Tweet>> result) {
                        tweets[pos] = result.data.get(0);
                        if(++tweetsRecieved == amt) {
                            congresslist.setAdapter(new CongressAdapter(ctx, people, parties, emails, websites, tweets, termend, id));
                            String x = congresspeople;
                            for(int i =0; i < tweets.length; i++) {
                                x += tweets[i].getId() + ",";
                                x += people[i]+ ",";
                                x += parties[i]+ ",";
                                x += termend[i]+ ",";
                                x += id[i]+ ",";
                                x += websites[i]+ ",";
                                x += emails[i]+ "~";
                            }
                            try {
                                launchWatch(link, x.substring(0, x.length()-1)+";");
                            } catch (IOException| JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.wtf("Place1", e);
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                Log.wtf("Place2", e);
            }
        });
    }
}
