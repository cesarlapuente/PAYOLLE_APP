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

        /* Version statique du jeu */
        String num = poi.getTitle().substring(0,3);
        if (num.equals("1-1")) poiGame = "1Pour découvrir le \"secret des frontières\" lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours.\n" +
                "Indice 1 : Comment s'écrivait le nom de l'hôtelier qui à donner son nom à Payolle ?";
        else if (num.equals("1-2")) poiGame = "2Indice 2 : Exploitation forestière, tourisme, quelle est la troisième activité principale à Payolle ?";
        else if (num.equals("1-3")) poiGame = "2Indice 3 : Vainqueur de l'étape de la première arrivée à Payolle en 2016.";
        else if (num.equals("1-4")) poiGame = "2Indice 4 : Une crête en patois.";
        else if (num.equals("1-5")) poiGame = "2Indice 5 : Dans la légende, comment s'appelle le combattant de la vallée de Campan ?";
        else if (num.equals("1-6")) poiGame = "2Indice 6 : Je domine Payolle.";
        else if (num.equals("1-7")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;Paillole – le Dogue – Stephen Cummings - Pastoralisme – Sarrat – L'Arbizon;Paillole – le Dogue – Stephen Cummings - Pastoralisme – Sarrat – L'Arbizon;Thibaut Pinot – Pastoralisme - Sarrat – Pailhole – le Dogue – Le Pic du Midi;Sarrat – Pastoralisme - L'Arbizon – Paillole – Stephen Cummings – Fréchou";
        /**/
        else if (num.equals("2-1")) poiGame = "1Pour découvrir le \"secret des rigoles\"  lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours.\n" +
                "Indice 1 : L'Adour de Payolle, l'Adour de Gripp, comment se nomme la troisième \"source\" du fleuve Adour ?";
        else if (num.equals("2-2")) poiGame = "2Indice 2 : Quelle est la hauteur maximale de la digue du lac ?";
        else if (num.equals("2-3")) poiGame = "2Indice 3 : Comment nomme-t-on la fabrication d'électricité grâce à la force de l'eau.";
        else if (num.equals("2-4")) poiGame = "2Indice 4 : Je nage dans les rivières et le lac de Payolle.";
        else if (num.equals("2-5")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;16 m – Truite fario - Hydroélectricité - Lesponne;Desman - Hydroélectricité - 15 m - Lesponne;Turbine – Lesponne -12 m – Truite fario;16 m – Truite fario - Hydroélectricité - Lesponne";
        /**/
        else if (num.equals("3-1")) poiGame = "1Pour découvrir le \"secret de de la soupe\"  lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours.\n" +
                "Indice 1 : Petit animal qui vivait dans les mers il y a environ 360 millions d'années.";
        else if (num.equals("3-2")) poiGame = "2Indice 2 : Les romains ont envoyé ce marbre à Rome.";
        else if (num.equals("3-3")) poiGame = "2Indice 3 : Je protège la maison sur le chapeau des cheminées.";
        else if (num.equals("3-4")) poiGame = "2Indice 4 : Je marche à la queue leu leu.";
        else if (num.equals("3-5")) poiGame = "2Indice 5 : Maurice Bénézech alias...";
        else if (num.equals("3-6")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;Loup – Cocut – Goniatite – Vert tendre – Capitaine Bernard;Vert tendre – Goniatite – Pénaous – Capitaine Bernard – Loup;Loup – Cocut – Goniatite – Vert tendre – Capitaine Bernard;Capitaine Bernard - Griotte – Goniatite - Loup - Cocut";
        /**/
        else if (num.equals("4-1")) poiGame = "1Pour découvrir le \"secret de de la cabane\" lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours.\n" +
                "Indice 1 : Le bétail se déplace de la plaine vers la montagne.";
        else if (num.equals("4-2")) poiGame = "2Indice 2 : Ancien outil pour fabriquer le beurre.";
        else if (num.equals("4-3")) poiGame = "2Indice 3 : Village de vacher en montagne.";
        else if (num.equals("4-4")) poiGame = "2Indice 4 : Les chefs de maison composaient la...";
        else if (num.equals("4-5")) poiGame = "2Indice 5 : Le courtaou des Esclozes a été construit sur une...";
        else if (num.equals("4-6")) poiGame = "2Indice 6 : Vous êtes passé par ce courtaou avant d'arriver aux Esclozes.";
        else if (num.equals("4-7")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;Moraine glacière -Bésiau - Artigussy – Baratte – Transhumance - Courtaou;Courtaou – Le Sarroua – Baratte – Moraine glaciaire – Transhumance - Bésiau;Moraine glacière -Bésiau - Artigussy – Baratte – Transhumance - Courtaou;Transhumance – Badino – Courtaou - Bésiau - Artigussy – Moraine glaciaire";
        /**/
        else if (num.equals("5-1")) poiGame = "1Pour découvrir le \"secret des paysages\"  lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours..\n" +
                "Indice 1 : Je ne m'enfonce pas dans la tourbière grâce à son plumet.";
        else if (num.equals("5-2")) poiGame = "2Indice 2 : Mes cônes poussent vers le bas.";
        else if (num.equals("5-3")) poiGame = "2Indice 3 : Fleur de la pelouse avec un O dans l'E.";
        else if (num.equals("5-4")) poiGame = "2Indice 4 : Je nettoie par le feu.";
        else if (num.equals("5-5")) poiGame = "2Indice 5 : Sangliers et palombes adorent le fruit du hêtre.";
        else if (num.equals("5-6")) poiGame = "2Indice 6 : Roche qui s'est formée au fond des mers.";
        else if (num.equals("5-7")) poiGame = "2Indice 7 : Nappe cachée dans la montagne.";
        else if (num.equals("5-8")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;Calcaire - Epicéa – Faine – Linaigrette - Phréatique - Ecobuage - Œillet;Gentiane - Phréatique - Faine – Linaigrette – Calcaire – Ecobuage - Épicéa;Ecobuage - Vinaigrette - Epicéa - Granite - Phréatique - Œillet - Faine;Calcaire - Epicéa – Faine – Linaigrette - Phréatique - Ecobuage - Œillet";
        /**/
        else if (num.equals("6-1")) poiGame = "1Pour découvrir le \"secret du gypaète\"  lors de la dernière étape, vous allez devoir trouver un indice dans chaque étape du parcours.\n" +
                "Indice 1 : A Payolle, je suis le roi de l'apnée.\n";
        else if (num.equals("6-2")) poiGame = "2Indice 2 : Fleur la plus toxique de France.";
        else if (num.equals("6-3")) poiGame = "2Indice 3 : Aquila chrysaetos en français.";
        else if (num.equals("6-4")) poiGame = "2Indice 4 : Animal à raquettes.";
        else if (num.equals("6-5")) poiGame = "2Indice 5 : Indice de présence odorant.";
        else if (num.equals("6-6")) poiGame = "2Indice 6 : Je ne mange pas de viande.";
        else if (num.equals("6-7")) poiGame = "2Indice 7 : Maternité des fourmis.";
        else if (num.equals("6-8")) poiGame = "3Rappelez-vous des indices et choisissez la bonne liste !;Aconit napel – Couvin - Végétarien - Aigle royal – Desman - Lagopède alpin;Couvin – Desman - Végétarien - Aconit tue-loup – Crotte - Lagopède alpin – Aigle royal;Vautour fauve - Végétarien - Lagopède alpin – Couvin – Desman – Aconit napel - Poil;Aconit napel – Couvin - Végétarien - Aigle royal – Desman - Lagopède alpin";

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


