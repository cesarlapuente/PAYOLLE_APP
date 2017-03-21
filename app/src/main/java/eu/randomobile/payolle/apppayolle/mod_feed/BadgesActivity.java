package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;


public class BadgesActivity extends Activity {
    private MainApp app;
    ImageButton btn_home;
    ImageButton btn_return;
    private ArrayList<Route> arrayRoutes = null;
    ImageView badge1;
    ImageView badge2;
    ImageView badge3;
    ImageView badge4;
    ImageView badge5;
    ImageView badge6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_badges);

        this.app = (MainApp) getApplication();

        arrayRoutes = app.getRoutesListCO();


        //app.getSuccessByRoute("toto"); // TODO change by route name in route loop to check associated badges
        capturarControles();


        if(arrayRoutes.size()>5) { //Normalement c'est bon si on ne change pas le nombre de routes CO
            Log.d("PierreLog : Badge", "");
            if(app.getSuccessByRoute(arrayRoutes.get(0).getTitle())){
                badge1.setImageResource(R.drawable.badges_competition_actif);
                Log.d("PierreLog : Badge1", "");
            }
            if(app.getSuccessByRoute(arrayRoutes.get(1).getTitle()))badge2.setImageResource(R.drawable.badges_competition_actif);
            if(app.getSuccessByRoute(arrayRoutes.get(2).getTitle()))badge3.setImageResource(R.drawable.badges_competition_actif);
            if(app.getSuccessByRoute(arrayRoutes.get(3).getTitle()))badge4.setImageResource(R.drawable.badges_competition_actif);
            if(app.getSuccessByRoute(arrayRoutes.get(4).getTitle()))badge5.setImageResource(R.drawable.badges_competition_actif);
            if(app.getSuccessByRoute(arrayRoutes.get(5).getTitle()))badge6.setImageResource(R.drawable.badges_competition_actif);
        }


        escucharEventos();
    }

    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        badge1 = (ImageView) findViewById(R.id.badge1);
        badge2 = (ImageView) findViewById(R.id.badge2);
        badge3 = (ImageView) findViewById(R.id.badge3);
        badge4 = (ImageView) findViewById(R.id.badge4);
        badge5 = (ImageView) findViewById(R.id.badge5);
        badge6 = (ImageView) findViewById(R.id.badge6);
    }

    private void escucharEventos() {

        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BadgesActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BadgesActivity.this.finish();
                    }
                });

    }
}
