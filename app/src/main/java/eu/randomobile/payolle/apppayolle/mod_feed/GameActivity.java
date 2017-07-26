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
                if (route.getTitle().equals(paramRoute)) {
                    this.route = route;
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
        if (num.equals("1-1")) poiGame = "Pour découvrir le \"secret des frontières\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "Comment s'écrivait le nom de l'hôtelier qui a donné son nom à Payolle ?;Paillole;Pailhole;Paillole;Payole";
        else if (num.equals("1-2")) poiGame = "Exploitation forestière, tourisme, quelle est la troisième activité principale à Payolle ?;Le Pastoralisme;Le Pastoralisme;le commerce;L’industrie";
        else if (num.equals("1-3")) poiGame = "Qui a gagné l'étape de la première arrivée à Payolle en 2016 ?;Stephen Cummings;Thibaut Pinot;Stephen Cummings;Bernard Hinault";
        else if (num.equals("1-4")) poiGame = "Comment dit-on une crête en patois ?;Sarrat;Corde;Courtaou;Sarrat";
        else if (num.equals("1-5")) poiGame = "Dans la légende, comment s'appelle le combattant de la vallée de Campan ?;Le Dogue;Le Dogue;Goliath;Fréchou";
        else if (num.equals("1-6")) poiGame = "Je domine Payolle.;L'Arbizon;Le Pic du Midi;Col d’Aspin;L'Arbizon";
        else if (num.equals("1-7")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret des frontières\" !\n\n"
                +"De tous temps les communautés se sont affrontées pour la propriété des meilleures estives et des plus belles forêts. Les différents pouvoirs se sont accaparés des territoires puis les ont rendus aux communautés. Cette histoire explique la présence des propriétés indivises et le partage parfois bizarre des territoires comme celui de Payolle réparti sur cinq communes."
                +";Pour découvrir le \"secret des frontières\" vous devez obtenir toutes les bonnes réponses.";
        /**/
        else if (num.equals("2-1")) poiGame = "Pour découvrir le \"secret des rigoles\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "L'Adour de Payolle, l'Adour de Gripp, comment se nomme la troisième \"source\" du fleuve Adour ?;Lesponne;Lesponne;Bigorre;Campan";
        else if (num.equals("2-2")) poiGame = "Quelle est la hauteur maximale de la digue du lac ?;16 m;12 m;15 m;16 m";
        else if (num.equals("2-3")) poiGame = "Comment nomme-t-on la fabrication d'électricité grâce à la force de l'eau ?;Hydroélectricité;Solaire;Hydroélectricité;Turbine";
        else if (num.equals("2-4")) poiGame = "Je nage dans les rivières et le lac de Payolle.;Truite fario;Truite fario;L’ours;Desman";
        else if (num.equals("2-5")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret des rigoles\" !\n\n"
                +"Des dizaines de kilomètres de rigoles ont été creusées par les hommes pour distribuer l'eau partout dans la vallée. L'eau pour arroser, transporter, boire, laver... Ces rigoles alimentaient en permanence chaque lieu par un trou de quatre centimètres de diamètre maximum. Pour arroser les terrains, les bégades fixaient le tour de chacun pour prendre l'eau dans les rigoles pendant une durée déterminée du jour ou de la nuit."
                +";Pour découvrir le \"secret des rigoles\" vous devez obtenir toutes les bonnes réponses.";
        /**/
        else if (num.equals("3-1")) poiGame = "Pour découvrir le \"secret de la soupe\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "Je suis un petit animal qui vivait dans les mers il y a environ 360 millions d'années.;Goniatite;Fossiles;Goniatite;Goniate";
        else if (num.equals("3-2")) poiGame = "Les Romains ont envoyé ce marbre à Rome.;Vert tendre;Rouge;Griotte;Vert tendre";
        else if (num.equals("3-3")) poiGame = "Je protège la maison sur le chapeau des cheminées.;Cocut;Cocut;Pénaous;Chaume";
        else if (num.equals("3-4")) poiGame = "Je marche à la queue leu leu.;Loup;Loup;Fourvel;Renard";
        else if (num.equals("3-5")) poiGame = "Maurice Bénézech alias...;Capitaine Bernard;Capitaine Bernard;Fourvel;Soubielle";
        else if (num.equals("3-6")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret de la soupe\" !\n\n"
                +"Le courtaou du Sarroua pourrait s'appeler les cabanes aux épinards.\n" +
                "Le sarrou (chénopode bon-Henri) était utilisé par les bergers pour cuisiner la soupe. Cette plante a certainement été introduite en montagne dans la laine des brebis, elle affectionne les reposoirs à bétail riches en nitrates et les brebis en sont friandes à l'automne."
                +";Pour découvrir le \"secret de la soupe\" vous devez obtenir toutes les bonnes réponses.";
        /**/
        else if (num.equals("4-1")) poiGame = "Pour découvrir le \"secret de la cabane\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "Comment appelle-t-on le déplacement du bétail de la plaine vers la montagne.;Transhumance;Migration;Voyage du bétail;Transhumance";
        else if (num.equals("4-2")) poiGame = "J’étais utilisé pour fabriquer le beurre.;Baratte;Baratte;Courtaou;Badino";
        else if (num.equals("4-3")) poiGame = "Je suis le nom donné à un village de vachers en montagne.;Courtaou;Village de bergers;Artigussy;Courtaou";
        else if (num.equals("4-4")) poiGame = "Mon nom était celui donné à l’assemblée composée par les chefs de maison.;Bésiau;Bésiau;Courtaou;Badino";
        else if (num.equals("4-5")) poiGame = "Le courtaou des Esclozes a été construit sur une...;Moraine glacière;Montagne;Moraine glacière;Glacier";
        else if (num.equals("4-6")) poiGame = "Vous êtes passés par ce courtaou avant d'arriver aux Esclozes.;Artigussy;Le Sarroua;Artigussy;Bésiau";
        else if (num.equals("4-7")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret de la cabane\" !\n\n"
                +"Pour se protéger des intempéries, des loups et des ours, les bergers possédaient une cabane. Comme ils suivaient toujours leurs troupeaux, ils faisaient suivre aussi une cabane mobile : le burguet. Il s'agissait d'une grande caisse de deux mètres de long, avec une petite porte, munie de deux brancards pour la déplacer."
                +";Pour découvrir le \"secret de la cabane\" vous devez obtenir toutes les bonnes réponses.";
        /**/
        else if (num.equals("5-1")) poiGame = "Pour découvrir le \"secret des paysages\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "Je ne m'enfonce pas dans la tourbière grâce à son plumet.;Linaigrette;Gentiane;Vinaigrette;Linaigrette";
        else if (num.equals("5-2")) poiGame = "Mes cônes poussent vers le bas.;Épicéa;Épicéa;Sapins;Œillet";
        else if (num.equals("5-3")) poiGame = "Fleur de la pelouse avec un O dans l'E.;Œillet;Gœntiane;Œuf;Œillet";
        else if (num.equals("5-4")) poiGame = "Je nettoie par le feu.;Écobuage;Écobuage;Incendie;Faine";
        else if (num.equals("5-5")) poiGame = "Sangliers et palombes adorent ce fruit du hêtre.;Faine;Épicéa;Faine;Murs";
        else if (num.equals("5-6")) poiGame = "Roche qui s'est formée au fond des mers.;Calcaire;Marbre;Calcaire;Granite";
        else if (num.equals("5-7")) poiGame = "Nappe cachée dans la montagne.;Phréatique;Phréatique;Calcaire;Textile";
        else if (num.equals("5-8")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret des paysages\" !\n\n"
                +"Les jolis paysages de montagne sont dus à l'exploitation séculaire des estives et des forêts. Sans cette exploitation, ni belles pelouses ni belles forêts. La chasse permet également de gérer ces paysages. Aujourd'hui l'objectif de cet équilibre est de pérenniser une faune sauvage riche et diversifiée, compatible avec la rentabilité des activités agricoles et sylvicoles."
                +";Pour découvrir le \"secret des paysages\" vous devez obtenir toutes les bonnes réponses.";
        /**/
        else if (num.equals("6-1")) poiGame = "Pour découvrir le \"secret du gypaète\" à la dernière étape, vous allez devoir trouver la bonne réponse dans chacun des articles du parcours.\n" +
                "\n" +
                "À Payolle, je suis le roi de l'apnée.;Desman;Desman;Couvain;Lagopède alpin";
        else if (num.equals("6-2")) poiGame = "Fleur la plus toxique de France.;Aconit napel;Aconit tue-loup;Aconit napel;Végétarien";
        else if (num.equals("6-3")) poiGame = "Aquila chrysaetos en français.;Aigle royal;Couvain;Vautour fauve;Aigle royal";
        else if (num.equals("6-4")) poiGame = "Animal à raquettes.;Lagopède alpin;Isard;Campagnol;Lagopède alpin";
        else if (num.equals("6-5")) poiGame = "Indice de présence odorant.;Crotte;Crotte;Parfum;Poil";
        else if (num.equals("6-6")) poiGame = "Je ne mange pas de viande.;Végétarien;Endémique;Végétarien;Mammifère";
        else if (num.equals("6-7")) poiGame = "Maternité des fourmis.;Couvain;Couvain;Cave;Niche";
        else if (num.equals("6-8")) poiGame = "LASTPOI;Bravo ! vous avez bien répondu à ;Félicitations, vous avez découvert le \"secret du gypaète\" !\n\n"
                +"Le gypaète barbu est le seul oiseau à posséder une barbe. En fait ce sont des plumes qui ressemblent étrangement à des poils et qui forment une belle barbichette. Le gypaète est le plus grand rapace d'Europe, il a la particularité de se nourrir presque exclusivement d'os."
                +";Pour découvrir le \"secret du gypaète\" vous devez obtenir toutes les bonnes réponses.";

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


