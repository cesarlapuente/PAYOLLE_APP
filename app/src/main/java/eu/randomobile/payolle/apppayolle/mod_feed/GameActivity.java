package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.ContextWrapper;

public class GameActivity extends Activity {
    public static final String PARAM_KEY_POI_TITLE = "poi_title";
    public static final String PARAM_KEY_TITLE_ROUTE = "route_title";

    private MainApp app;
    private Poi poi;
    private Route route;
    private ImageButton btn_home;
    private ImageButton btn_return;
//    private ImageButton btn_read;
//    private ImageButton btn_info;
    private TextView txt_game;
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
                String poiLog1 = Normalizer.normalize(poi.getTitle(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                String poiLog2 = Normalizer.normalize(paramTitle, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                if (poiLog1.equals(poiLog2)) {
                    this.poi = poi;
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
        if (poi == null || route == null) GameActivity.this.finish(); //In case of bug, not crashing
        else {
            capturarControles();
            escucharEventos();
            play();
        }
    }

    private void capturarControles() {

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
//        btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
//        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        txt_game = (TextView) findViewById(R.id.poi_game);
        list_answers = (ListView) findViewById(R.id.scrollView_game);
    }

    private void escucharEventos() {
        /*btn_info.setOnClickListener(new View.OnClickListener() {
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
                });*/

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
        String poiGame = "Question ?;Reponse;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix 1;Choix 2; ... ;Choix x"; //Get the string here

        /* Version statique du jeu */
        String num = poi.getTitle().substring(0,3);
        /*if (num.equals("1-1")) poiGame = "Pour découvrir le \"secret des frontières\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "Comment s'écrivait le nom de l'hôtelier qui a donné son nom à Payolle ?;Paillole;Pailhole;Paillole;Payole";*/
        poiGame = getString(getResources().getIdentifier("game" + num.replace("-","_"), "string", getPackageName()));

        //poiGame = poi.getGame();
        if (poiGame != null){
            ArrayList<String> txt_items = new ArrayList<String>(Arrays.asList(poiGame.split(";")));

            if(txt_items.get(0).equals("LASTPOI")){
                int numOk = route.getNumberPoiGameOkTrue();
                int numTot = route.getNumberPoiGameOk().size();
                txt_items.set(1,txt_items.get(1).concat(numOk+" questions sur "+numTot));
                if (numOk==numTot) {
                    txt_items.set(1,txt_items.get(1).concat("\n"+txt_items.get(2))); //Game full ok
                } else {
                    txt_items.set(1,txt_items.get(1).concat("\n"+txt_items.get(3))); //Game with mistakes
                }
                txt_game.setText(txt_items.get(1));
            } else {
                list_answers.setVisibility(View.VISIBLE);
                txt_game.setText(txt_items.remove(0));
                good_answer = txt_items.remove(0);

                answersAdapter = new AnswersAdapter(this, txt_items);
                list_answers.setAdapter(answersAdapter);
            }
        }
    }

    private void check_answer(String ans) {
        if (ans.equals(good_answer)){
            txt_game.setText(getText(getResources().getIdentifier("good_answer", "string", getPackageName())));
            route.setGameTrue(Integer.parseInt(poi.getTitle().substring(2,3))-1);
        } else {
            txt_game.setText(getText(getResources().getIdentifier("bad_answer", "string", getPackageName())));
        }
        txt_game.setTextSize(16);
        list_answers.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase, MainApp.locale);
        super.attachBaseContext(context);
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
            btn.setBackgroundResource(R.drawable.fondo_indice3x);
            btn.setTextColor(Color.WHITE);

            // Recoger el item
            final String item = listaItems.get(position);

            btn.setText(item);
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


