package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourceFile;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.ContextWrapper;

public class POIDetailsActivity extends Activity {
    private MainApp app;
    private Poi poi;
    private Route route;
    public static final String PARAM_KEY_TITLE_POI = "poi_title";
    public static final String PARAM_KEY_NID_POI = "poi_nid";
    public static final String PARAM_KEY_TITLE_ROUTE = "route_title";

    private TextView txt_poi_title;
    private TextView txt_description_body;
    private ImageView main_image;

    ImageButton btn_home;
    ImageButton btn_return;
    private ImageButton btn_read;
    private ImageButton btn_game;
    FrameLayout btn_galery;
    //private ImageButton btn_info;
    //private ImageButton btn_badges;

    String paramTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (MainApp) getApplication();
        //Get Parameters from ROutesListActivity
        Bundle b = getIntent().getExtras();
        if (b != null) {
            // paramTitle = b.getString(PARAM_KEY_NID);
            paramTitle = b.getString(PARAM_KEY_TITLE_POI);
            int param2 = b.getInt(PARAM_KEY_NID_POI);
            final ArrayList<Poi> alPoi = app.getPoisList();

            for (Poi poi : alPoi) {
                String poiLog1 = Normalizer.normalize(poi.getTitle(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                String poiLog2 = Normalizer.normalize(paramTitle, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                Log.d("PierreDebug", "Poi source : " + poiLog2 + "      Poi cible : " + poiLog1);
                Log.d("PierreDebug", "Poi source : " + param2 + "      Poi cible : " + poi.getNid());
                if (poiLog1.equals(poiLog2)) {
                    this.poi = poi;
                    Log.d("VaninaLog", "Objet POI : title : " + poi.getTitle() + " images : " + poi.getImages() + " game: " + poi.getGame()+ "     ID " + poi.getNid());
                    //Log.d("JmLog", "Objet POI : " + poi.getTitle() + " " + poi.getImages() + " images item0 : " + poi.getImages().get(0).getFileUrl());
                    break;
                }
            }

            String paramRoute = b.getString(PARAM_KEY_TITLE_ROUTE);
            final ArrayList<Route> routes = app.getRoutesListDE();
            for (Route route : routes) {
                Log.d("PierreLog", route.getTitle() + "  ---  " + paramRoute);
                if (route.getTitle().equals(paramRoute)) {
                    this.route = route;
                    break;
                }
            }

        }
        setContentView(R.layout.feed_activity_poi_details);

        if (poi == null) POIDetailsActivity.this.finish(); //In case of bug, not crashing
        else {
            initComponents();
            setData();
            escucharEventos();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase, MainApp.locale);
        super.attachBaseContext(context);
    }



    public void initComponents(){
        // <----------------->_TEXTVIEWS_DECLARATIONS_<----------------->

        txt_poi_title = (TextView) findViewById(R.id.poi_title);
//        txt_ramp = (TextView) findViewById(R.id.txt_ramp);
//        txt_duration = (TextView) findViewById(R.id.txt_duration);
        txt_description_body = (TextView) findViewById(R.id.poi_description);
        btn_galery = (FrameLayout) findViewById(R.id.btn_galerie);

        // <----------------->_IMAGEVIEWS_DECLARATIONS_<----------------->
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);

        main_image = (ImageView) findViewById(R.id.main_image);

        btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
        btn_game = (ImageButton) findViewById(R.id.btn_footer_game);
        //btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        //btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);
    }

    private void escucharEventos() {
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(POIDetailsActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });


        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        POIDetailsActivity.this.finish();
                    }
                });


        btn_galery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(POIDetailsActivity.this, GaleryActivity.class);
                        intent.putParcelableArrayListExtra(GaleryActivity.PARAM_KEY_ARRAY_RESOURCES, poi.getImages());
                        startActivity(intent);
                    }
                });

        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(POIDetailsActivity.this, LireActivity.class);
                startActivity(intent);
            }
        });
        //if (poi.getGame().equals("1")) {
            btn_game.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(POIDetailsActivity.this, GameActivity.class);
                            intent.putExtra(GameActivity.PARAM_KEY_POI_TITLE, poi.getTitle());
                            intent.putExtra(GameActivity.PARAM_KEY_TITLE_ROUTE, route.getTitle());
                            startActivity(intent);
                        }
                    });
        //}
        /*btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(POIDetailsActivity.this, InfoDecouverteActivity.class);
                startActivity(intent);
            }
        });*/
        /*btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(POIDetailsActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });*/


    }
    private void setData() {

        txt_poi_title.setText(poi.getTitle());
        //txt_ramp.setText(Util.formatDesnivel(route.getSlope()));
        // Log.d("Desnivel:", String.valueOf(route.getSlope()));
        // txt_duration.setText(Util.formatDuracion(route.getEstimatedTime()));
        txt_description_body.setText(Html.fromHtml(poi.getBody().replace("\t","\t\t")).toString().trim(), TextView.BufferType.SPANNABLE);

        // Image
        if (poi.getMainImage() != null) {
            BitmapManager.INSTANCE.loadBitmap(poi.getMainImage(),
                    main_image, 90, 90);
        }
        /* if (poi.getImages().get(0).getFileUrl() != null) {
            Log.d("First image", poi.getImages().get(0).getFileUrl());
            BitmapManager.INSTANCE.loadBitmap(poi.getImages().get(0).getFileUrl(),
                    main_image, 90, 90);
        } else {
            main_image.setImageResource(R.mipmap.ic_launcher);
        } */

        // GAME
        //if (poi.getGame().equals("1")) {
            btn_game.setImageResource(R.drawable.icono_juer_deactif);
        /*} else {
            btn_game.setImageResource(R.drawable.icono_juer_inactif);
        }*/

    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}