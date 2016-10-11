package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_feed.game.SlideActivity;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class GameActivity extends Activity {

    private MainApp app;
    private ListView mListView;
    private ArrayList<Route> arrayRoutes = null;
    private ImageButton btn_home;
    private ImageButton btn_return;
    private ImageButton btn_read;
    private ImageButton btn_info;
    private ImageButton btn_badges;
    private FrameLayout btn_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_game);


        capturarControles();
        escucharEventos();

    }

    private void capturarControles() {

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);
        btn_play = (FrameLayout) findViewById(R.id.btn_play);
    }

    private void escucharEventos() {
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, InfoOrientationActivity.class);
                startActivity(intent);
            }
        });
        btn_read.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(GameActivity.this, FeedRouteActivity.class);
//                        startActivity(intent);
                    }
                });
        btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });
        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        btn_play.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       Intent intent = new Intent(GameActivity.this, SlideActivity.class);
//                        ArrayList<Poi> pois = app.getPoisList();
//
//                        String poisJSON = JSONPOIParser.parseToJSONArray(app, pois).toString();
//                        Intent intent = new Intent(GameActivity.this, RAActivity.class);
//                        intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, getString(R.string.mod_discover__augmented_reality));
//                        intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
//                        intent.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, poisJSON);
                        startActivity(intent);
                    }
                });
    }


}


