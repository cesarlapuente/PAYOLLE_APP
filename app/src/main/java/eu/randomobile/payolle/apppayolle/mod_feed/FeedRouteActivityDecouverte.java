package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.WKTUtil;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedRouteActivityDecouverte extends Activity  {
    private MainApp app;
    ImageButton btn_list;
    ImageButton btn_home;
    ImageButton btn_return;
    ImageButton btn_map;
    //ImageButton btn_badges;

    private ImageButton btn_info;
    public MapView mapView;
    private MapboxMap prvMapBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (MainApp) getApplication();
        MapboxAccountManager.start(this, getString(R.string.access_token));

        setContentView(R.layout.activity_feed_route_decouverte);

        PermissionsRequest.askForPermission(FeedRouteActivityDecouverte.this, Manifest.permission.ACCESS_COARSE_LOCATION);
//        while (ContextCompat.checkSelfPermission(FeedRouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
////
//        }
        PermissionsRequest.askForPermission(FeedRouteActivityDecouverte.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        //btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);

    }
    private void escucharEventos(){
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedRouteActivityDecouverte.this.finish();
                    }
                });
        btn_map.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });
        btn_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, InfoDecouverteActivity.class);
                        startActivity(intent);
                    }
                });
        /*btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });*/
        btn_list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, DecouverteRoutesListActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void initMapView(Bundle savedInstanceState){

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        Log.d("JmLog", "MAPVIEW  ? =>" + mapView.isEnabled());
        // <-- Declarations -->
        final ArrayList<Poi> alPoi =  app.getPoisList();
        final ArrayList<Route> alRoute = app.getDBHandler().getRouteListByCateg("Decouverte");
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                Log.d("JmLog", "OnMapReady yes !");
                // Customize map with markers, polylines, etc.
                FeedRouteActivityDecouverte.this.prvMapBox = mapboxMap;
                // style of the map
                FeedRouteActivityDecouverte.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteActivityDecouverte.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);
                if(FeedRouteActivityDecouverte.this.prvMapBox != null) {
                    FeedRouteActivityDecouverte.this.prvMapBox.getUiSettings().setCompassEnabled(true);
                    FeedRouteActivityDecouverte.this.prvMapBox.setMyLocationEnabled(true);
                }

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(FeedRouteActivityDecouverte.this);
                Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteActivityDecouverte.this, R.drawable.poi0);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise = iconFactory.fromDrawable(iconDrawable);


                // if Poi not null
                if(alPoi != null) {
                    for (Poi poi : alPoi) {
                        // create poiPosition Lat/Lng
                        // Icon icon = determinateCategoryPoi(poi);
                        LatLng poiPosition = new LatLng(poi.getCoordinates().getLatitude(), poi.getCoordinates().getLongitude());
                        //Create marker from Poi

                        MarkerViewOptions marker = new MarkerViewOptions()
                                .position(poiPosition)
                                .title(poi.getTitle())
                                .icon(icon_balise);



                        // custom infoWindow
                        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Nullable
                            private LayoutInflater mInflater = (LayoutInflater) FeedRouteActivityDecouverte.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                View convertView = mInflater.inflate(R.layout.activity_feed_route_decouverte_popup, null);

                                ImageView poi_image = (ImageView)convertView.findViewById(R.id.poi_image);
                                TextView poi_title = (TextView)convertView.findViewById(R.id.poi_title);

                                poi_title.setText(marker.getTitle());

                                return convertView;


                            }
                        });

                        mapboxMap.setOnInfoWindowClickListener(
                                new MapboxMap.OnInfoWindowClickListener() {
                                    @Override
                                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, POIDetailsActivity.class);
                                        intent.putExtra(POIDetailsActivity.PARAM_KEY_TITLE_POI, marker.getTitle());
                                        startActivity(intent);
                                        return false;
                                    }
                                }
                        );

                        //add Poi
                        alLatLng.add(poiPosition);
                        //add Marker
                        mapboxMap.addMarker(marker);

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
                if(alRoute != null) {
                    for (Route route : alRoute) {
                        if (route.getTrack() != null) {
                            mapboxMap.addPolyline(WKTUtil.getPolylineFromWKTLineStringFieldFEED(route.getTrack()).color(Color.BLUE));
                        }
                    }
                }
            }

        });
    }


    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 110, 110, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }


    // Add the mapView lifecycle to the activity's lifecycle methods
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

    public class AdapterInfoWindows extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context ctx;

        class ViewHolder{
            TableLayout poi_popup;
            TextView poi_title;
            ImageView poi_image;
        }

        //View mView = setContentView(R.layout.activity_feed_route_decouverte_popup);

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }


}
