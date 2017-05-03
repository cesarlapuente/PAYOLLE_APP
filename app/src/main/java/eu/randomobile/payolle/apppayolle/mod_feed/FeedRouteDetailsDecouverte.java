package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
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
import eu.randomobile.payolle.apppayolle.mod_global.Util;
import eu.randomobile.payolle.apppayolle.mod_global.WKTUtil;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedRouteDetailsDecouverte extends Activity {
    private MapView mapView;
    private MapboxMap prvMapBox;
    private MainApp app;
    private Route route;
    public static final String PARAM_KEY_NID = "nid";
    public static final String PARAM_KEY_DISTANCE = "distance";
    public static final String PARAM_KEY_TITLE_ROUTE = "route_title";
    public static final String PARAM_KEY_CATEGORY_ROUTE = "route_category";
    public static final String PARAM_KEY_MAP_URL = "map_url";
    public static final String PARAM_KEY_COLOR_ROUTE = "route_color";

    private TextView txt_route_title;
    private TextView txt_distance;
    private TextView txt_duration;
    private TextView txt_description_body;
    FrameLayout btn_galery;
    ImageView main_image;

    ImageButton btn_home;
    ImageButton btn_return;
    //private ImageView img_difficulty ;
    //private ImageView img_stars;
    FloatingActionButton toggle_display_map_decouverte;

    Polyline route_polyline;


    String paramNid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.app = (MainApp)  getApplication();

        setContentView(R.layout.activity_feed_route_details_decouverte);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);
            for (Route route : app.getRoutesListDE()) {
                if (route.getNid().equals(paramNid)) {
                    this.route = route;
                    Log.d("JmLog", "Objet route : " + route.getTitle() + " " + route.getUrlMap() + " " + route.getImages());

                }
            }
        }

        /*Pierre debug POI image*/
        for (ResourcePoi poi : route.getPois()){
            Log.d("PierreLog", "Objet POI route : title : " + poi.getTitle() + " image : " + poi.getMainImage() + " lat " + poi.getLatitude()+ " long "+ poi.getLongitude());
        }

        initMapView(savedInstanceState);
        initComponents();
        setData();
        escucharEventos();
    }


    public void initComponents(){
        // <----------------->_TEXTVIEWS_DECLARATIONS_<----------------->

        txt_route_title = (TextView) findViewById(R.id.route_title);
//        txt_ramp = (TextView) findViewById(R.id.txt_ramp);
        txt_duration = (TextView) findViewById(R.id.route_duration);
        txt_distance = (TextView) findViewById(R.id.route_distance);
        txt_description_body = (TextView) findViewById(R.id.route_description);
        btn_galery = (FrameLayout) findViewById(R.id.btn_galerie);

        // <----------------->_IMAGEVIEWS_DECLARATIONS_<----------------->
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);

        main_image = (ImageView) findViewById(R.id.main_image);
        //img_difficulty = (ImageView) findViewById(R.id.route_difficulty);
        //img_stars = (ImageView) findViewById(R.id.stars_vote);

        toggle_display_map_decouverte = (FloatingActionButton)findViewById(R.id.toggle_display_map_decouverte);
    }

    private void escucharEventos() {
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteDetailsDecouverte.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });


        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedRouteDetailsDecouverte.this.finish();
                    }
                });


        btn_galery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteDetailsDecouverte.this, GaleryActivity.class);
                        intent.putParcelableArrayListExtra(GaleryActivity.PARAM_KEY_ARRAY_RESOURCES,route.getImages());
                        intent.putExtra(GaleryActivity.PARAM_KEY_ROUTE_NID, route.getNid());
                        startActivity(intent);
                    }
                });

        toggle_display_map_decouverte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.scrollView_decouverte).getVisibility() == View.GONE) {
                    findViewById(R.id.scrollView_decouverte).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.scrollView_decouverte).setVisibility(View.GONE);
                }
            }
        });

    }
    private void setData() {

        //Get Parameters from ROutesListActivity

        txt_route_title.setText(route.getTitle());
        //txt_ramp.setText(Util.formatDesnivel(route.getSlope()));
        // Log.d("Desnivel:", String.valueOf(route.getSlope()));
        // txt_duration.setText(Util.formatDuracion(route.getEstimatedTime()));
        txt_distance.setText(Util.formatDistanciaRoute(route.getRouteLengthMeters()));
        txt_duration.setText(route.timeToHoursMinutes(route.getEstimatedTime()));
        txt_description_body.setText(Html.fromHtml(route.getBody()).toString().trim(), TextView.BufferType.SPANNABLE);

        // Image
        if(route.getMainImage()!=null){
            main_image.setImageBitmap(route.getMainImage());
            //app.storeMainImage(route);
        } else {
            if (route.getMainImageURL() != null) {
                Log.d("MainImage", route.getMainImageURL());
                BitmapManager.INSTANCE.loadBitmap(route.getMainImageURL(),
                        main_image, 90, 90, route, app);
            } else {
                main_image.setImageResource(R.mipmap.ic_launcher);
            }
        }

        // DIFFICULT PICTURE

        /*switch (route.getDifficulty_tid()) {
            case "18":
                img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.dificultad_1));

                break;
            case "16":
                img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.dificultad_4));

                break;
            case "17":
                img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.dificultad_3));

                break;
            case "22":
                img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.dificultad_2));
                break;
        }*/
/*
        // STARS ICONS

        //img_stars.setVisibility(View.GONE);
        try {
            if (route.getVote().getValue() > 10 && route.getVote().getValue() < 30) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.estrella_icono_1));

            } else if (route.getVote().getValue() >= 30 && route.getVote().getValue() < 50) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.estrella_icono_2));

            } else if (route.getVote().getValue() >= 50 && route.getVote().getValue() < 70) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.estrella_icono_3));

            } else if (route.getVote().getValue() >= 70 && route.getVote().getValue() < 90) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.estrella_icono_4));

            } else if (route.getVote().getValue() >= 90) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.estrella_icono_5));
            }
        } catch (Exception e) {

        }*/
    }

    private void initMapView (Bundle savedInstanceState) {
        MapboxAccountManager.start(this, getString(R.string.access_token));
        PermissionsRequest.askForPermission(FeedRouteDetailsDecouverte.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionsRequest.askForPermission(FeedRouteDetailsDecouverte.this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapviewdetail);
        mapView.onCreate(savedInstanceState);
        // <-- Declarations -->
        final ArrayList<ResourcePoi> alPoi = route.getPois();
        final ArrayList<Route> alRoute = app.getRoutesList();
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();
        Log.d("Debug","route size poi = "+ route.getPois().size());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                FeedRouteDetailsDecouverte.this.prvMapBox = mapboxMap;
                // style of the map
                FeedRouteDetailsDecouverte.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteDetailsDecouverte.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);
                if (FeedRouteDetailsDecouverte.this.prvMapBox != null) {
                    FeedRouteDetailsDecouverte.this.prvMapBox.getUiSettings().setCompassEnabled(true);
                    FeedRouteDetailsDecouverte.this.prvMapBox.setMyLocationEnabled(true);
                }

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(FeedRouteDetailsDecouverte.this);
                Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi3x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi_depart3x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_start = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi13x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_1 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi23x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_2 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi33x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_3 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi43x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_4 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi53x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_5 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi63x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_6 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi73x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_7 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi83x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_8 = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(FeedRouteDetailsDecouverte.this, R.drawable.poi93x);
                iconDrawable = resize(iconDrawable);
                Icon icon_balise_9 = iconFactory.fromDrawable(iconDrawable);

                // if Poi not null
                if (alPoi != null) {
                    for (final ResourcePoi poi : alPoi) {
                        Log.d("Affichage PoiList : ", "  titre " + poi.getTitle() + "      lat " + poi.getLatitude() + "     ID " + poi.getNid());

                        final String poiNid = String.valueOf(poi.getNid());
                        // create poiPosition Lat/Lng
                        // Icon icon = determinateCategoryPoi(poi);
                        LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());
                        //Create marker from Poi
                        //getCategoryPoi(poi);
                        MarkerViewOptions marker = new MarkerViewOptions()
                                .position(poiPosition)
                                .title(poi.getTitle());
                        int poiNum = Integer.parseInt(poi.getTitle().substring(2,3));
                        switch (poiNum) {
                            case 1:
                                marker.icon(icon_balise_1);
                                break;
                            case 2:
                                marker.icon(icon_balise_2);
                                break;
                            case 3:
                                marker.icon(icon_balise_3);
                                break;
                            case 4:
                                marker.icon(icon_balise_4);
                                break;
                            case 5:
                                marker.icon(icon_balise_5);
                                break;
                            case 6:
                                marker.icon(icon_balise_6);
                                break;
                            case 7:
                                marker.icon(icon_balise_7);
                                break;
                            case 8:
                                marker.icon(icon_balise_8);
                                break;
                            case 9:
                                marker.icon(icon_balise_9);
                                break;
                            default:
                                marker.icon(icon_balise);
                                break;
                        }
                        Log.d("PierreLog : Marker bug ", "dans le poi individuel "+ marker.getTitle() + " lat " + marker.getPosition().getLatitude()+ " long "+ marker.getPosition().getLongitude());

                        // custom infoWindow
                        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Nullable
                            private LayoutInflater mInflater = (LayoutInflater) FeedRouteDetailsDecouverte.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                Location temp = new Location(LocationManager.GPS_PROVIDER);

                                temp.setLatitude(marker.getPosition().getLatitude());
                                temp.setLongitude(marker.getPosition().getLongitude());

                                View convertView = mInflater.inflate(R.layout.activity_feed_route_decouverte_popup, null);

                                ImageView poi_image = (ImageView)convertView.findViewById(R.id.poi_image);
                                final TextView poi_title = (TextView)convertView.findViewById(R.id.poi_title);
                                ImageView poi_close = (ImageView)convertView.findViewById(R.id.validation_close);
                                ImageView poi_more = (ImageView)convertView.findViewById(R.id.imageView2);
                                TextView poi_text = (TextView)convertView.findViewById(R.id.textView);

                                poi_title.setText(marker.getTitle());



                                if (mapboxMap.getMyLocation().distanceTo(temp) <= 40.0) { //TODO : en prod, changer pour <= 40.0 or >= -1.0 in debug
                                    poi_more.setVisibility(View.VISIBLE);
                                    poi_text.setVisibility(View.INVISIBLE);
                                    poi_more.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(FeedRouteDetailsDecouverte.this, POIDetailsActivity.class);
                                            intent.putExtra(POIDetailsActivity.PARAM_KEY_TITLE_POI, poi_title.getText());
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    poi_more.setVisibility(View.INVISIBLE);
                                    poi_text.setVisibility(View.VISIBLE);

                                }

                                return convertView;


                            }
                        });

                        /*mapboxMap.setOnInfoWindowClickListener(
                                new MapboxMap.OnInfoWindowClickListener() {
                                    @Override
                                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                                        Intent intent = new Intent(FeedRouteDetailsDecouverte.this, POIDetailsActivity.class);
                                        intent.putExtra(POIDetailsActivity.PARAM_KEY_TITLE_POI, marker.getTitle());
                                        mapboxMap.deselectMarker(marker);
                                        startActivity(intent);
                                        return false;
                                    }
                                }
                        );*/
                        /*Recup from FeedRouteBalise*/
                        /*mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                marker.hideInfoWindow();
                                return true;
                            }
                        });*/

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
                if (route.getTrack() != null) {
                    route_polyline = mapboxMap.addPolyline(WKTUtil.getPolylineFromWKTLineStringFieldFEED(route.getTrack()));
                    MarkerViewOptions marker_start = new MarkerViewOptions()
                            .position(WKTUtil.getFirstLatLngFromWKTLineStringFieldFEED(route.getTrack()))
                            .title("Depart : " + route.getTitle())
                            .icon(icon_balise_start);
                    mapboxMap.addMarker(marker_start);
                }

//                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
//                    @Override
//                    public void onMapClick(LatLng clickCoords) {
//                        List<LatLng> alpoint = route_polyline.getPoints();
//                        if(alpoint.contains(clickCoords)){
//
//                        }
//                    }
//                });


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

}