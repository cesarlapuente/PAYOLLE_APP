package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;

/**
 * Created by 44Screens on 2017-03-20.
 */

public class FeedSettingsActivity extends Activity{
    private MainApp app;
    ImageButton btn_home;
    ImageButton btn_return;
    Button btn_fr;
    Button btn_en;
    Button btn_es;
    Button btn_cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.activity_feed_settings);

        capturarControles();
        escucharEventos();

    }

    private void capturarControles() {

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_fr = (Button) findViewById(R.id.lang_fr);
        btn_en = (Button) findViewById(R.id.lang_en);
        btn_es = (Button) findViewById(R.id.lang_es);
        btn_cache = (Button) findViewById(R.id.clear_cache);
    }

    private void escucharEventos() {
        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedSettingsActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedSettingsActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
        btn_fr.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO change language here
                    }
                });
        btn_en.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO change language here
                    }
                });
        btn_es.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO change language here
                    }
                });
        btn_cache.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO clear the cache here (database)
                    }
                });
    }
}