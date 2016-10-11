package eu.randomobile.payolle.apppayolle.mod_feed.game;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.R;

/**
 * Created by 44 screens on 30/09/2016.
 */
public class SlideActivity extends Activity {

    int windowwidth;
    int windowheight;

    LinearLayout img_validate1;
    LinearLayout img_validate2;
    LinearLayout img_validate3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_slide_activity);

        windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        windowheight = getWindowManager().getDefaultDisplay().getHeight();
        ArrayList<ImageView> alimgView = new ArrayList<>();

        alimgView.add((ImageView) findViewById(R.id.imageview_lion));
        alimgView.add((ImageView) findViewById(R.id.imageview2_voiture));
        alimgView.add((ImageView) findViewById(R.id.imageview3_lion));
        img_validate1 = (LinearLayout) findViewById(R.id.layout_valid);
        img_validate2 = (LinearLayout) findViewById(R.id.layout_valid2);
        img_validate3 = (LinearLayout) findViewById(R.id.layout_valid3);
        img_validate1.setOnDragListener(new MyDragListener());
        img_validate2.setOnDragListener(new MyDragListener());
        img_validate3.setOnDragListener(new MyDragListener());

        for (final ImageView image : alimgView) {
            image.setOnTouchListener(new MyClickListener());
        }

    }
    private final class MyClickListener implements View.OnTouchListener  {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);
        Drawable targetShape = getResources().getDrawable(R.drawable.shape_drop);

        @Override
        public boolean onDrag(View v, DragEvent event) {

            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(targetShape);	//change the shape of the view
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);	//change the shape of the view back to normal
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    // if the view is the bottomlinear, we accept the drag item
                    checkVerifResponse(event, v,findViewById(R.id.imageview2_voiture),findViewById(R.id.layout_valid));
                    checkVerifResponse(event, v,findViewById(R.id.imageview_lion),findViewById(R.id.layout_valid2));
                    checkVerifResponse(event, v,findViewById(R.id.imageview3_lion),findViewById(R.id.layout_valid3));
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                   final View droppedView = (View) event.getLocalState();
                    droppedView.post(new Runnable(){
                        @Override
                        public void run() {
                            droppedView.setVisibility(View.VISIBLE);
                        }
                    });
                    break;//go back to normal shape

                default:
                    break;
            }
            return true;
        }
    }

    private void checkVerifResponse(DragEvent event,View v,View start,View end){
        if(v == end && event.getLocalState() == start) {
            View view = (View) event.getLocalState();
            ViewGroup viewgroup = (ViewGroup) view.getParent();
            viewgroup.removeView(view);
            LinearLayout containView = (LinearLayout) v;
            containView.addView(view);
            view.setVisibility(View.VISIBLE);
        } else {
            View view = (View) event.getLocalState();
            view.setVisibility(View.VISIBLE);
        }
    }
}
