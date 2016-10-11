package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wikitude.architect.ArchitectView;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_feed.wikitude.BasicArchitectActivity;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;


public class RAActivity extends BasicArchitectActivity{
    public static final String PARAM_KEY_JSON_POI_DATA		=	"json_poi_data_string";

    protected String poiDataJSONString;

    private Poi selectedPoi;

    private ImageButton btn_home;
    private ImageButton btn_return;

    MainApp app;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        app = (MainApp)getApplication();

        Bundle b = getIntent().getExtras();
        if(b != null){
            poiDataJSONString = b.getString(PARAM_KEY_JSON_POI_DATA);
        }

        capturarControles();
        inicializarForm();
        escucharEventos();

    }

    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
    }


    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );


        // Cargar datos
        loadData();

        // Establecer el radio de distancia
        String radiusStr = String.valueOf(app.MAX_DISTANCE_SEARCH_GEOMETRIES_RA);
        RAActivity.this.callJavaScript("World.setRadiousMetters", new String[] { radiusStr });



        // Escuchar el evento de cuando se pulsa un elemento
        this.architectView.registerUrlListener(new ArchitectView.ArchitectUrlListener() {
            // fetch e.g. document.location = "architectsdk://markerselected?id=1";
            public boolean urlWasInvoked(String uriString) {
                Log.d("Milog", "URL was invoqued: " + uriString);
                Uri invokedUri = Uri.parse(uriString);
                String idClicked = invokedUri.getQueryParameter("id");
                if ("markerselected".equalsIgnoreCase(invokedUri.getHost()) && idClicked != null) {
                    // TODO si se pulsa un poi
                    Log.d("Milog", "Pulsado un poi con id: " + idClicked);

                    for(Poi poi : app.getPoisList()){
                        if(poi.getNid().equals(idClicked)){
                            RAActivity.this.selectedPoi = poi;
                            Log.d("POI s?lectionn? : ", selectedPoi.getNid());
                        }
                    }

                    Dialog poiInfo = new Dialog(RAActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
//                    poiInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    poiInfo.getWindow().setGravity(Gravity.CENTER);
                    poiInfo.setContentView(R.layout.mod_ra_popup);

                    TextView poi_title = (TextView) poiInfo.findViewById(R.id.poi_title);

                    poi_title.setText(selectedPoi.getTitle());

                    if(selectedPoi.getMainImage()!=null) {
                        ImageView poi_image = (ImageView) poiInfo.findViewById(R.id.poi_image);
                        BitmapManager.INSTANCE.loadBitmap(selectedPoi.getMainImage(), poi_image, poi_image.getWidth(), poi_image.getHeight());
                    }

                    poiInfo.show();

//                    builder.setMessage("Coffre " + idClicked + " !")
//                            .setPositiveButton("Ouvrir", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                }
//                            })
//                            .setNegativeButton("Anuler", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // User cancelled the dialog
//                                }
//                            });
//                    builder.create().show();
                }
                return false;
            }
        });
    }



    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
    }

    private void escucharEventos(){
        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RAActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RAActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
    }


    private void inicializarForm() {
        // Poner el t?tulo de la ventana
//        this.tvTitle.setText(titleGame.toUpperCase());

    }





    boolean isLoading = false;

    final Runnable loadData = new Runnable() {

        @Override
        public void run() {

            isLoading = true;

            final int WAIT_FOR_LOCATION_STEP_MS = 2000;

            while (RAActivity.this.lastKnownLocaton == null && !RAActivity.this.isFinishing()) {

                RAActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d("Milog","Location fetching");
                        //Toast.makeText(RAActivity.this, "Location fetching", Toast.LENGTH_SHORT).show();
                    }
                });

                try {
                    Thread.sleep(WAIT_FOR_LOCATION_STEP_MS);
                } catch (InterruptedException e) {
                    break;
                }
            }

            if (RAActivity.this.lastKnownLocaton != null && !RAActivity.this.isFinishing()) {
                Log.d("RAActivity", "load Pois from Json");
                // Poner los datos
                RAActivity.this.callJavaScript("World.loadPoisFromJsonData", new String[] { poiDataJSONString });
            }

            isLoading = false;
        }
    };


    protected void loadData() {
        if (!isLoading) {
            final Thread t = new Thread(loadData);
            t.start();
        }
    }


    /**
     * call JacaScript in architectView
     * @param methodName
     * @param arguments
     */
    private void callJavaScript(final String methodName, final String[] arguments) {
        final StringBuilder argumentsString = new StringBuilder("");
        for (int i= 0; i<arguments.length; i++) {
            argumentsString.append(arguments[i]);
            if (i<arguments.length-1) {
                argumentsString.append(", ");
            }
        }

        if (this.architectView!=null) {
            final String js = ( methodName + "( " + argumentsString.toString() + " );" );
            this.architectView.callJavascript(js);
        }
    }
}
