package eu.randomobile.payolle.apppayolle.mod_home;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_feed.FeedInfo;
import eu.randomobile.payolle.apppayolle.mod_feed.HomeActivity;
import eu.randomobile.payolle.apppayolle.mod_global.environment.DataConection;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.GeoPoint;
import eu.randomobile.payolle.apppayolle.mod_global.model.Page;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourceFile;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourceLink;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_global.model.Vote;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.PoiCategoryTerm;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.RouteDifficultyTerm;
import eu.randomobile.payolle.apppayolle.utils.JSONManager;
import eu.randomobile.payolle.apppayolle.utils.PermissionsRequest;

import static android.text.Html.fromHtml;

public class SplashActivity extends Activity {
    private MainApp app;

    private static ProgressBar progressBar;
    private Route routeItem ;
    private Poi poiItem  ;

    private static final int SPLASH_TIME = 3 * 2000; // seconds //TODO change for full download
    private static boolean splashActivado = true;

    // Coordenadas GPS de Fuerteventura
    double lat = -14.2789;
    double lon = 28.1958;

    private static int loadedComponents = 0;
    //
    //

    ArrayList<FeedInfo> alListeDE = new ArrayList<>();
    ArrayList<FeedInfo> alListeCO = new ArrayList<>();
    ArrayList<FeedInfo> alListeLI = new ArrayList<>();

    ArrayList<FeedInfo> listInfo = new ArrayList<>();

    public class MyProgressBar extends ProgressBar {
        SplashActivity splashActivity;

        public MyProgressBar(Context context, SplashActivity splashActivity) {
            super(context);

            this.splashActivity = splashActivity;
        }

        @Override
        public void setProgress(int progress)
        {
            super.setProgress(progress);
            if(progress == this.getMax())
            {
                splashActivity.loadMainActivity();
            }
        }
    }

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_home__activity_splash);

        PermissionsRequest.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
        }
        PermissionsRequest.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
        }

        this.app = (MainApp) getApplication();


        progressBar = (ProgressBar) findViewById(R.id.splash_prograssBar);
        progressBar.setMax(100);

        PermissionsRequest.askForPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionsRequest.askForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (app.getNetStatus() != 0) {

            feedInfo();


        } else {

            new backgroundLoadOffline().execute();
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

            builder.setTitle(R.string.txt_sin_conexion);
            builder.setMessage(R.string.txt_caracteristicas_no_disponibles);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });

            builder.show();

        }
    }

    public class backgroundLoadOffline extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            updateDataOffline();
            return null;
        }

    }

    public void updateDataOffline(){ //TODO in a new thread

        //app.setRoutesListCO(app.getDBHandler().getRouteListByCateg("CO"));
        try {
            progressBar.incrementProgressBy(25);
            app.setRoutesListCO(app.getDBHandler().getRouteListByCateg("CO"));
            progressBar.incrementProgressBy(25);
            loadedComponents = 1;
            //app.setRoutesList(app.getDBHandler().getRouteList());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Se ha producido un error al actualizar las rutas", Toast.LENGTH_LONG).show();
        }
        try {
            app.setPoisList(app.getDBHandler().getPoiList());
            progressBar.incrementProgressBy(25);
            loadedComponents = 2;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Se ha producido un error al actualizar los pois", Toast.LENGTH_LONG).show();
        }
        app.setPoisList(app.getDBHandler().getPoiList());
        try {
            app.setRoutesListDE(app.getDBHandler().getRouteListByCateg("Decouverte"));
            progressBar.incrementProgressBy(25);
            loadedComponents = 3;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Se ha producido un error al actualizar las rutas decouverte", Toast.LENGTH_LONG).show();
        }

        loadedComponents = 4;
        loadMainActivity();
    }

    public void feedInfo(){
        app.clienteDrupal.customMethodCallPost("info/get_list", new AsyncHttpResponseHandler() { //TODO fix first launch bug nullPointerException
                    public void onSuccess(String response) {
                        Log.d("JmmLog","response :="+response);

                        if (response != null && !response.equals("")) {

                            try {
                                JSONArray arrayRes = new JSONArray(response);
                                for (int i = 0; i < arrayRes.length(); i++) {

                                    Object recObj = arrayRes.get(i);
                                    if (recObj != null) {
                                        if (recObj.getClass().getName().equals(JSONObject.class.getName())) {
                                            JSONObject recDic = (JSONObject) recObj;
                                            String nid = recDic.getString("nid");
                                            String title = recDic.getString("title");
                                            String body = fromHtml(recDic.getString("body")).toString();
                                            String cat = determinateCat(recDic.getString("cat"));
                                            FeedInfo item = new FeedInfo(nid, title, body,cat);


                                            if(item.getCat().equals("Decouverte")){
                                                alListeDE.add(item);
                                            }else if(item.getCat().equals("CO")){
                                                alListeCO.add(item);
                                            }else if(item.getCat().equals("Lire")){
                                                alListeLI.add(item);
                                            }
                                        }
                                    }
                                }


                            } catch (Exception e) {
                                Log.d("JmmLog", "Excepcion en infos: " + e.getMessage());
                            }
                        }


                        Log.d("JmmLog", "size listInfo: " + listInfo.size());

                    }

                    public void onFailure(Throwable error) {
                        Log.d("JmmLog", "Excepcion en infos: " + error.getMessage());
                    }

                    @Override
                    public void onFinish(){
                        app.setInfoListCO(alListeCO);
                        app.setInfoListDE(alListeDE);
                        app.setInfoListLI(alListeLI);
                        updateLocalDatabase_Routes();
                    }
                },
                null);


    }
    public String determinateCat(String cat){
        // several categ : Decouverte or CO or Lire
        switch (cat) {
            case "51":
                return "CO";
            case "52":
                return "Decouverte";
            case "53":
                return "Lire";
            case "default":
                return "INDETERMINATED";
        }
        return "";
    }
    protected void onResume() {
        super.onResume();

        splashActivado = true;
        /*
        try {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (splashActivado) {
                        // Abrir el home tras unos instantes
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                        SplashActivity.this.finish();

                        splashActivado = false;
                    }
                }
            }, SPLASH_TIME);

        } catch (Exception e) {

        }
        */
    }

    public void onBackPressed() {
        splashActivado = false;

        super.onBackPressed();
    }

    //

    //

    private void updateLocalDatabase_Routes() {
        cargarListaRutasOrdenadosDistancia(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N?mero de elementos por p?gina
                0, // P?gina
                null, // Tid de la categor?a que queremos filtrar
                null, // Dificultad
                null // Texto a buscar
        );

        updateLocalDatabase_Pois();
    }

    private  void cargarListaRutasOrdenadosDistancia(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String difTid, String searchTxt) {
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

                        ArrayList<Route> listaRutas = null;
                        ArrayList<Route> route_CO  = new ArrayList<Route>();
                        ArrayList<Route> route_DE = new ArrayList<Route>();

                        Log.d("ROUTE: ", response);

                        if (response != null && !response.equals("")) {
                            listaRutas = fillRouteList(response, application);
                        }

                        if (listaRutas != null) {
                            //app.setRoutesList(listaRutas);


                            for (Route route :listaRutas) {
                                Log.d("ForEach route sais:", " Route ID: " + route.getNid());
                                Log.d("ForEach route sais:", " Route Name: " + route.getTitle());
                                if(route.getCategory().getName().equals("CO")){
                                    route_CO.add(route);
                                }else if (route.getCategory().getName().equals("Decouverte")) {
                                    route_DE.add(route);
                                }

                                /*
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("nid", route.getNid());

                                app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
                                    public void onSuccess(String response) {
                                        if (response != null && !response.equals("")) {

                                            Route route = fillRoute(response);

                                            app.getDBHandler().addOrReplaceRoute(route);

                                        } else {
                                            Log.d("DetailedRoute:"," ERROR al descargar la ruta detallada.");
                                        }
                                    }

                                    public void onFailure(Throwable error) {
                                        Log.d("cargarRoute():", error.toString());
                                    }
                                }, params);
                                */

                                app.getDBHandler().addOrReplaceRoute(route);

                            }
                            app.setRoutesListCO(route_CO);
                            app.setRoutesListDE(route_DE);
                            progressBar.incrementProgressBy(25);

                            //if (!app.isNetworkAvailable())
                            //    app.setRoutesList(app.getDBHandler().getRouteList());

                            loadedComponents++;

                        } else {
                            app.setRoutesList(new ArrayList<Route>());
                        }
                    }

                    public void onFailure(Throwable error) {
                        Log.d("JmLog", "FAIL connection cargarListaRutasOrdenadosDistancia");
                    }
                },
                params);
    }


    private   ArrayList<Route> fillRouteList(String response, Application application) {
        Context ctx = application.getApplicationContext();

        ArrayList<Route> listaRutas = null;

        int count_GR = 0;
        int count_PR = 0;

        try {
            JSONArray arrayRes = new JSONArray(response);
            if (arrayRes != null) {
                listaRutas = new ArrayList<Route>();

                for (int i = 0; i < arrayRes.length(); i++) {
                    Object recObj = arrayRes.get(i);

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

//                                if (item.getCategory().getName().equals("PR")) {
//                                    int n = count_PR % 3;
//                                    switch (n) {
//                                        case 0:
//                                            item.setColor(ctx.getResources().getColor(R.color.pr1_route));
//                                            break;
//                                        case 1:
//                                            item.setColor(ctx.getResources().getColor(R.color.pr2_route));
//                                            break;
//                                        case 2:
//                                            item.setColor(ctx.getResources().getColor(R.color.pr3_route));
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    count_PR++;
//                                } else if (item.getCategory().getName().equals("GR")) {
//                                    int n = count_GR % 2;
//                                    switch (n) {
//                                        case 0:
//                                            item.setColor(ctx.getResources().getColor(R.color.gr1_route));
//                                            break;
//                                        case 1:
//                                            item.setColor(ctx.getResources().getColor(R.color.gr2_route));
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    count_GR++;
//                                } else if (item.getCategory().getName().equals("CR")) {
//                                    item.setColor(ctx.getResources().getColor(R.color.cr1_route));
//                                }
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
                                item.setMainImageURL(null);
                            } else {
                                item.setMainImageURL(image);
                            }

                            if (!recDic.getString("difficulty").equals("null"))
                                item.setDifficulty_tid(recDic.getString("difficulty"));
                            handleImages(item,null);

                            //"pois":[{"title":"Cuevas","body":null,"lat":"28.403591432499","lon":"-14.155712170242","number":1,"type":"30","nid":"31","languageNone":"und","language":"es"},

                            /*
                            String pois = recDic.getString("pois");
                            Log.d("####################", " ############################################################ ");
                            Log.d("JSON Obj POIS:", "  " + pois);
                            */

                            try {
                                JSONArray arrayPois = recDic.getJSONArray("pois");
                                if (arrayPois != null) {
                                    ArrayList<ResourcePoi> arrayTemp = new ArrayList<>();
                                    ArrayList<Boolean> game = new ArrayList<Boolean>();

                                    for (int j = 0; j < arrayPois.length(); j++) {
                                        JSONObject objPOI = (JSONObject) arrayPois.get(j);

                                        String poi_nid = objPOI.getString("nid");
                                        String poi_title = objPOI.getString("title");
                                        double longitudePoi = objPOI.getDouble("lon");
                                        double latitudePoi = objPOI.getDouble("lat");
                                        String poi_code1 = objPOI.getString("code1");

                                        Log.d("--------------------", " ------------------------------------------------------------ ");

                                        Log.d("Obj poi:", " Nid " + poi_nid);
                                        Log.d("Obj poi:", " Title " + poi_title);
                                        Log.d("Obj poi:", " Code 1 " + poi_code1);

                                        ResourcePoi poi = new ResourcePoi();
                                        poi.setNid(Integer.parseInt(poi_nid));
                                        poi.setTitle(poi_title);
                                        poi.setLatitude(latitudePoi);
                                        poi.setLongitude(longitudePoi);
                                        poi.setCode1(poi_code1);
                                        //poi.setMainImageURL(poi_image);

                                        /*************




                                        *********/

                                        arrayTemp.add(poi);
                                        game.add(Boolean.FALSE);

                                        Log.i("JmLog", "resourcePoi Route title =>" + poi.getTitle());

                                        /*
                                        Log.d("--------------------", " ------------------------------------------------------------ ");
                                        Log.d("JSON Obj POIS:", "  " + arrayPois.get(j).toString());
                                        Log.d("--------------------", " ------------------------------------------------------------ ");
                                        */
                                    }
                                    item.setPois(arrayTemp);

                                    /*Game part*/
                                    game.remove(game.size()-1);
                                    item.setNumberPoiGameOk(game);

                                }

                            } catch (Exception e) {
                                Log.d("--------------------", " ---------------------------v-ERROR-v------------------------------- ");
                                e.printStackTrace();
                                Log.d("--------------------", " ---------------------------^-ERROR-^------------------------------- ");
                            }

                            listaRutas.add(item);

                        }
                    }
                }
            }

        } catch (Exception e) {
            listaRutas = null;
        }
        return listaRutas;
    }

    private void handleImages(final Route routeItem,final Poi poiItem){
        if (DataConection.hayConexion(SplashActivity.this)) {
            try {

                String url = "";
                Log.d("Images loading", "connexion");
                HashMap<String, String> params = new HashMap<String, String>();

                if (routeItem != null) {
                    url = "route/get_item";
                    params.put("nid", routeItem.getNid());
                } else if (poiItem != null ) {
                    url = "poi/get_item";
                    params.put("nid", poiItem.getNid());
                }

                app.clienteDrupal.customMethodCallPost(url,
                        new AsyncHttpResponseHandler() {
                            public void onSuccess(String response) {
                                Log.d("Images loading", "success");

                                if (response != null && !response.equals("")) {
                                    Log.d("Images loading", "response not null");
                                    ArrayList<ResourceFile> arrayResourceImages = new ArrayList<ResourceFile>();
                                    try {
                                        Log.d("Images loading", "try");
                                        JSONObject dicRes = new JSONObject(response);
                                        Object objImages = dicRes.get("images");


                                        if (objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())) {
                                            Log.d("Images loading", "images not null");
                                            JSONArray array = (JSONArray) objImages;
                                            for (int i = 0; i < array.length(); i++) {
                                                Log.d("Images loading", "image " + i);
                                                Object obj = array.get(i);
                                                if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                                                    JSONObject dic = (JSONObject) obj;
                                                    String name = dic.getString("name");
                                                    String url = dic.getString("url");
                                                    Log.d("JmLog", "l'url est :" + url);
                                                    ResourceFile rf = new ResourceFile();
                                                    rf.setFileName(name);
                                                    rf.setFileUrl(url);
                                                    //Log.d("Jmlog", "L'objet images : " + dicRes.get("images"));
                                                    rf.setFileTitle(JSONManager.getString(dic, "title"));
                                                    rf.setFileBody(JSONManager.getString(dic, "body"));
                                                    rf.setCopyright(JSONManager.getString(dic, "copyright"));
                                                    arrayResourceImages.add(rf);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.d("Images loading", "exeption");
                                    }
                                    Log.d("Images loading", "nb images : " + arrayResourceImages.size());

                                    //       Images gallery loading
                                    if (routeItem != null ) {
                                        routeItem.setImages(arrayResourceImages);

                                    } else if (poiItem != null) {
                                        poiItem.setImages(arrayResourceImages);
                                        Log.d("Images poi not null", "nb images : " + arrayResourceImages.size());
                                    }


                                }
                            }

                            public void onFailure(Throwable error) {
                                Log.d("Images loading", "failure --> " + error);
                                // Informar al delegate
//                            if (Route.routesInterface != null) {
//                                Log.d("Milog",
//                                        "Antes de informar al delegate de un error: " + error.toString());
//                                Route.routesInterface
//                                        .producidoErrorAlCargarRoute(error
//                                                .toString());
//                            }
                            }
                        }, params);
            }catch(Exception e){
                e.printStackTrace();
                Log.d("JmLog","message exception = "+ e.getMessage());
            }
        }
        // ImgArray = route.getImages();
    }

    private static void cargarRoute(Application application, String nid) {
        MainApp app = (MainApp) application;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nid", nid);

        app.clienteDrupal.customMethodCallPost("route/get_item", new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Log.d("Milog", "Exito al cargar route: " + response);

                if (response != null && !response.equals("")) {
                    Route route = fillRoute(response);
                    // Informar al delegate
                    if (Route.routesInterface != null) {
                        if (route != null) {
                            //Route.routesInterface.seCargoRoute(route);
                        } else {
                            Log.d("Milog", "Antes de informar al delegate de un error");
                            //Route.routesInterface.producidoErrorAlCargarRoute("Error al cargar route");
                        }
                    }
                }
            }

            public void onFailure(Throwable error) {
                Log.d("cargarRoute():", error.toString());
            }
        }, params);

    }

    private static Route fillRoute(String response) {
        Route route = null;
        try {
            JSONObject dicRes = new JSONObject(response);
            if (dicRes != null) {
                String nid = dicRes.getString("nid");
                String title = dicRes.getString("title");
                String body = dicRes.getString("body");

                Log.d("fillRoute() sais:", " BODY: " + body);

                String geom = dicRes.getString("geom");
                String url_map = dicRes.getString("map");

                route = new Route();
                route.setNid(nid);
                route.setTitle(title);
                route.setBody(body);

                route.setUrlMap(url_map);
                if (!dicRes.getString("time").equals("null"))
                    route.setEstimatedTime(dicRes.getDouble("time"));

                if (!dicRes.getString("distance").equals("null"))
                    route.setRouteLengthMeters(dicRes.getDouble("distance"));

                if (!(dicRes.getString("alt_max").equals("null")) && !(dicRes.getString("alt_min").equals("null")))
                    route.setSlope(dicRes.getDouble("alt_max") - dicRes.getDouble("alt_min"));

                Object objCat = dicRes.get("type");

                if (objCat != null && objCat.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicCat = (JSONObject) objCat;
                    String tid = dicCat.getString("tid");
                    String name = dicCat.getString("name");
                    RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
                    routeCatTerm.setTid(tid);
                    routeCatTerm.setName(name);
                    route.setCategory(routeCatTerm);
                }

                Object objDif = dicRes.get("difficulty");

                if (objDif != null && objDif.getClass().getName().equals(JSONObject.class.getName())) {
                    JSONObject dicDif = (JSONObject) objDif;
                    String tid = dicDif.getString("tid");
                    String name = dicDif.getString("name");
                    RouteDifficultyTerm routeDifTerm = new RouteDifficultyTerm();
                    routeDifTerm.setTid(tid);
                    routeDifTerm.setName(name);
                    route.setDifficulty(routeDifTerm);
                }

                route.setTrack(geom);

                ArrayList<ResourceFile> arrayResourceImages = new ArrayList<ResourceFile>();

                Object objImages = dicRes.get("images");

                if (objImages != null && objImages.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objImages;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            rf.setFileTitle(JSONManager.getString(dic, "title"));
                            rf.setFileBody(JSONManager.getString(dic, "body"));
                            rf.setCopyright(JSONManager.getString(dic, "copyright"));
                            arrayResourceImages.add(rf);
                        }
                    }
                }
                Log.d("Array Resources",String.valueOf(arrayResourceImages.size()));
                route.setImages(arrayResourceImages);

                ArrayList<ResourceFile> arrayResourceAudios = new ArrayList<ResourceFile>();

                Object objAudios = dicRes.get("audios");

                if (objAudios != null && objAudios.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objAudios;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            arrayResourceAudios.add(rf);
                        }
                    }
                }

                route.setAudios(arrayResourceAudios);

                ArrayList<ResourceFile> arrayResourceVideos = new ArrayList<ResourceFile>();

                Object objVideos = dicRes.get("videos");

                if (objVideos != null && objVideos.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objVideos;

                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);

                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceFile rf = new ResourceFile();
                            rf.setFileName(name);
                            rf.setFileUrl(url);
                            arrayResourceVideos.add(rf);
                        }
                    }
                }

                route.setVideos(arrayResourceVideos);

                ArrayList<ResourceLink> arrayResourceLinks = new ArrayList<ResourceLink>();
                Object objLinks = dicRes.get("links");
                if (objLinks != null && objLinks.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objLinks;
                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);
                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String name = dic.getString("name");
                            String url = dic.getString("url");
                            ResourceLink rl = new ResourceLink();
                            rl.setTitle(name);
                            rl.setUrl(url);
                            arrayResourceLinks.add(rl);
                        }
                    }
                }
                route.setEnlaces(arrayResourceLinks);

                ArrayList<ResourcePoi> arrayResourcePois = new ArrayList<ResourcePoi>();
                Object objPois = dicRes.get("pois");
                if (objPois != null && objPois.getClass().getName().equals(JSONArray.class.getName())) {
                    JSONArray array = (JSONArray) objPois;
                    for (int i = 0; i < array.length(); i++) {
                        Object obj = array.get(i);
                        if (obj != null && obj.getClass().getName().equals(JSONObject.class.getName())) {
                            JSONObject dic = (JSONObject) obj;
                            String bodyPoi = dic.getString("body");
                            String titlePoi = dic.getString("title");
                            int numberPoi = dic.getInt("number");
                            double longitudePoi = dic.getDouble("lon");
                            double latitudePoi = dic.getDouble("lat");
                            int typePoi = dic.getInt("type");
                            int nidPoi = dic.getInt("nid");
                            String code1Poi = dic.getString("code1");
                            ResourcePoi rl = new ResourcePoi();
                            rl.setBody(bodyPoi);
                            rl.setTitle(titlePoi);
                            rl.setNumber(numberPoi);
                            rl.setLatitude(latitudePoi);
                            rl.setLongitude(longitudePoi);
                            rl.setType(typePoi);
                            rl.setNid(nidPoi);
                            rl.setCode1(code1Poi);
                            arrayResourcePois.add(rl);
                        }
                    }
                }
                //route.setPois(arrayResourcePois);

                Object objRate = dicRes.get("rate");
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
                    route.setVote(vote);
                }

            } //dicRes != null

        } catch (Exception e) {
            Log.d("Milog", "Excepcion cargar route: " + e.toString());
            route = null;
        }

        return route;
    }

    //

    private void updateLocalDatabase_Pois() {
        cargarListaPoisOrdenadosDistancia(getApplication(), // Aplicacion
                lat, // Latitud
                lon, // Longitud
                0, // Radio en Kms
                0, // N�mero de elementos por p�gina
                0, // P�gina
                null, // Tid de la categor�a que queremos filtrar
                null // Texto a buscar
        );

        progressBar.incrementProgressBy(50);

        loadMainActivity();
    }

    private void cargarListaPoisOrdenadosDistancia(final Application application, double lat, double lon, int radio, int num, int pag, String catTid, String searchTxt) {
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

        Log.d("Milog", "Parametros enviados a poi/get_list_distance: " + params.toString());

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
                                //Log.d("ForEach poi sais:", " POI ID: " + poi.getNid());
                                //Log.d("ForEach poi sais:", " POI Name: " + poi.getTitle());
                                //Log.d("ForEach poi sais:", " POI Body: " + poi.getBody());

                                app.getDBHandler().addOrReplacePoi(poi);
                            }

                            progressBar.incrementProgressBy(25);
                            //app.setPoisList(app.getDBHandler().getPoiList()); //TODO fix image stockage in database, and don't to that, because it require read in database, witch is longer than just app.setPoisList(listaPois);
                            loadedComponents++;

                        } else {
                            app.setPoisList(new ArrayList<Poi>());
                        }
                    }

                    public void onFailure(Throwable error) {

                    }
                },
                params);
    }

    private  ArrayList<Poi> fillPoiList(String response, Application application) {
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
                            Log.d("PierreLog", "PoiImageURL = "+image);
                            String game = recDic.getString("game");
                            String code1 = recDic.getString("code1");

                            Poi item = new Poi();

                            item.setNid(nid);
                            item.setTitle(title);
                            item.setBody(body);
                            item.setGame(game);
                            item.setCode1(code1);


                            handleImages(null,item);
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

                            if (image == null || image.equals("") || image.equals("null")) {
                                item.setMainImage(null);
                                Log.d("PierreDebug", "Poi title : "+title+"     image : "+image);

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

    private void updateLocalDatabase_Pages() {
        try {
            AsyncHttpClient client_pages = new AsyncHttpClient();
            Log.d("SplashActivity", "update Pages try");
            client_pages.get(SplashActivity.this, "http://sitedepayolle.randomobile.eu/api/routedata/pages/retrieve.json", new AsyncHttpResponseHandler() {
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
                    loadedComponents++;
                    loadMainActivity();
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

                    // ignore failure and continue to MainActivity
                    progressBar.incrementProgressBy(25);
                    loadedComponents++;
                    loadMainActivity();
                }
            });

        } catch (Exception e) {
            Log.d("SplashActivity", "update Pages catch");
            Toast.makeText(getApplicationContext(), "Ha fallado la descarga de datos P3", Toast.LENGTH_LONG).show();

            progressBar.incrementProgressBy(25);
            loadedComponents++;
            loadMainActivity();
        }
    }

    //

    public void loadMainActivity() {
        // if (loadedComponents > 3) {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
        //}
    }
}