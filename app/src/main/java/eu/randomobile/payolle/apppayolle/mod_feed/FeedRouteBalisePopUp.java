package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedRouteBalisePopUp extends Activity {
    private MapView mapView;
    private MapboxMap prvMapBox;
    FrameLayout check1, check2, check3;
    TextView code1, code2, code3;
    ImageView validation_ok, validation_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_feed_route_balise_pop_up);

        PermissionsRequest.askForPermission(FeedRouteBalisePopUp.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionsRequest.askForPermission(FeedRouteBalisePopUp.this, Manifest.permission.ACCESS_FINE_LOCATION);

        initMapView(savedInstanceState);
        capturarControles();
        escucharEventos();
    }

    private void capturarControles() {
        check1 = (FrameLayout)findViewById(R.id.check1);
        check2 = (FrameLayout)findViewById(R.id.check2);
        check3 = (FrameLayout)findViewById(R.id.check3);

        code1 = (TextView)findViewById(R.id.code1);
        code2 = (TextView)findViewById(R.id.code2);
        code3 = (TextView)findViewById(R.id.code3);


    }

    private void escucharEventos() {
        check1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FeedRouteBalisePopUp.this, ResultsActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void initMapView(Bundle savedInstanceState) {

        // Create a mapView
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // Customize map with markers, polylines, etc.
                mapboxMap.getMyLocationViewSettings().setBackgroundTintColor(Color.rgb(0,102,204));
                mapboxMap.getMyLocationViewSettings().setForegroundTintColor(Color.rgb(0,128,255));
                FeedRouteBalisePopUp.this.prvMapBox = mapboxMap;
                // style of the map
                FeedRouteBalisePopUp.this.prvMapBox.setStyleUrl(Style.OUTDOORS);
                FeedRouteBalisePopUp.this.prvMapBox.getUiSettings().setZoomControlsEnabled(true);



                // declare marker and its options
                MarkerViewOptions marker = new MarkerViewOptions()
                        .position(new LatLng(41.885, -87.679))
                        .title("Sydney Opera House")
                        .snippet("Bennelong Point, Sydney NSW 2000, Australia");

                // insert a marker
                mapboxMap.addMarker(marker);

                // centrer carte
//                LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                       .include(new LatLng(-77.348891, 47.405953)) // Northeast
//                        .include(new LatLng(-99.854003, 37.975967)) // Southwest
//                        .build();
//
//                mapboxMap.easeCamera(CameraUpdateFactory
//                       .newLatLngBounds(latLngBounds, 10), 10000);

                // display or hide info window, return true to display windows AND Toast
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                        return false;
                    }
                });




            }
        });


    }
}
