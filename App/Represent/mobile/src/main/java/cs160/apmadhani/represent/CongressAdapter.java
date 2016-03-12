package cs160.apmadhani.represent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import org.w3c.dom.Text;


/**
 * Created by Akshay on 3/7/16.
 */
public class CongressAdapter extends BaseAdapter {

    private String[] names, parties, emails, websites, termend;
    private Tweet[] tweets;
    private String[] id;
    Context ctx;


    public CongressAdapter(Context ctx, String[] names, String[] parties, String[] emails, String[] websites, Tweet[] tweets, String[] termend, String[] id) {
        this.ctx = ctx;
        this.names = names;
        this.parties = parties;
        this.emails = emails;
        this.websites = websites;
        this.tweets = tweets;
        this.termend = termend;
        this.id = id;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int p = position;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View congressCard = inflater.inflate(R.layout.card_congressperson, parent, false);
        ((TextView) congressCard.findViewById(R.id.member_name)).setText(names[position]);
        ((ImageView) congressCard.findViewById(R.id.mailIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("YAY", emails[p]);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emails[p]});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.putExtra(Intent.EXTRA_CC, "");
                intent.setType("message/rfc822");
                ctx.startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });
        ((ImageView) congressCard.findViewById(R.id.webIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("YAY", "I have finished 2");
                Uri uri = Uri.parse(websites[p]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ctx.startActivity(intent);
            }
        });
        ((TextView) congressCard.findViewById(R.id.learnmore)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailedView.class);
                intent.putExtra("Tweet", tweets[p].getId());
                intent.putExtra("Name", names[p]);
                intent.putExtra("Party", parties[p]);
                intent.putExtra("Termend", termend[p]);
                intent.putExtra("ID", id[p]);
                intent.putExtra("Website", websites[p]);
                intent.putExtra("Email", emails[p]);
                ctx.startActivity(intent);
            }
        });
        Log.wtf("ID", "" + tweets[position].id);
        CompactTweetView t = new CompactTweetView(ctx, tweets[position]);
        ((RelativeLayout) congressCard.findViewById(R.id.twitter)).addView(t);
//        Log.wtf("gotID", ""+tweetId);
//        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//            }
//
//            @Override
//            public void failure(TwitterException e) {
//
//            }
//        });

        RelativeLayout c = (RelativeLayout) congressCard.findViewById(R.id.candidate_card);
        if (parties[position].equals("D")) {
            c.setBackgroundColor(0xFF4099FF);
        } else if (parties[position].equals("R")) {
            c.setBackgroundColor(0xFFFF6759);
        } else {
            c.setBackgroundColor(0xFF424242);
        }
        Log.wtf(parties[position], names[position]);
        return congressCard;
    }
}
