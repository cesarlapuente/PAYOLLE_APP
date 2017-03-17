package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Pattern;

import eu.randomobile.payolle.apppayolle.R;

public class ResultsActivity extends Activity {

    private ImageButton backButton;
    private ImageButton homeButton;

    public static String CHRONO_TIME = "chrono";
    public static String BALISE_VALIDATE = "balise";
    public static String BALISE_VALIDATE_MAX = "balise_max";
    public static String DISTANCE_TRAVELLED = "distance";
    public static String PARAM_KEY_NID = "nid";

    public TextView txt_hours,
            txt_minutes,
            txt_secondes,
            txt_distance,
            txt_nb_balise,
            txt_nb_balise_max;
    public String distance;
    public int nb_balise_max,nb_balise;
    public String paramNid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_results);
        capturarControles();
        escucharEventos();
        setData();
        showBalise();
    }

    private void capturarControles() {
        backButton = (ImageButton) findViewById(R.id.btn_return);
        homeButton = (ImageButton) findViewById(R.id.btn_home);

        txt_hours = (TextView) findViewById(R.id.chrono_hours);
        txt_minutes = (TextView) findViewById(R.id.chrono_minutes);
        txt_secondes = (TextView) findViewById(R.id.chrono_seconds);

        txt_distance = (TextView) findViewById(R.id.distance_value);

        txt_nb_balise = (TextView) findViewById(R.id.nb_balise_validate);
        txt_nb_balise_max = (TextView) findViewById(R.id.nb_balise_validate_max);
    }

    private void escucharEventos() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultsActivity.this.finish();
                /*Possible cheat here, if the runner stop the course and go back, he can do the previous activity without running chrono, but there is no ranking, so this is not a problem*/
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setData(){
        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);
            String time = b.getString(CHRONO_TIME);
            String[] output = time.split(Pattern.quote(":"));

            txt_hours.setText( output[0]);
            txt_minutes.setText( output[1]);
            txt_secondes.setText( output[2]);

            txt_distance.setText(b.getString(DISTANCE_TRAVELLED));
            txt_nb_balise.setText(b.getString(BALISE_VALIDATE));
            txt_nb_balise_max.setText(b.getString(BALISE_VALIDATE_MAX));

            nb_balise =  Integer.parseInt(b.getString(BALISE_VALIDATE));
            nb_balise_max = Integer.parseInt(b.getString(BALISE_VALIDATE_MAX));
        }
    }

    private void showBalise(){

        LinearLayout list =(LinearLayout) findViewById(R.id.list_balise);

        if(nb_balise >= 1) {
            for (int j = 0; j < nb_balise; j++) {
                list.addView(createBalise(true));
            }
        }
        int freq = nb_balise_max - nb_balise;
        if(freq >= 1) {
            for (int i = 0; i < freq; i++) {
                list.addView(createBalise(false));
            }
        }
    }

    public ImageView createBalise(boolean isValide){
        ImageView imgbalise = new ImageView(this);

        android.view.ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(5,5);
        layoutParams.width = layoutParams.WRAP_CONTENT;
        layoutParams.height =  layoutParams.WRAP_CONTENT;
        imgbalise.setLayoutParams(layoutParams);
        imgbalise.setAdjustViewBounds(true);
        if(isValide) {
            imgbalise.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.parcours_validate));
        }else{
            imgbalise.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.parcours_0));
        }
        return imgbalise;
    }

    @Override
    public void onBackPressed() { /*Unused*/
        Intent intent = new Intent(ResultsActivity.this,FeedRouteDetails.class);
        intent.putExtra(FeedRouteDetails.PARAM_KEY_NID,paramNid);
        startActivity(intent);
    }
}
