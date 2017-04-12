package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class FeedRouteBalise extends Activity implements  LocationListener {

    private MainApp app;
    MapView mapView;
    private MapboxMap prvMapBox;
    ImageButton btn_home;
    ImageButton btn_return;
    ImageView btn_info;
    Timer TChrono;
    LinearLayout balise_distance_ok;
    LinearLayout balise_distance_nok;
    FrameLayout check1, check2, check3;
    ImageView validation_ok;
    ImageView validation_skip;
    ImageView validation_code1;
    ImageView validation_code2;
    ImageView validation_code3;
    ImageView validation_close;
    boolean codeOK;

    LocationManager locationManager;
    boolean isPlaying;
    boolean isSetLocation;
    double lat;
    double lon;
    long distance = 0;
    TextView txt_route_title,
            txt_chrono_hours,
            txt_chrono_minutes,
            txt_chrono_secondes,
            txt_distance,
            txt_nb_balise,
            txt_nb_balise_max;
    int timeChronometer = 0;
    boolean chronosStatus = false;
    static final int KEY_ROUTE = 0;
    public static int NB_POIS_VALIDATE = 0;
    public static int NB_POIS_INVALIDATE = 0;

    HashMap<Long,String> hsvalidate = new HashMap<>();

    ImageView btn_pause;
    Route route;
    String paramNid;
    public static final String PARAM_KEY_NID = "nid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.app = (MainApp) getApplication();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);
            for (Route route : app.getRoutesListCO()) {
                if (route.getNid().equals(paramNid)) {
                    this.route = route;
                    Log.d("JmLog", "Objet route : " + this.route.getTitle() + " " + this.route.getUrlMap() + " " + route.getImages());
                }
            }
        }
        setContentView(R.layout.activity_feed_route_balise);
        // PermissionsRequest.askForPermission(FeedRouteBalise.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        //PermissionsRequest.askForPermission(FeedRouteBalise.this, Manifest.permission.ACCESS_FINE_LOCATION);

        this.initLocationManager();
        initMapView(savedInstanceState);
        capturarControles();
        resetData();
        escucharEventos();
        doChronometer();
        setData();


    }
    private void initLocationManager() {
        if ( Build.VERSION.SDK_INT >= 23 && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return ;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, this);
        //GET LAST KNOW LOCATION
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //if there is a valid location
        if (lastLocation != null) {
            //init coordinates
            this.lat = lastLocation.getLatitude();
            this.lon = lastLocation.getLongitude();
        }

    }

    private void capturarControles() {

        txt_route_title = (TextView) findViewById(R.id.route_title);

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_info = (ImageView) findViewById(R.id.btn_info_route);
        btn_pause = (ImageView) findViewById(R.id.btn_pause);

        txt_distance = (TextView) findViewById(R.id.txt_distanceb);
        txt_nb_balise = (TextView) findViewById(R.id.txt_nb_balise);
        txt_nb_balise_max = (TextView) findViewById(R.id.txt_nb_balise_max);

        txt_chrono_hours = (TextView) findViewById(R.id.chrono_hours);
        txt_chrono_minutes = (TextView) findViewById(R.id.chrono_minutes);
        txt_chrono_secondes = (TextView) findViewById(R.id.chrono_seconds);

        codeOK = false;

    }

    private void resetData(){
        NB_POIS_VALIDATE = 0;
        txt_nb_balise.setText("0");
        txt_distance.setText("0");
    }
    private void escucharEventos() {
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteBalise.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedRouteBalise.this.finish();
                    }
                });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedRouteBalise.this);
                stopChronometer();

                builder.setTitle(getString(R.string.permission_message_attention));
                builder.setMessage(getString(R.string.course_finish));

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishCourse();
                    }
                });
                builder.setNegativeButton(R.string.permission_message_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startChronometer();
                    }
                });
                builder.show();

            }
        });
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedRouteBalise.this, InfoOrientationActivity.class);
                startActivity(intent);
            }
        });

    }

    public void finishCourse(){
        isPlaying = false;
        Intent intent = new Intent(FeedRouteBalise.this, ResultsActivity.class);
        String chronoValue = txt_chrono_hours.getText()+":"+txt_chrono_minutes.getText()+":"+txt_chrono_secondes.getText();
        intent.putExtra(ResultsActivity.CHRONO_TIME, chronoValue);
        intent.putExtra(ResultsActivity.BALISE_VALIDATE, String.valueOf(NB_POIS_VALIDATE));
        intent.putExtra(ResultsActivity.BALISE_VALIDATE_MAX, String.valueOf(route.getPois().size()));
        intent.putExtra(ResultsActivity.DISTANCE_TRAVELLED, txt_distance.getText());
        intent.putExtra(ResultsActivity.PARAM_KEY_NID, route.getNid());


        /*Bagdes*/ //TODO PierreBadges
        app.setSuccessByRoute(route.getTitle(),2);

        startActivity(intent);
        resetData();
        TChrono.purge();
    }
    private void initMapView(Bundle savedInstanceState){
        MapboxAccountManager.start(this, getString(R.string.access_token));
        mapView = (MapView) findViewById(R.id.mapview);

        Log.d("Log", "route is null? " + " " + this.route.getTitle());
        final ArrayList<ResourcePoi> alPoi = this.route.getPois();
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                Log.d("JmLog", "OnMapReady yes !");
                // Customize map with markers, polylines, etc.
                FeedRouteBalise.this.prvMapBox = mapboxMap;
                // style of the map
                FeedRouteBalise.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteBalise.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);
                if (FeedRouteBalise.this.prvMapBox != null) {
                    FeedRouteBalise.this.prvMapBox.getUiSettings().setCompassEnabled(true);
                    FeedRouteBalise.this.prvMapBox.setMyLocationEnabled(true);
                }


                if (route.getDifficulty_tid().equals("22")) {
                    mapboxMap.setMyLocationEnabled(false);
                }
                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(FeedRouteBalise.this);
                Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteBalise.this, R.drawable.parcours_0);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise = iconFactory.fromDrawable(iconDrawable);


                for (final ResourcePoi poi : alPoi) {
                    LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());

                    MarkerViewOptions mk = new MarkerViewOptions()
                            .position(poiPosition)
                            .title(poi.getTitle())
                            .icon(icon_balise)
                            .snippet(poi.getCode1());

                    //alLatLng.add(poiPosition);
                    //mapboxMap.addMarker(mk);

                    mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                        @Nullable
                        private LayoutInflater mInflater = (LayoutInflater) FeedRouteBalise.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



                        @Override
                        public View getInfoWindow(@NonNull final Marker marker) {

                            Location temp = new Location(LocationManager.GPS_PROVIDER);

                            View convertView = mInflater.inflate(R.layout.activity_feed_route_balise_pop_up, null);

                            balise_distance_ok = (LinearLayout) convertView.findViewById(R.id.balise_distance_ok);
                            balise_distance_nok = (LinearLayout) convertView.findViewById(R.id.balise_distance_nok);

                            temp.setLatitude(marker.getPosition().getLatitude());
                            temp.setLongitude(marker.getPosition().getLongitude());
                            if (mapboxMap.getMyLocation().distanceTo(temp) >= -1.0) { //TODO on prod, change with <=20.0 or >= -1.0 in debug
                                //Log.d("Debug", "marker lat / lon : " + marker.getPosition().getLatitude() + " / " + marker.getPosition().getLongitude());
                                //Log.d("Debug", "MyLocation lat / lon : " + mapboxMap.getMyLocation().getLatitude() + " / " + mapboxMap.getMyLocation().getLongitude());
                                //Log.d("Debug", "Difference : " + mapboxMap.getMyLocation().distanceTo(temp) + " m");

                                balise_distance_ok.setVisibility(View.VISIBLE);
                                balise_distance_nok.setVisibility(View.INVISIBLE);

                                TextView code1, code2, code3;
                                check1 = (FrameLayout) convertView.findViewById(R.id.check1);
                                check2 = (FrameLayout) convertView.findViewById(R.id.check2);
                                check3 = (FrameLayout) convertView.findViewById(R.id.check3);

                                code1 = (TextView) convertView.findViewById(R.id.code1);
                                code2 = (TextView) convertView.findViewById(R.id.code2);
                                code3 = (TextView) convertView.findViewById(R.id.code3);

                                validation_ok = (ImageView) convertView.findViewById(R.id.validation_ok);
                                validation_skip = (ImageView) convertView.findViewById(R.id.validation_skip);
                                validation_ok.setVisibility(View.INVISIBLE);

                                validation_code1 = (ImageView) convertView.findViewById(R.id.validation_code1);
                                validation_code2 = (ImageView) convertView.findViewById(R.id.validation_code2);
                                validation_code3 = (ImageView) convertView.findViewById(R.id.validation_code3);

                                Random random = new Random();
                                String false1 = marker.getSnippet();
                                String false2 = marker.getSnippet();
                                while (false1.equals(marker.getSnippet())){
                                    false1 = "" + (random.nextInt(1000 - 1) + 1);
                                }
                                while (false2.equals(marker.getSnippet()) | false2.equals(false1)){
                                    false2 = "" + (random.nextInt(1000 - 1) + 1);
                                }

                                Log.d("Debug","false1= " + false1 + " / false2=" + false2);

/*
                            Random r = new Random();
                            int i = r.nextInt(3 - 1) + 1;
                            int j = i + 1;
                            if (j > 2)
                                j = 1;
                            int k = j + 1;
                            if (k > 2)
                                k = 1;
                            //Log.d("Debug","i= " + i + " / j=" + j + " / k=" + k);
                            */

                                code1.setText(marker.getSnippet());
                                code2.setText(false1);
                                code3.setText(false2);


                                check1.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                codeOK = true;
                                                //gestionBalise(marker);
                                                validation_ok.setVisibility(View.VISIBLE);
                                                validation_code1.setImageResource(R.drawable.fondo_generic_marron);
                                                validation_code2.setImageResource(R.drawable.fondo_generic_codigo);
                                                validation_code3.setImageResource(R.drawable.fondo_generic_codigo);
                                            }
                                        });
                                check2.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //gestionBaliseError(marker);
                                                // gestionBalise(balise,code1)
                                                validation_ok.setVisibility(View.VISIBLE);
                                                codeOK = false;
                                                validation_code1.setImageResource(R.drawable.fondo_generic_codigo);
                                                validation_code2.setImageResource(R.drawable.fondo_generic_marron);
                                                validation_code3.setImageResource(R.drawable.fondo_generic_codigo);
                                            }
                                        });
                                check3.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //gestionBaliseError(marker);
                                                // gestionBalise(balise,code1)
                                                validation_ok.setVisibility(View.VISIBLE);
                                                codeOK = false;
                                                validation_code1.setImageResource(R.drawable.fondo_generic_codigo);
                                                validation_code2.setImageResource(R.drawable.fondo_generic_codigo);
                                                validation_code3.setImageResource(R.drawable.fondo_generic_marron);
                                            }
                                        });
                                validation_skip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        codeOK = false;
                                        mapboxMap.deselectMarker(marker);
                                    }
                                });

                                validation_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(codeOK){
                                            gestionBalise(marker);
                                        } else {
                                            gestionBaliseError(marker);
                                        }
                                    }
                                });
                            } else {

                                balise_distance_ok.setVisibility(View.INVISIBLE);
                                balise_distance_nok.setVisibility(View.VISIBLE);
                                validation_close = (ImageView) convertView.findViewById(R.id.validation_close);
                                validation_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mapboxMap.deselectMarker(marker);
                                    }
                                });


                            }


                            return convertView;
                        }
                    });

                    /*mapboxMap.setOnInfoWindowClickListener(
                            new MapboxMap.OnInfoWindowClickListener() {
                                @Override
                                public boolean onInfoWindowClick(@NonNull Marker marker) {
                                    checkBaliseToActivate(mapboxMap.getMyLocation(), marker);
                                    return false;
                                }
                            }
                    );*/
                    alLatLng.add(poiPosition);
                    mapboxMap.addMarker(mk);


                    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            if (hsvalidate.containsKey(marker.getId())) {
                                marker.hideInfoWindow();
                                return true;
                            } else {
                                return false;
                            }

                        }
                    });
                }
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .includes(alLatLng)
                        .include(new LatLng(mapboxMap.getMyLocation()))
                        .build();


                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
            }

        });

    }


    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 110, 110, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }


    public void stopChronometer(){isPlaying = false;}
    public void startChronometer(){isPlaying = true;}
    public void doChronometer() {
        TChrono = new Timer();
        isPlaying = true;
        TChrono.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seconds2chonometerView();
                        if (isPlaying)
                            timeChronometer++;
                    }
                });
            }
        }, 1000, 1000);
    }



    public void seconds2chonometerView() {
        int hours = timeChronometer / 3600;
        int minutes = (timeChronometer % 3600) / 60;
        int seconds = (timeChronometer % 3600) % 60;

        if (seconds < 10) {
            //secondes
            txt_chrono_secondes.setText("0" + String.valueOf(seconds));

        } else {
            txt_chrono_secondes.setText(String.valueOf(seconds));
        }

        if (minutes < 10) {
            //minutes
            txt_chrono_minutes.setText("0" + String.valueOf(minutes));
        } else {
            txt_chrono_minutes.setText(String.valueOf(minutes));
        }

        if (hours < 10) {
            //heures
            txt_chrono_hours.setText("0" + String.valueOf(hours));
        } else {
            txt_chrono_hours.setText(String.valueOf(hours));
        }
    }

    @Override
    public void onBackPressed() {
        if(isPlaying) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FeedRouteBalise.this);

            builder.setTitle(getString(R.string.permission_message_attention));
            builder.setMessage(getString(R.string.cronoExit));

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetData();
                    finish();
                }
            });
            builder.setNegativeButton(R.string.permission_message_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();
        }else {
            super.onBackPressed();
        }
    }
    public void gestionBalise(Marker marker){

        IconFactory iconFactory = IconFactory.getInstance(FeedRouteBalise.this);
        Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteBalise.this, R.drawable.parcours_validate);
        iconDrawable = resize(iconDrawable);
        Icon icon_validate = iconFactory.fromDrawable(iconDrawable);
        marker.setIcon(icon_validate);
        hsvalidate.put(marker.getId(),"validate");
        NB_POIS_VALIDATE++;
        txt_nb_balise.setText(String.valueOf(NB_POIS_VALIDATE));
        marker.hideInfoWindow();
        if((NB_POIS_VALIDATE + NB_POIS_INVALIDATE) == route.getPois().size()) {
            finishCourse();
        }
    }
    public void gestionBaliseError(Marker marker){

        IconFactory iconFactory = IconFactory.getInstance(FeedRouteBalise.this);
        Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteBalise.this, R.drawable.parcours_validate);
        iconDrawable = resize(iconDrawable);
        Icon icon_validate = iconFactory.fromDrawable(iconDrawable);
        marker.setIcon(icon_validate);
        hsvalidate.put(marker.getId(),"no validate");
        NB_POIS_INVALIDATE++;
        txt_nb_balise.setText(String.valueOf(NB_POIS_VALIDATE));
        marker.hideInfoWindow();
        if((NB_POIS_VALIDATE + NB_POIS_INVALIDATE) == route.getPois().size()) {
            finishCourse();
        }
    }
    private void setData() {
        txt_route_title.setText(this.route.getTitle());
        txt_distance.setText(String.format("%.2f", (distance) / 1000.0));
        txt_nb_balise.setText(String.format("%d", NB_POIS_VALIDATE));
        txt_nb_balise_max.setText(String.format("%d", route.getPois().size()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(getBaseContext(), "Location changed!! ", Toast.LENGTH_SHORT).show();
        prvMapBox.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(location))
                .zoom(16)
                .build());
        if (isSetLocation) {
            //dispatchGenericMotionEvent()
            Location locationFixed = new Location("Fixed");
            locationFixed.setLatitude(lat);
            locationFixed.setLongitude(lon);
            float distanceTemp = location.distanceTo(locationFixed);
            distance += distanceTemp;
            lat = location.getLatitude();
            lon = location.getLongitude();
            //mapView.getLocationDisplayManager().setAutoPanMode(LocationDisplayManager.AutoPanMode.NAVIGATION);
        } else {
            lat = location.getLatitude();
            lon = location.getLongitude();
            isSetLocation = true;
            //mapView.getLocationDisplayManager().setAutoPanMode(LocationDisplayManager.AutoPanMode.NAVIGATION);
        }
        //double distanceOut=Precision.round(distance,2);
        txt_distance.setText(String.format("%.2f", (distance) / 1000.0));
//        if (isNearToRoute(location)) {
//            LLavis.setVisibility(View.GONE);
//        } else {
//            LLavis.setVisibility(View.VISIBLE);
//            String distancePretty = prettyDistanceToRute();
//            textViewAvis.setText(getApplicationContext().getResources().getText(R.string.vous_etes) + " " + distancePretty + " en dehors du parcours");
//        }


    }

    public void checkBaliseToActivate(Location mylocation,Marker balise){
        Location temp = new Location(LocationManager.GPS_PROVIDER);
        if(balise != null && mylocation != null ){
            temp.setLatitude(balise.getPosition().getLatitude());
            temp.setLongitude(balise.getPosition().getLongitude());
            if (mylocation.distanceTo(temp) >= 10.0 || true) {
                balise.hideInfoWindow();
                Toast.makeText(FeedRouteBalise.this, "Courage ! rapprochez vous ! ", Toast.LENGTH_LONG).show();
            }

        }
        // pour le poi cliquer en question
        // checker si la localisation utilisateur est proche de cette balise
        // si oui proposer de valider
        // -> afficher pop up

        // sinon ( afficher "rapprochez vous" / ne rien faire)

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
