package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class GameInfoActivity extends Activity {

    private MainApp app;

    private ImageButton btn_home;
    private ImageButton btn_return;
    private ImageButton btn_map;
    private ImageButton btn_info;
    private ImageButton btn_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_game_info);

        capturarControles();
        escucharEventos();

    }

    private void capturarControles() {
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_map = (ImageButton) findViewById(R.id.btn_footer_map);
        btn_list = (ImageButton) findViewById(R.id.btn_footer_list);
    }

    private void escucharEventos() {
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameInfoActivity.this, InfoDecouverteActivity.class);
                startActivity(intent);
            }
        });
        btn_list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameInfoActivity.this, DecouverteRoutesListActivity.class);
                        startActivity(intent);
                    }
                });
        btn_map.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameInfoActivity.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });
        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameInfoActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GameInfoActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
    }
}


