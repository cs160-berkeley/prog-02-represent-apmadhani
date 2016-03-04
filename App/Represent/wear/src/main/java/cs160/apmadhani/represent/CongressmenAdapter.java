package cs160.apmadhani.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by Akshay on 3/2/16.
 */
public class CongressmenAdapter extends FragmentGridPagerAdapter {

    String[] congressmen;
    String[] party;
    int romney;
    int obama;
    String location;
    Context ctx;

    public CongressmenAdapter(Context ctx, FragmentManager fm, String[] congressmen, String[] party, String location, int romney, int obama) {
        super(fm);
        this.ctx = ctx;
        this.location = location;
        this.romney = romney;
        this.obama = obama;
        this.congressmen = congressmen;
        this.party = party;
    }


    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Fragment congressman;
        if (col == 3) {
            congressman = new CongressmanFragment(ctx, location, romney, obama);
        } else {
            congressman = new CongressmanFragment(ctx, congressmen[col], party[col]);
        }
        return congressman;
    }


    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 4;
    }
}