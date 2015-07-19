package com.yooiistudios.coachingtutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.yooiistudios.coachingtutorial.coaching.Coach;
import com.yooiistudios.coachingtutorial.coaching.SpeechBubble;
import com.yooiistudios.coachingtutorial.coaching.TargetSpec;
import com.yooiistudios.coachingtutorial.coaching.TargetSpecs;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mRootView;
    private SpeechBubble mBubble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = (RelativeLayout) findViewById(R.id.root);
        mBubble = (SpeechBubble) findViewById(R.id.bubble);

//        CoachCover cover = new CoachCover(this);
////        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.INSCRIBE);
////        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.HALF_INSCRIBE);
//        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.CIRCUMSCRIBE);
//
//        mRootView.addView(cover, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//        mBubble.bringToFront();

        TargetSpecs specs = new TargetSpecs();
        specs.add(new TargetSpec.Builder(mRootView).build());
        specs.add(new TargetSpec.Builder(mRootView).build());
        Coach.start(this, specs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
