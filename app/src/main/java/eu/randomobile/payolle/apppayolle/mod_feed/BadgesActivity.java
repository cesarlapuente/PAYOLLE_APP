package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;


public class BadgesActivity extends Activity {
    private MainApp app;
    ImageButton btn_home;
    ImageButton btn_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_badges);

        this.app = (MainApp) getApplication();

        app.getSuccessByRoute("toto"); // TODO change by route name in route loop to check associated badges
        capturarControles();
        escucharEventos();
    }

    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
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
