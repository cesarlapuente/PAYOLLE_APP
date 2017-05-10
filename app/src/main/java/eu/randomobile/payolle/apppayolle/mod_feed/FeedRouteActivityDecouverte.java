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
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    ImageButton btn_game;

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
        btn_game = (ImageButton) findViewById(R.id.btn_footer_game);

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
        /*btn_map.setOnClickListener( //Not usefull, we are already here
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });*/
        btn_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, InfoDecouverteActivity.class);
                        startActivity(intent);
                    }
                });
        btn_game.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteActivityDecouverte.this, GameInfoActivity.class);
                        startActivity(intent);
                    }
                });
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
        final ArrayList<Route> alRoute = app.getRoutesListDE();
        //final ArrayList<Route> alRoute = app.getDBHandler().getRouteListByCateg("Decouverte");
        Log.d("PIERRE DEBUG", "Routes decouvertes");
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
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
                /*Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteActivityDecouverte.this, R.drawable.poi0);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise = iconFactory.fromDrawable(iconDrawable);*/
                Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteActivityDecouverte.this, R.drawable.poi_depart3x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_start = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteActivityDecouverte.this, R.drawable.poi_info3x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_info = iconFactory.fromDrawable(iconDrawable);


                // if Poi not null
                // delete from the view, not usefull here, WARNING there is a conversion double -> int problem for POIs
                /*if(alPoi != null) {
                    for (Poi poi : alPoi) {
                        // create poiPosition Lat/Lng
                        // Icon icon = determinateCategoryPoi(poi);
                        LatLng poiPosition = new LatLng(poi.getCoordinates().getLatitude(), poi.getCoordinates().getLongitude());
                        //Create marker from Poi

                        MarkerViewOptions marker = new MarkerViewOptions()
                                .position(poiPosition)
                                .title(poi.getTitle())
                                .icon(icon_balise);
                        Log.d("PierreLog : MArker bug ", poi.getTitle() + " lat " + marker.getPosition().getLatitude()+ " long "+ marker.getPosition().getLongitude());

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

                }*/
                if(alRoute != null) {
                    int i = 0;
                    //8 colors, repetable
                    ArrayList<Integer> colors = new ArrayList(Arrays.asList(Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.rgb(255,128,0), Color.CYAN, Color.rgb(127,0,255), Color.rgb(127,255,0)));
                    for (Route route : alRoute) {
                        if (route.getTrack() != null) {
                            PolylineOptions track = WKTUtil.getPolylineFromWKTLineStringFieldFEED(route.getTrack()).color(colors.get(i%colors.size()));
                            LatLng coord = WKTUtil.getFirstLatLngFromWKTLineStringFieldFEED(route.getTrack());
                            Log.d("Pierre debug", "PoiPoiPoi : " + coord.getLongitude() +"      " + coord.getLatitude());
                            MarkerViewOptions marker = new MarkerViewOptions()
                                    .position(coord)
                                    .title("Depart : " + route.getTitle())
                                    .icon(icon_balise_start);


                            mapboxMap.addPolyline(track);
                            mapboxMap.addMarker(marker);
                        }
                        i++;
                    }
                }

                /*POI Services*/
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.936020,0.301284)).title("WC 2").icon(icon_balise_info));
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.942485,0.284836)).title("WC 1").icon(icon_balise_info));
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.942697,0.283989)).title("Point info").icon(icon_balise_info));
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.942441,0.284750)).title("Parking 1").icon(icon_balise_info));
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.937415,0.271881)).title("Parking 2").icon(icon_balise_info));
                mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(42.942822,0.278855)).title("Parking 3").icon(icon_balise_info));


                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(42.941305, 0.281269))  // set the camera's center position -> Focus on Payolle, no dynamics
                                .zoom(12)  // set the camera's zoom level
                                .tilt(20)  // set the camera's tilt
                                .build()));

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        //for (Polyline pol : mapboxMap.getPolylines()) {
                        for (int i = 0; i<mapboxMap.getPolylines().size(); i++) {
                            if (isPointInPolygon(new LatLng(point.getLatitude(), point.getLongitude()), mapboxMap.getPolylines().get(i))) {
                                Route route = alRoute.get(i);
                                Log.d("Pierre debug", "OnclickTrack : " + route.getTitle());
                                Intent intent = new Intent(FeedRouteActivityDecouverte.this, FeedRouteDetailsDecouverte.class);
                                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_NID, route.getNid());
                                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_DISTANCE, route.getDistanceMeters());
                                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_TITLE_ROUTE, route.getTitle());
                                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_MAP_URL, route.getUrlMap());
                                startActivity(intent);
                            }
                        }
                    }
                });
            }

        });
    }

    /*2 methods for OnPolylineClickListener from https://github.com/mapbox/mapbox-gl-native/issues/3720*/
    public boolean isPointInPolygon(LatLng coordsOfPoint, Polyline pol) {

        List<LatLng> latlngsOfPolygon =  extractPolygonToPoints(pol);
        int i;
        int j;
        boolean contains = false;
        for (i = 0, j = latlngsOfPolygon.size() - 1; i < latlngsOfPolygon.size(); j = i++) {
            if ((latlngsOfPolygon.get(i).getLongitude() > coordsOfPoint.getLongitude()) != (latlngsOfPolygon.get(j).getLongitude() > coordsOfPoint.getLongitude()) &&
                    (coordsOfPoint.getLatitude() < (latlngsOfPolygon.get(j).getLatitude() - latlngsOfPolygon.get(i).getLatitude()) * (coordsOfPoint.getLongitude() - latlngsOfPolygon.get(i).getLongitude()) / (latlngsOfPolygon.get(j).getLongitude() - latlngsOfPolygon.get(i).getLongitude()) + latlngsOfPolygon.get(i).getLatitude())) {
                contains = !contains;
            }
        }
        return contains;
    }
    public List<LatLng> extractPolygonToPoints(Polyline p) {
        List <LatLng> latlngsOfPolygon = new ArrayList<>();
        for (int x = 0; x < p.getPoints().size(); ++x) {
            LatLng coords = new LatLng(p.getPoints().get(x).getLatitude(), p.getPoints().get(x).getLongitude());
            latlngsOfPolygon.add(coords);
        }
        return latlngsOfPolygon;
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
