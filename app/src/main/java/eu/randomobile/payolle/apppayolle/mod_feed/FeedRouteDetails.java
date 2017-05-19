package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
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
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.Util;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedRouteDetails extends Activity {
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
    FrameLayout txt_depart;
    ImageView main_image;

    ImageButton btn_home;
    ImageButton btn_return;
    //private ImageView img_difficulty ;
    //private ImageView img_stars;

    boolean isEndNotified;
    ProgressBar progressBar;
    FloatingActionButton toggle_download;
    FloatingActionButton toggle_display_map;
    String paramNid;
    LatLngBounds latLngBounds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.app = (MainApp)  getApplication();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramNid = b.getString(PARAM_KEY_NID);
            for (Route route : app.getRoutesListCO()) {
                if (route.getNid().equals(paramNid)) {
                    this.route = route;
                    Log.d("JmLog", "Objet route : " + route.getTitle() + " " + route.getUrlMap() + " " + route.getImages() + " difficult? " + route.getDifficulty_tid());
                }
            }
        }
        setContentView(R.layout.activity_feed_route_details);
        initMapView(savedInstanceState);
        initComponents();
        escucharEventos();
        setData();
    }


    public void initComponents(){
        // <----------------->_TEXTVIEWS_DECLARATIONS_<----------------->

        txt_route_title = (TextView) findViewById(R.id.route_title);
//        txt_ramp = (TextView) findViewById(R.id.txt_ramp);
        txt_duration = (TextView) findViewById(R.id.route_duration);
        txt_distance = (TextView) findViewById(R.id.route_distance);
        txt_description_body = (TextView) findViewById(R.id.route_description);
        txt_depart = (FrameLayout) findViewById(R.id.btn_start);

        // <----------------->_IMAGEVIEWS_DECLARATIONS_<----------------->
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        main_image = (ImageView) findViewById(R.id.main_image);
        //img_difficulty = (ImageView) findViewById(R.id.route_difficulty);
        //img_stars = (ImageView) findViewById(R.id.stars_vote);


        toggle_download = (FloatingActionButton) findViewById(R.id.toggle_download);
        toggle_display_map = (FloatingActionButton) findViewById(R.id.toggle_display_map);
    }

    private void escucharEventos() {
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteDetails.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });


        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedRouteDetails.this.finish();
                    }
                });


        txt_depart.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteDetails.this, FeedRouteBalise.class);
                        intent.putExtra(PARAM_KEY_NID,route.getNid());
                        startActivity(intent);
                    }
                });

        toggle_download.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initMapOffline();
                    }
                });

        toggle_display_map.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.scrollView_depart).getVisibility() == View.GONE) {
                    findViewById(R.id.scrollView_depart).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.scrollView_depart).setVisibility(View.GONE);
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

        /*if (route.getDifficulty_tid().equals("18")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.dificultad_1));

        } else if (route.getDifficulty_tid().equals("16")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.dificultad_4));

        } else if (route.getDifficulty_tid().equals("17")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.dificultad_3));

        } else if (route.getDifficulty_tid().equals("22")) {
            img_difficulty.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.dificultad_2));
        }*/
/*
        // STARS ICONS

        //img_stars.setVisibility(View.GONE);
        try {
            if (route.getVote().getValue() > 10 && route.getVote().getValue() < 30) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.estrella_icono_1));

            } else if (route.getVote().getValue() >= 30 && route.getVote().getValue() < 50) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.estrella_icono_2));

            } else if (route.getVote().getValue() >= 50 && route.getVote().getValue() < 70) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.estrella_icono_3));

            } else if (route.getVote().getValue() >= 70 && route.getVote().getValue() < 90) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.estrella_icono_4));

            } else if (route.getVote().getValue() >= 90) {
                img_stars.setImageDrawable(ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.estrella_icono_5));
            }
        } catch (Exception e) {

        }*/
    }

    private void initMapView (Bundle savedInstanceState) {
        MapboxAccountManager.start(this, getString(R.string.access_token));
        PermissionsRequest.askForPermission(FeedRouteDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionsRequest.askForPermission(FeedRouteDetails.this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapviewdetail);
        mapView.onCreate(savedInstanceState);
        // <-- Declarations -->
        final ArrayList<ResourcePoi> alPoi = route.getPois();
        final ArrayList<Route> alRoute = app.getRoutesList();
        final ArrayList<LatLng> alLatLng = new ArrayList<LatLng>();
        Log.d("JmLog","Mapvi ? "+ mapView.isEnabled()+" "+mapView.getId());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                FeedRouteDetails.this.prvMapBox = mapboxMap;
                // style of the map
                FeedRouteDetails.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteDetails.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);
                if (FeedRouteDetails.this.prvMapBox != null) {
                    FeedRouteDetails.this.prvMapBox.getUiSettings().setCompassEnabled(true);
                    FeedRouteDetails.this.prvMapBox.setMyLocationEnabled(true);
                }
                IconFactory iconFactory = IconFactory.getInstance(FeedRouteDetails.this);
                Drawable iconDrawable = ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.parcours_0);
                iconDrawable = app.resize(iconDrawable);
                Icon icon_balise = iconFactory.fromDrawable(iconDrawable);
                Drawable iconDrawableStart = ContextCompat.getDrawable(FeedRouteDetails.this, R.drawable.poi_depart3x);
                iconDrawableStart = app.resize(iconDrawableStart);
                Icon icon_balise_start = iconFactory.fromDrawable(iconDrawableStart);
                if(route.getDifficulty_tid().equals("22")) {
                    mapboxMap.setMyLocationEnabled(false);
                }
                // if Poi not null
                if (alPoi != null) {
                    for (ResourcePoi poi : alPoi) {
                        // create poiPosition Lat/Lng
                        // Icon icon = determinateCategoryPoi(poi);
                        LatLng poiPosition = new LatLng(poi.getLatitude(), poi.getLongitude());

                        //Create marker from Poi
                        //getCategoryPoi(poi);
                        MarkerViewOptions marker = new MarkerViewOptions()
                                .position(poiPosition)
                                .title(poi.getTitle())
                                .icon(icon_balise);
                        if(Normalizer.normalize(poi.getTitle(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase().concat("      ").substring(0,6).equals("depart")) {
                            marker.icon(icon_balise_start);
                        }
                        //add Poi
                        alLatLng.add(poiPosition);
                        //add Marker
                        mapboxMap.addMarker(marker);

                    }
                    if(!alLatLng.isEmpty()) {
                        //Create LatLng Fit all Markers
                        latLngBounds = new LatLngBounds.Builder()
                                .includes(alLatLng)
                                .build();
                        // zoom Camera on all Markers
                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
                    }
                }
            }

        });
    }

    public void initMapOffline(){
        //<-- OFFLINE MAP -->//
        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(FeedRouteDetails.this);
        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                mapView.getStyleUrl(),
                latLngBounds,
                10,
                20,
                FeedRouteDetails.this.getResources().getDisplayMetrics().density);

        // Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title_region", route.getTitle());
            String json = jsonObject.toString();
            metadata = json.getBytes("UTF-8");
        } catch (Exception e) {
            Log.e("Log", "Failed to encode metadata: " + e.getMessage());
            metadata = null;
        }



        // Create the region asynchronously
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                // Display the download progress bar
                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                startProgress();

                // Monitor the download progress using setObserver
                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                    @Override
                    public void onStatusChanged(OfflineRegionStatus status) {

                        // Calculate the download percentage and update the progress bar
                        double percentage = status.getRequiredResourceCount() >= 0 ?
                                (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                0.0;

                        if (status.isComplete()) {
                            // Download complete
                            endProgress("Region downloaded successfully.");
                        } else if (status.isRequiredResourceCountPrecise()) {
                            // Switch to determinate state
                            setPercentage((int) Math.round(percentage));
                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {
                        // If an error occurs, print to logcat
                        Log.e("Log", "onError reason: " + error.getReason());
                        Log.e("Log", "onError message: " + error.getMessage());
                    }

                    @Override
                    public void mapboxTileCountLimitExceeded(long limit) {
                        // Notify if offline region exceeds maximum tile count
                        Log.e("Log", "Mapbox tile count limit exceeded: " + limit);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("Log", "Error: " + error);
            }
        });
    }
    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) return;

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(FeedRouteDetails.this, message, Toast.LENGTH_LONG).show();
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