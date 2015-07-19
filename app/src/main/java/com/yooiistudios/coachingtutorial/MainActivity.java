package com.yooiistudios.coachingtutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.yooiistudios.coachingtutorial.coaching.Coach;
import com.yooiistudios.coachingtutorial.coaching.HoleType;
import com.yooiistudios.coachingtutorial.coaching.TargetSpec;
import com.yooiistudios.coachingtutorial.coaching.TargetSpecs;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout mRootView;
//    private SpeechBubble mBubble;
    private View mRightTopView;
    private View mBottomCenterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = (RelativeLayout) findViewById(R.id.root);
//        mBubble = (SpeechBubble) findViewById(R.id.bubble);
        mRightTopView = findViewById(R.id.right_top);
        mBottomCenterView = findViewById(R.id.bottom_center);

//        CoachCover cover = new CoachCover(this);
////        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.INSCRIBE);
////        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.HALF_INSCRIBE);
//        cover.makeHoleAt(new RectF(300, 100, 500, 200), CoachCover.HoleType.CIRCUMSCRIBE);
//
//        mRootView.addView(cover, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//        mBubble.bringToFront();

        TargetSpecs specs = new TargetSpecs();
        TargetSpec targetSpec;

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.INSCRIBE)
                .setDirection(TargetSpec.Direction.TOP_LEFT)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.HALF_INSCRIBE)
                .setDirection(TargetSpec.Direction.TOP_LEFT)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.CIRCUMSCRIBE)
                .setDirection(TargetSpec.Direction.TOP_LEFT)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.INSCRIBE)
                .setDirection(TargetSpec.Direction.BOTTOM)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.HALF_INSCRIBE)
                .setDirection(TargetSpec.Direction.BOTTOM)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);

        targetSpec = new TargetSpec.Builder(mRightTopView)
                .setHoleType(HoleType.CIRCUMSCRIBE)
                .setDirection(TargetSpec.Direction.BOTTOM)
                .setMessage("The quick gray fox jumps over the lazy dog.")
                .build();
        specs.add(targetSpec);
        Coach.start(this, specs);

//        mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                test();
//                return true;
//            }
//        });


//        final SpeechBubble bubble = new SpeechBubble(this);
//        bubble.setMessage("The gray fox is blahblah... The gray fox is blahblah... The gray fox is blahblah...");
//        CoachCover.LayoutParams lp = new CoachCover.LayoutParams(
//                CoachCover.LayoutParams.WRAP_CONTENT,
//                CoachCover.LayoutParams.WRAP_CONTENT
//        );
//        mRootView.addView(bubble, lp);
    }

//    private void test() {
//        View parent = findViewById(R.id.parent);
//        View child = findViewById(R.id.child);
//
//        Rect rect = new Rect();
//
//        parent.getGlobalVisibleRect(rect);
//        Log.i("qwerasdf", "parent - getGlobalVisibleRect: " + rect.flattenToString());
//        parent.getLocalVisibleRect(rect);
//        Log.i("qwerasdf", "parent - getLocalVisibleRect: " + rect.flattenToString());
//
//        child.getGlobalVisibleRect(rect);
//        Log.i("qwerasdf", "child  - getGlobalVisibleRect: " + rect.flattenToString());
//        child.getLocalVisibleRect(rect);
//        Log.i("qwerasdf", "child  - getLocalVisibleRect: " + rect.flattenToString());
//    }

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
