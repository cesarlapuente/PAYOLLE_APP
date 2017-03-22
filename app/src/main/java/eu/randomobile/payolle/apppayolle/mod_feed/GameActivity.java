package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_feed.game.SlideActivity;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class GameActivity extends Activity {
    public static final String PARAM_KEY_POI_TITLE = "poi_title";

    private MainApp app;
    private Poi poi;
    private ImageButton btn_home;
    private ImageButton btn_return;
    private ImageButton btn_read;
    private ImageButton btn_info;
    private TextView txt_game;
    private int type; //1:start(rules, infos), 2:content(hints, answers), 3:end(question, multiple choices)
    private ListView list_answers;
    private String good_answer;
    private AnswersAdapter answersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_game);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String paramTitle = b.getString(PARAM_KEY_POI_TITLE);
            final ArrayList<Poi> alPoi = app.getPoisList();

            for (Poi poi : alPoi) {
                if (poi.getTitle().equals(paramTitle)) {
                    this.poi = poi;
                    break;
                }
            }
        }
        if (poi == null) GameActivity.this.finish(); //In case of bug, not crashing
        else {
            capturarControles();
            escucharEventos();
            play();
        }
    }

    private void capturarControles() {

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        txt_game = (TextView) findViewById(R.id.poi_game);
        list_answers = (ListView) findViewById(R.id.scrollView_game);
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

    }

    private void play() {
        String poiGame = "3Question ?;Reponse;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix x"; //Get the string here

        /* Version statiaue du jeu */
        String num = poi.getTitle().substring(0,3);
        if (num.equals("1-1")) poiGame = "1Restez éveillés à chaque étape du parcours pour découvrir les indices qui vont vous donner la réponse à l’énigme dans la dernière étape.";
        else if (num.equals("1-7")) poiGame = "3Indiquez les cinq communes de Payolle d’entre ces 3 choix;Campan – Beyrède-Jumet – Ancizan – Aspin-Aure – Arreau;Beyrede – Arreau – La Séoube – Campan – Asté;Campan – Beyrède-Jumet – Ancizan – Aspin-Aure – Arreau;Arreau – Campan – Beyrède – Aspin - Bagnères";
        else if (num.equals("1-2")) poiGame = "2Campan";
        else if (num.equals("1-3")) poiGame = "2Beyrède-Jumet";
        else if (num.equals("1-4")) poiGame = "2Ancizan";
        else if (num.equals("1-5")) poiGame = "2Aspin-Aure";
        else if (num.equals("1-6")) poiGame = "2Arreau";
        /**/

        //poiGame = poi.getGame();
        if (poiGame != null){
            type = Integer.parseInt(poiGame.substring(0,1));
            if (type == 3) {
                list_answers.setVisibility(View.VISIBLE);
                ArrayList<String> txt_items = new ArrayList<String>(Arrays.asList(poiGame.substring(1).split(";")));
                txt_game.setText(txt_items.remove(0));
                good_answer = txt_items.remove(0);

                answersAdapter = new AnswersAdapter(this,txt_items);
                list_answers.setAdapter(answersAdapter);

            } else {
                txt_game.setText(poiGame.substring(1));
            }
        }
    }

    private void check_answer(String ans) {
        if (ans.equals(good_answer)){
            txt_game.setText("Félicitations, c'est la bonne réponse !");
        } else {
            txt_game.setText("Dommage, ce n'est pas la bonne réponse.");
        }
        txt_game.setTextSize(20);
        txt_game.setTypeface(null, Typeface.BOLD);
        list_answers.setVisibility(View.GONE);
    }

    public class AnswersAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<String> listaItems;

        public class ViewHolder {
            Button btn;

            int index;
        }

        public AnswersAdapter(Context _ctx, ArrayList<String> _items) {
            this.listaItems = _items;
            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (listaItems != null) {
                return listaItems.size();

            } else {
                Log.d("Answers List", "liste vide");
                return 0;
            }
        }
        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            GameActivity.AnswersAdapter.ViewHolder holder;
            Button btn = new Button(GameActivity.this);

            // Recoger el item
            final String item = listaItems.get(position);

            btn.setText(item);
            //TODO add listenner with response comparaison
            btn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            check_answer(item);
                        }
                    });


            return btn;
        }


    }

}


