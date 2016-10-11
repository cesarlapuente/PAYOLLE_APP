package eu.randomobile.payolle.apppayolle.mod_feed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.Especie;
import eu.randomobile.payolle.apppayolle.mod_global.model.GeoPoint;
import eu.randomobile.payolle.apppayolle.mod_global.model.Page;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_global.model.Vote;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

public class FeedSplashActivity extends Activity {
    private MainApp app;
    private static boolean splashActivado = true;

    private static ProgressBar progressBar;

    // GPS coordinates to ordered elements by distance to user
    double lat = 0;
    double lon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home__activity_splash);
        Log.d("SplashActivity", "Enter on create");
        // Ask for necessary permission to use the application
        PermissionsRequest.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
        }
        PermissionsRequest.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
        }

        this.app = (MainApp) getApplication();

        // Intitialize Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.splash_prograssBar);
        progressBar.setMax(100);


        // Check if connection available
        if (app.getNetStatus() != 0) {
            updateLocalDatabase();
        } else {
            // Show Alert
            continueWithoutConnectionAlert();

            // Get datas from local database
            getLocalDatas(app);

        }

        // Launch MainActivity
        if(progressBar.getProgress()==100){
            Log.d("SplashActivity", "Load main activity");
            loadMainActivity();
        }
    }

    protected void onResume() {
        super.onResume();

        splashActivado = true;
    }

    public void onBackPressed() {
        splashActivado = false;

        super.onBackPressed();
    }

    private void getLocalDatas(MainApp app) {
        // load routes
        try {
            app.setRoutesList(app.getDBHandler().getRouteList());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Updating routes failed", Toast.LENGTH_LONG).show();
        }

        // load POIs
//        try {
//            app.setPoisList(app.getDBHandler().getPoisList());
//
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Updating POIs failed", Toast.LENGTH_LONG).show();
//        }

        // load species
//        try {
//            app.setEspecies(app.getDBHandler().getEspeciesList());
//
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Updating species failed", Toast.LENGTH_LONG).show();
//        }
    }

    private void continueWithoutConnectionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedSplashActivity.this);

        builder.setTitle(R.string.txt_sin_conexion);
        builder.setMessage(R.string.txt_caracteristicas_no_disponibles);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMainActivity();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadMainActivity();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loadMainActivity();
            }
        });

        builder.show();
    }

    private void updateLocalDatabase() {
        Log.d("SplashActivity", "Enter updateLocalDatabase");
        updateLocalDatabase_Routes();
        updateLocalDatabase_Pois();
//        updateLocalDatabase_Especies();
//        updateLocalDatabase_Pages();
    }

    private void updateLocalDatabase_Routes() {
        Log.d("SplashActivity", "Enter updateLocalDatabase_Routes");
        loadRoutesListSortByDistance(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N?mero de elementos por p?gina
                0, // P?gina
                null, // Tid de la categor?a que queremos filtrar
                null, // Dificultad
                null // Texto a buscar
        );
        progressBar.incrementProgressBy(50);
    }

    private void updateLocalDatabase_Pois() {
        loadPoisListSortByDistance(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N?mero de elementos por p?gina
                0, // P?gina
                null, // Tid de la categor?a que queremos filtrar
                null // Texto a buscar
        );
        progressBar.incrementProgressBy(50);
    }

    private void updateLocalDatabase_Especies() {
        AsyncHttpClient client_especies = new AsyncHttpClient();
        client_especies.get(FeedSplashActivity.this, "http://sitedepayolle.randomobile.eu/api/routedata/route/retrieve.json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);

                Gson gson = new Gson();
                Especie[] especiesArray = gson.fromJson(s, Especie[].class);

                ArrayList<Especie> especies = new ArrayList<Especie>(Arrays.asList(especiesArray));

                for (Especie especie : especies) {
                    Log.d("updateLocalDatabase():", " Especie Nid: " + especie.getNid());
                    Log.d("updateLocalDatabase():", " Especie Title: " + especie.getTitle());
                    Log.d("updateLocalDatabase():", " Especie Description: " + especie.getDescription());
                    Log.d("updateLocalDatabase():", " Especie Body: " + especie.getBody());

                    app.getDBHandler().addOrReplaceEspecie(especie);
                }

                app.setEspecies(app.getDBHandler().getEspeciesList());

                progressBar.incrementProgressBy(25);
            }

            @Override
            public void onFailure(Throwable throwable, String s) {
                super.onFailure(throwable, s);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void updateLocalDatabase_Pages() {
        try {
            AsyncHttpClient client_pages = new AsyncHttpClient();

            client_pages.get(FeedSplashActivity.this, "http://sitedepayolle.randomobile.eu/api/routedata/pages/retrieve.json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String string) {
                    Log.d("SplashActivity", "update Pages success");
                    super.onSuccess(string);

                    Gson gson = new Gson();
                    Page[] list_Pages = gson.fromJson(string, Page[].class);

                    ArrayList<Page> pages = new ArrayList<Page>(Arrays.asList(list_Pages));

                    for (Page page : pages) {
                        Log.d("updateLocalDatabase():", " Page Nid: " + page.getNid());
                        Log.d("updateLocalDatabase():", " Page Title: " + page.getTitle());
                        Log.d("updateLocalDatabase():", " Page Body: " + page.getBody());

                        app.getDBHandler().addOrReplacePage(page);
                    }

                    progressBar.incrementProgressBy(25);
                }

                @Override
                public void onFailure(Throwable throwable, String s) {
                    Log.d("SplashActivity", "update Pages failure");
                    super.onFailure(throwable, s);
                }

                @Override
                public void onFinish() {
                    Log.d("SplashActivity", "update Pages finish");
                    super.onFinish();
                }
            });

        } catch (Exception e) {
            Log.d("SplashActivity", "update Pages catch");
            Toast.makeText(getApplicationContext(), "Ha fallado la descarga de datos P3", Toast.LENGTH_LONG).show();
        }
    }

    private static void loadRoutesListSortByDistance(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String difTid, String searchTxt) {
        Log.d("SplashActivity", "Enter loadRoutesListSortByDistance");
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        if (radio > 0) {
            params.put("radio", String.valueOf(radio));
        }
        if (num > 0) {
            params.put("num", String.valueOf(num));
        }
        if (pag > 0) {
            params.put("pag", String.valueOf(pag));
        }
        if (catTid != null && !catTid.equals("")) {
            params.put("cat", catTid);
        }
        if (difTid != null && !difTid.equals("")) {
            params.put("difficulty", difTid);
        }
        if (searchTxt != null && !searchTxt.equals("")) {
            params.put("search", searchTxt);
        }

        final MainApp app = (MainApp) application;

        app.clienteDrupal.customMethodCallPost("route/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {

                        ArrayList<Route> RoutesList = null;

                        Log.d("ROUTE: ", response);

                        if (response != null && !response.equals("")) {
                            Log.d("SplashActivity", "Response not null");
                            RoutesList = fillRouteList(response, application);
                        }

                        if (RoutesList != null) {
                            Log.d("SplashActivity", "Route List not null");
                            app.setRoutesList(RoutesList);

                            for (Route route : app.getRoutesList()) {
                                Log.d("ForEach route sais:", " Route ID: " + route.getNid());
                                Log.d("ForEach route sais:", " Route Name: " + route.getTitle());

                                app.getDBHandler().addOrReplaceRoute(route);

                            }

                            app.setRoutesList(app.getDBHandler().getRouteList());

                        } else {
                            app.setRoutesList(new ArrayList<Route>());
                        }
                    }

                    public void onFailure(Throwable error) {
                        Log.d("JmLog","FAIL connection loadRoutesListSortByDistance");
                    }
                },
                params);
    }

    private static void loadPoisListSortByDistance(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String searchTxt) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));

        if (radio > 0) {
            params.put("radio", String.valueOf(radio));
        }
        if (num > 0) {
            params.put("num", String.valueOf(num));
        }
        if (pag > 0) {
            params.put("pag", String.valueOf(pag));
        }
        if (catTid != null && !catTid.equals("")) {
            params.put("cat", catTid);
        }
        if (searchTxt != null && !searchTxt.equals("")) {
            params.put("search", searchTxt);
        }

        final MainApp app = (MainApp) application;

        app.clienteDrupal.customMethodCallPost("poi/get_list_distance", new AsyncHttpResponseHandler() {
                    public void onSuccess(String response) {
                        ArrayList<Poi> listaPois = null;

                        if (response != null && !response.equals("")) {
                            listaPois = fillPoiList(response, application);
                        }

                        if (listaPois != null) {
                            app.setPoisList(listaPois);

                            for (Poi poi : listaPois) {
                                Log.d("ForEach poi sais:", " POI ID: " + poi.getNid());
                                Log.d("ForEach poi sais:", " POI Name: " + poi.getTitle());
                                Log.d("ForEach poi sais:", " POI Body: " + poi.getBody());

//                                app.getDBHandler().addOrReplacePoi(poiDetail);
                            }

//                            app.setPoisList(app.getDBHandler().getPoiList());

                        } else {
                            app.setPoisList(new ArrayList<Poi>());
                        }
                    }

                    public void onFailure(Throwable error) {
                        Log.d("JmLog","FAIL connection loadPoisListSortByDistance");
                    }
                },
                params);
    }

    private static ArrayList<Route> fillRouteList(String response, Application application) {
        Log.d("SplashActivity", "enter fillRouteList");
        Context ctx = application.getApplicationContext();

        ArrayList<Route> RoutesList = null;

        //GR and PR : cathegory names
        int count_GR = 0;
        int count_PR = 0;

        try {
            Log.d("SplashActivity", "try");
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                Log.d("SplashActivity", "arrayRes not null");
                RoutesList = new ArrayList<Route>();

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);
                    Log.d("SplashActivity", "recObj = "+ recObj.toString());
                    if (recObj != null) {
                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject recDic = (JSONObject) recObj;
                            Log.d("JSON Object:", " String: " + recDic.toString());

                            String nid = recDic.getString("nid");
                            String title = recDic.getString("title");
                            String body = recDic.getString("body");

                            Log.d("fillRouteList() sais:", " BODY: " + body);

                            String distance = recDic.getString("distance");
                            String image = recDic.getString("image");
                            String geomWKT = recDic.getString("geom");
                            String url = recDic.getString("map_tpk");

                            Log.d("fillRouteList() sais:", "URL tpk: " + url);

                            Route item = new Route();

                            item.setNid(nid);
                            item.setTitle(title);
                            item.setBody(body);
                            item.setTrack(geomWKT);
                            item.setUrlMap(url);

                            Log.d("fillRouteList() sais:", "URL en objeto Ruta: " + item.getUrlMap());

                            double distanceKMDouble = Double.valueOf(distance);
                            double distanceMDouble = distanceKMDouble * 1000;

                            item.setDistanceMeters(distanceMDouble);

                            if (!recDic.getString("time").equals("null"))
                                item.setEstimatedTime(recDic.getDouble("time"));
                            if (!recDic.getString("routedistance").equals("null"))
                                item.setRouteLengthMeters(recDic.getDouble("routedistance"));

                            Object objCat = recDic.get("cat");
                            if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicCat = (JSONObject) objCat;
                                String tid = dicCat.getString("tid");
                                String name = dicCat.getString("name");
                                RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
                                routeCatTerm.setTid(tid);
                                routeCatTerm.setName(name);
                                item.setCategory(routeCatTerm);
                                if (item.getCategory().getName().equals("PR")) {
                                    int n = count_PR % 3;
                                    switch (n) {
                                        case 0:
                                            item.setColor(ctx.getResources().getColor(R.color.pr1_route));
                                            break;
                                        case 1:
                                            item.setColor(ctx.getResources().getColor(R.color.pr2_route));
                                            break;
                                        case 2:
                                            item.setColor(ctx.getResources().getColor(R.color.pr3_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    count_PR++;
                                } else if (item.getCategory().getName().equals("GR")) {
                                    int n = count_GR % 2;
                                    switch (n) {
                                        case 0:
                                            item.setColor(ctx.getResources().getColor(R.color.gr1_route));
                                            break;
                                        case 1:
                                            item.setColor(ctx.getResources().getColor(R.color.gr2_route));
                                            break;
                                        default:
                                            break;
                                    }
                                    count_GR++;
                                } else if (item.getCategory().getName().equals("CR")) {
                                    item.setColor(ctx.getResources().getColor(R.color.cr1_route));
                                }
                            }

                            Object objRate = recDic.get("rate");
                            if (objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicRate = (JSONObject) objRate;
                                String numVotosStr = dicRate.getString("count");
                                String resultsAvgStr = dicRate.getString("results");
                                int numVotos = 0;
                                int resultsAvg = 0;
                                if (numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")) {
                                    numVotos = (int) Float.parseFloat(numVotosStr);
                                }
                                if (resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")) {
                                    resultsAvg = (int) Float.parseFloat(resultsAvgStr);
                                }
                                Vote vote = new Vote();
                                vote.setEntity_id(nid);
                                vote.setNumVotes(numVotos);
                                vote.setValue(resultsAvg);
                                item.setVote(vote);
                            }

                            if (image != null && (image.equals("") || image.equals("null"))) {
                                item.setMainImage(null);
                            } else {
                                item.setMainImage(image);
                            }

                            if (!recDic.getString("difficulty").equals("null"))
                                item.setDifficulty_tid(recDic.getString("difficulty"));

                            try {
                                JSONArray arrayPois = recDic.getJSONArray("pois");
                                if (arrayPois != null) {
                                    ArrayList<ResourcePoi> arrayTemp = new ArrayList<>();

                                    for (int j = 0; j < arrayPois.length(); j++) {
                                        JSONObject objPOI = (JSONObject) arrayPois.get(j);

                                        String poi_nid = objPOI.getString("nid");
                                        String poi_title = objPOI.getString("title");

                                        Log.d("--------------------", " ------------------------------------------------------------ ");

                                        Log.d("Obj poi:", " Nid " + poi_nid);
                                        Log.d("Obj poi:", " Title " + poi_title);

                                        ResourcePoi poi = new ResourcePoi();
                                        poi.setNid(Integer.parseInt(poi_nid));
                                        poi.setTitle(poi_title);

                                        arrayTemp.add(poi);
                                    }

                                    item.setPois(arrayTemp);
                                }

                            } catch (Exception e) {
                                Log.d("--------------------", " ---------------------------v-ERROR-v------------------------------- ");
                                e.printStackTrace();
                                Log.d("--------------------", " ---------------------------^-ERROR-^------------------------------- ");
                            }


                            RoutesList.add(item);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.d("SplashActivity", "RoutesList = null");
            RoutesList = null;
        }

        return RoutesList;
    }

    private static ArrayList<Poi> fillPoiList(String response, Application application) {
        Context ctx = application.getApplicationContext();

        ArrayList<Poi> listaPois = null;

        try {
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                if (arrayRes.length() > 0) {
                    listaPois = new ArrayList<Poi>();
                }

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);
                    if (recObj != null) {
                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject recDic = (JSONObject) recObj;

                            String nid = recDic.getString("nid");
                            String title = recDic.getString("title");
                            String body = recDic.getString("body");
                            String lat = recDic.getString("lat");
                            String lon = recDic.getString("lon");
                            String alt = recDic.getString("altitude");
                            String distance = recDic.getString("distance");
                            String image = recDic.getString("image");

                            Poi item = new Poi();

                            item.setNid(nid);
                            item.setTitle(title);
                            item.setBody(body);

                            double distanceKMDouble = Double.valueOf(distance);
                            double distanceMDouble = distanceKMDouble * 1000;
                            item.setDistanceMeters(distanceMDouble);

                            Object objCat = recDic.get("cat");

                            if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicCat = (JSONObject) objCat;
                                String tid = dicCat.getString("tid");
                                String name = dicCat.getString("name");
                                String icon = dicCat.getString("image");
                                PoiCategoryTerm poiCatTerm = new PoiCategoryTerm();
                                poiCatTerm.setTid(tid);
                                poiCatTerm.setName(name);
                                poiCatTerm.setIcon(icon);
                                item.setCategory(poiCatTerm);
                            }

                            Object objRate = recDic.get("rate");

                            if (objRate != null && objRate.getClass().getName().equals(JSONObject.class.getName())) {
                                JSONObject dicRate = (JSONObject) objRate;
                                String numVotosStr = dicRate.getString("count");
                                String resultsAvgStr = dicRate.getString("results");
                                int numVotos = 0;
                                int resultsAvg = 0;
                                if (numVotosStr != null && !numVotosStr.equals("") && !numVotosStr.equals("null")) {
                                    numVotos = (int) Float.parseFloat(numVotosStr);
                                }
                                if (resultsAvgStr != null && !resultsAvgStr.equals("") && !resultsAvgStr.equals("null")) {
                                    resultsAvg = (int) Float.parseFloat(resultsAvgStr);
                                }
                                Vote vote = new Vote();
                                vote.setEntity_id(nid);
                                vote.setNumVotes(numVotos);
                                vote.setValue(resultsAvg);
                                item.setVote(vote);
                            }

                            if (image != null && (image.equals("") || image.equals("null"))) {
                                item.setMainImage(null);

                            } else {
                                item.setMainImage(image);
                            }

                            GeoPoint gp = new GeoPoint();

                            if (lat != null && !lat.equals("") && !lat.equals("null")) {
                                gp.setLatitude(Double.parseDouble(lat));
                            }

                            if (lon != null && !lon.equals("") && !lon.equals("null")) {
                                gp.setLongitude(Double.parseDouble(lon));
                            }

                            if (alt != null && !alt.equals("") && !alt.equals("null")) {
                                gp.setAltitude(Double.parseDouble(alt));
                            }
                            item.setCoordinates(gp);

                            listaPois.add(item);
                        }
                    }
                }
            }

        } catch (Exception e) {
            listaPois = null;
        }

        return listaPois;
    }

    public void loadMainActivity() {

        Intent intent = new Intent(FeedSplashActivity.this, HomeActivity.class);
        startActivity(intent);
    }

}
