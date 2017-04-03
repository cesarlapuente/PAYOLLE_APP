package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class HomeActivity extends Activity {
    //ImageButton btn_general_map;
    //ImageButton btn_badges;
    //ImageButton btn_info;
    //ImageButton btn_read;
    private MainApp app; //TODO delete after synchro fix
    ImageButton btn_settings;
    FrameLayout btn_orientation;
    FrameLayout btn_discover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.app = (MainApp) getApplication();
        //btn_general_map = (ImageButton) findViewById(R.id.btn_footer_poi);
        //btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);
        //btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        //btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
        btn_settings = (ImageButton) findViewById(R.id.btn_settings);
        btn_orientation = (FrameLayout) findViewById(R.id.btn_orientation);
        btn_discover = (FrameLayout) findViewById(R.id.btn_discover);



        /*btn_general_map.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });

        btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });

        btn_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, InfoDecouverteActivity.class);
                        startActivity(intent);
                    }
                });*/

        btn_settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, FeedSettingsActivity.class);
                        startActivity(intent);
                    }
                });
        btn_orientation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(app.getRoutesListCO().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Application is loading ...", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(HomeActivity.this, RoutesListActivity.class);
                            startActivity(intent);
                        }
                    }
                });

        btn_discover.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(app.getRoutesListDE().isEmpty() || app.getPoisList().isEmpty()){
                            Toast.makeText(getApplicationContext(), "Application is loading ...", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(HomeActivity.this, DecouverteRoutesListActivity.class);
                            startActivity(intent);
                        }
                    }
                });

        /*btn_read.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, LireActivity.class);
                        startActivity(intent);
                    }
                });*/


    }
}