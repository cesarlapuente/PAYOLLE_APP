package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.text.Normalizer;
import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedRouteActivity extends Activity  {
    private MainApp app;
    ImageButton btn_list;
    ImageButton btn_home;
    ImageButton btn_return;
    ImageButton btn_map;
    ImageButton btn_badges;

    private ImageButton btn_info;
    public MapView mapView;
    private MapboxMap prvMapBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (MainApp) getApplication();
        //Mapbox.getInstance(this, getString(R.string.access_token));
        Mapbox.getInstance(this, "pk.eyJ1IjoibGFwdWVudGUiLCJhIjoiY2l4aWVrdHprMDAwNDJ3bm5hcmR3YnlkaSJ9.jTCAsUxKNME64cQFF0wVcQ");
        //MapboxAccountManager.start(this, "pk.eyJ1IjoibGFwdWVudGUiLCJhIjoiY2l4aWVrdHprMDAwNDJ3bm5hcmR3YnlkaSJ9.jTCAsUxKNME64cQFF0wVcQ");

        setContentView(R.layout.activity_feed_route);

        PermissionsRequest.askForPermission(FeedRouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
//        while (ContextCompat.checkSelfPermission(FeedRouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
////
//        }
        PermissionsRequest.askForPermission(FeedRouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//        while (ContextCompat.checkSelfPermission(FeedRouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
////
//        }

        initMapView(savedInstanceState);
        capturarControles();
        escucharEventos();
    }


    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_map = (ImageButton) findViewById(R.id.btn_footer_map);
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        btn_list = (ImageButton) findViewById(R.id.btn_footer_list);
        btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);

    }
    private void escucharEventos(){
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedRouteActivity.this.finish();
                    }
                });
        /*btn_map.setOnClickListener( //Not usefull, we are already here
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivity.this, FeedRouteActivity.class);
                        startActivity(intent);
                    }
                });*/
        btn_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivity.this, InfoOrientationActivity.class);
                        startActivity(intent);
                    }
                });
        btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });
        btn_list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivity.this, RoutesListActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void initMapView(Bundle savedInstanceState){

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        //mapView.setStyleUrl("mapbox://styles/mapbox/dark-v9");
        Log.d("JmLog", "MAPVIEW  ? =>" + mapView.isEnabled());
        // <-- Declarations -->
        final ArrayList<Route> alRoute = app.getRoutesListCO();
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                Log.d("JmLog", "OnMapReady yes !");
                // Customize map with markers, polylines, etc.
                mapboxMap.getMyLocationViewSettings().setBackgroundTintColor(Color.rgb(0,102,204));
                mapboxMap.getMyLocationViewSettings().setForegroundTintColor(Color.rgb(0,128,255));
                FeedRouteActivity.this.prvMapBox = mapboxMap;
                // style of the map
                //FeedRouteActivity.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteActivity.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);
                if(FeedRouteActivity.this.prvMapBox != null) {
                    FeedRouteActivity.this.prvMapBox.getUiSettings().setCompassEnabled(true);
                    FeedRouteActivity.this.prvMapBox.setMyLocationEnabled(true);
                }

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(FeedRouteActivity.this);
                Icon icon_balise = iconFactory.fromBitmap(resize(R.drawable.parcours_0));
                Icon icon_balise_start = iconFactory.fromBitmap(resize(R.drawable.poi_depart3x));

                for(Route route : alRoute){
                    ArrayList<ResourcePoi> alPois =  route.getPois();
                    for(ResourcePoi poi : alPois){
                        LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());
                        //Create marker from Poi
                        MarkerViewOptions marker = new MarkerViewOptions()
                                .position(poiPosition)
                                .title(poi.getTitle())
                                .icon(icon_balise);
                        //add Poi
                        if(Normalizer.normalize(poi.getTitle(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase().concat("      ").substring(0,6).equals("depart")) {
                            marker.icon(icon_balise_start);
                        }
                        alLatLng.add(poiPosition);
                        //add Marker
                        mapboxMap.addMarker(marker);
                    }
                }

                    if(!alLatLng.isEmpty()) {
                        //Create LatLng Fit all Markers
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .includes(alLatLng)
                                .build();
                        // zoom Camera on all Markers
                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                    }

                }

        });
    }


    private Bitmap resize(int res) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), res);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bm, 110, 110, false);
        return bitmapResized;
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
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
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
