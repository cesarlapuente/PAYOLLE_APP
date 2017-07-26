package eu.randomobile.payolle.apppayolle;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Locale;

import eu.randomobile.payolle.apppayolle.mod_feed.FeedInfo;
import eu.randomobile.payolle.apppayolle.mod_global.Util;
import eu.randomobile.payolle.apppayolle.mod_global.data_access.DBHandler;
import eu.randomobile.payolle.apppayolle.mod_global.environment.Drupal7RESTClient;
import eu.randomobile.payolle.apppayolle.mod_global.environment.Drupal7Security;
import eu.randomobile.payolle.apppayolle.mod_global.environment.ExternalStorage;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_offline.Offline;


public class MainApp extends Application {
    private DBHandler dBHandler;

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;

    // Nombre del fichero de la BBDD
    public String NOMBRE_FICH_BBDD = "bd_app_pnrlorraine.sqlite";

    // Identificador del recurso del fichero de BBDD
    public int RES_ID_FICH_BBBDD = R.raw.bd_app_pnrlorraine;

    // Nombre de la carpeta en la SD donde est� la BBDD de la app
    public String CARPETA_SD = "Randomobile";
    public long LENGTH_LORRAINE_TPK = 0;
    // Claves para acceder a las cookies
    public String COOKIE_KEY_ID_USUARIO_LOGUEADO = "idUsuarioLogueado";
    public String COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO = "idSesionUsuarioLogueado";
    public String COOKIE_KEY_TIMESTAMP_SESSID = "sessionid_timestamp";
    public String COOKIE_KEY_NICK_USUARIO_LOGUEADO = "nickUsuarioLogueado";
    public String COOKIE_KEY_EMAIL_USUARIO_LOGUEADO = "emailUsuarioLogueado";
    public String COOKIE_KEY_NOMBRE_USUARIO_LOGUEADO = "nombreUsuarioLogueado";
    public String COOKIE_KEY_APELLIDOS_USUARIO_LOGUEADO = "apellidosUsuarioLogueado";
    public String COOKIE_KEY_SESSION_NAME = "sessionName";
    public String COOKIE_KEY_RANKING_USUARIO_LOGUEADO = "rankingUsuarioLogueado";
    public String COOKIE_KEY_COMUNIDAD_AUTONOMA_USUARIO_LOGUEADO = "isoCCAAUsuarioLogueado";
    public String COOKIE_KEY_PAIS_USUARIO_LOGUEADO = "isoPaisUsuarioLogueado";

    /*Language*/
    public static Locale locale = Locale.getDefault();


    private ArrayList<Route> routesList;
    private ArrayList<Route> routesList_CO;
    private ArrayList<Route> routesList_DE;

    public ArrayList<FeedInfo> listInfo_CO = new ArrayList<>();
    public ArrayList<FeedInfo> listInfo_DE = new ArrayList<>();
    public ArrayList<FeedInfo> listInfo_LI = new ArrayList<>();

    private ArrayList<Poi> poisList;
    private int espaciosRuta[];
    private int filtroCategoriasPOIs[];


    public ArrayList<Route> getRoutesListCO() {
        return routesList_CO;
    }

     public void setRoutesListCO(ArrayList<Route> routesList_CO) {this.routesList_CO = routesList_CO;}

    public ArrayList<Route> getRoutesListDE() {
        return routesList_DE;
    }

    public void setRoutesListDE(ArrayList<Route> routesList_DE) {this.routesList_DE = routesList_DE;}

    public ArrayList<Route> getRoutesList() {
        return routesList;
    }

    public void setRoutesList(ArrayList<Route> routesList) {
        this.routesList = routesList;
    }

    /* INFO ARRAYLIST */
    public ArrayList<FeedInfo> getInfoListCO() {return listInfo_CO;}
    public void setInfoListCO(ArrayList<FeedInfo> listInfo_CO) {this.listInfo_CO = listInfo_CO;}
    public ArrayList<FeedInfo> getInfoListDE() {return listInfo_DE;}
    public void setInfoListDE(ArrayList<FeedInfo> listInfo_DE) {this.listInfo_DE = listInfo_DE;}
    public ArrayList<FeedInfo> getInfoListLI() {return listInfo_LI;}
    public void setInfoListLI(ArrayList<FeedInfo> listInfo_LI) {this.listInfo_LI = listInfo_LI;}



    public ArrayList<Poi> getPoisList() {
        return poisList;
    }

    public void setPoisList(ArrayList<Poi> poisList) {
        this.poisList = poisList;
    }

    public DBHandler getDBHandler (){
        return dBHandler;
    }

    public int[] getFiltroCategoriasPOIs() {
        return filtroCategoriasPOIs;
    }

    public void setFiltroCategoriasPOIs(int[] filtroCategoriasPOIs) {
        this.filtroCategoriasPOIs = filtroCategoriasPOIs;
    }

    public int[] getEspaciosRuta() {
        return espaciosRuta;
    }

    public void setEspaciosRuta(int[] espaciosRutaN) {
        this.espaciosRuta = espaciosRutaN;
    }



    public int getSuccessByRoute(String route){
        return dBHandler.getSuccessByRoute(route);
    }

    public void setSuccessByRoute(String route, int success){
        dBHandler.setSuccessByRoute(route, success);
    }

    // Nombres de dominios y urls de servicios
    //public String URL_SERVIDOR = "http://185.18.198.182/"; // Altoagueda (por defecto)
    //public String URL_SERVIDOR = "http://dns198182.phdns.es/"; //Altoagueda (nuevo -ha habido redireccion ips-)
    public String URL_SERVIDOR = "http://sitedepayolle.randomobile.eu/";

    public String ENDPOINT = "api";

    // Tiempo m�ximo que dura una sesi�n abierta (en segundos)
    public long MAX_SESION_LIFETIME = 1296000000; // 15 d�as

    // Constante del radio maximo de distancia para buscar pois y rutas (en metros)
    public int MAX_DISTANCE_SEARCH_GEOMETRIES = /*1000000*/ 		1000000;

    // Constante del radio maximo de distancia para resolver enigma (en metros)
    public int MAX_DISTANCE_RESOLVE_ENIGMA_METERS = /*1000000*/		150;

    // Constante que define el radio de distancia en el que se buscar�n geocach�s cercanos (en kms)
    public int DISTANCE_SEARCH_GEOCACHES_KMS = /*200000*/ 			20000;

    // Constante que define la distancia m�xima en la que el usuario podr� realizar un checkin (en metros)
    public int MAX_DISTANCE_MAKE_CHECKIN_METERS = /*1000000*/		150;

    // Constante que define la distancia m�xima en la que el usuario podr� capturar un geocach� (en metros)
    public int MAX_DISTANCE_CAPTURE_GEOCACHE_METERS = /*1000000*/		150;

    // Constante del radio maximo de distancia para buscar pois y rutas en la ra (en metros)
    public int MAX_DISTANCE_SEARCH_GEOMETRIES_RA = 1000000;

    // Clave para el sdk de Wikitude (realidad aumentada)
    public static final String	WIKITUDE_SDK_KEY = "IBZZLpJwK/UdGkz7tDBG9oLEB0BmYdPVOAFGIy/4ifZ9JqsonY9n+g17uvP9vwUra8JMF1yQqcypyhL/sAyatzRZB6EmxFx8o6oQunAILoMWozCYtvjtU9rtdx/tav0gTel/q7nS5zLGtQrnEuu0iw6tpcOZcmf2es25IMw++zJTYWx0ZWRfX4C0RiGPecOHGy1ND8HBBktQTbAlmoR62Xky0bXdn6G5GvEN5SDYojup4K1tYQPchYrvX22JLzz0lDJuueOL/xDEjfa7SYIMREdZn/U31uqq+CgZRonPc3alSAfXaY0pK73DoX5K/w74qrkpfEpMYMYIfyS0CMaxIgvWQ8/Zjid5MADtayXVqScGhEYmqpmUfFePTefeZLyUap2kr1w8/58aP9dS5Q+c5FxiCOM5JDH6L8NWIsMxe+4v37GpCpNBZVZJAiBn6BlKEk+e2ka61y87MYozKRBwuadxe/W8E/ev2ynjirK97HxYLSzbWOBBIkpTI/iNh4jjfWjtrwHXiC1Y5vtMZS9WswbCTgCyhVv/FiKj6dL+Ejg/jqt23CEuh3VEzoksNzS4LD1ymn3PSlI3U75HF504fZTOWQ+r3ssaIsaPxoTH42l9288p6WhafMeoNE+H90j/ujEjl2+R8BivtKLsPq0x+GQVTGSHnqX+q2kQVNYa5rs=";

    // Coordenadas
    public String FILTER_KEY_LAST_LOCATION_LATITUDE = "filter_key_last_location_latitude";
    public String FILTER_KEY_LAST_LOCATION_LONGITUDE = "filter_key_last_location_longitude";
    public String FILTER_KEY_LAST_LOCATION_ALTITUDE = "filter_key_last_location_altitude";

    // Claves para acceder a los filtros de pois
    public String FILTER_KEY_POI_RADIO_DISTANCIA_KMS = "filter_key_poi_radio_distancia";
    public String FILTER_KEY_POI_CATEGORY_TID = "filter_key_poi_category_tid";
    public String FILTER_KEY_POI_TEXT = "filter_key_poi_text_search";

    // Claves para acceder a los filtros de pois
    public String FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS = "filter_key_route_radio_distancia";
    public String FILTER_KEY_ROUTE_CATEGORY_TID = "filter_key_route_category_tid";
    public String FILTER_KEY_ROUTE_DIFFICULTY_TID = "filter_key_route_difficulty_tid";
    public String FILTER_KEY_ROUTE_TEXT = "filter_key_route_text_search";

    // Claves para acceder a los filtros de Panoramio
    public String FILTER_KEY_NUM_PANORAMIOS_CARGAR = "filter_key_num_panoramios_cargar";
    public String FILTER_KEY_NUM_WIKIPEDIAS_CARGAR = "filter_key_num_wikipedias_cargar";
    public String FILTER_KEY_NUM_YOUTUBES_CARGAR = "filter_key_num_youtubes_cargar";

    // Define la el api key para los mapas de bing
    public String BING_MAPS_KEY = "AknzEuD2VRWfmk_HRnAq7SIBMNE6n3qXskFXUDhEW5kWjTlnec8I5dHjWht9_j_O";

    // Types de drupal
    public String DRUPAL_TYPE_POI = "poi";
    public String DRUPAL_TYPE_ROUTE = "route";

    // Preferencias de la aplicaci�n
    public SharedPreferences preferencias;

    // Referencia espacial para los mapas

    // Nombre del almacen de cookies
    public String getPreferencesKEY(Context ctx) {
        return Util.getPackageName(ctx) + "_cookies";
    }

    // Cliente del API de conexi�n con Servicios 3. Se inicializa al arrancar la
    // aplicaci�n
    public Drupal7RESTClient clienteDrupal;

    // Clase para gestionar la seguridad entre la app y los servicios
    public Drupal7Security drupalSecurity;


    public int getNetStatus(){
        // Checking for network status.
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            // Checking for wifi network status.
            NetworkInfo info_wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (info_wifi != null) {
                if (info_wifi.isConnected()) {
                    return 1;
                }
            }

            // Checking for mobile network status.
            NetworkInfo info_mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (info_mobile != null) {
                if (info_mobile.isConnected()) {
                    return 2;
                }
            }
        }

        return 0;
    }


    // Evento al lanzarse la aplicaci�n. Poner aqu� las inicializaciones
    public void onCreate(){
        super.onCreate();
        Log.d("Milog","Entre On Create MAINAPP");
      //ActivityCompat.requestPermissions((Activity)getApplicationContext(),new String[]{Manifest.permission.READ_CONTACTS},2);
        try{
            this.inicializarAplicacion();

        }catch (Exception e) {
            e.printStackTrace();
        }

        initImageLoader();

        /*For debug, need to be initialized*/ //TODO remote that part
        routesList = new ArrayList<Route>();
        routesList_CO = new ArrayList<Route>();
        routesList_DE = new ArrayList<Route>();
        poisList = new ArrayList<Poi>();


        dBHandler = new DBHandler(getApplicationContext(), null, null, 1, MainApp.this);
        Log.d("Milog","Fin On Create MAINAPP");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initImageLoader() {
        DisplayImageOptions displayImageOptions= new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(displayImageOptions).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }
    public  Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 110, 110, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    // Metodo que hace las primeras operaciones al arrancar la aplicaci�n
    public void inicializarAplicacion() {
        Context _ctx = getApplicationContext();

        //Registrar en Esri

        // Obtener una copia de la BBDD con permisos de escritura
        boolean haySD = ExternalStorage.tieneSD(_ctx);
        if (haySD) {
            // Si tiene almacenamiento externo disponible, generamos la
            // estructura de directorios y ficheros
            ExternalStorage.comprobarDirectorioAppSD(this);
        } else {
            // No tiene tarjeta SD. Avisarle y salir.
            Util.mostrarMensaje(
                    _ctx,
                    "Pas de carte SD",
                    "Se il vous pla�t installer une carte m�moire dans votre appareil pour utiliser l'application");
            return;
        }

        // Inicializar las cookies
        this.preferencias = _ctx.getSharedPreferences(
                this.getPreferencesKEY(_ctx), Context.MODE_PRIVATE);

        // Inicializar el cliente de Drupal para Servicios 3
        String preferencesKey = this.getPreferencesKEY(_ctx);
        String server = this.URL_SERVIDOR + this.ENDPOINT + "/";
        String domain = this.URL_SERVIDOR;
        Long maxLifeTime = Long.valueOf(this.MAX_SESION_LIFETIME);
        if (this.clienteDrupal == null) {
            Log.d("Milog", "Cliente drupal es nulo. Voy a inicializarlo");
            this.clienteDrupal = new Drupal7RESTClient(this,
                    preferencesKey, server, domain, maxLifeTime);
        } else {
            Log.d("Milog", "Cliente drupal no es nulo");
        }

        // Inicializar la seguridad de drupal
        //if(this.drupalSecurity == null){
        this.drupalSecurity = new Drupal7Security();
        //}

        // Colocar la capa base por defecto para los mapas
        this.setCapaBaseSeleccionadaPorDefecto();


        // Reestablecer los filtros
        this.resetPoiFilters();

        //Ver si se tiene que crear la BBDD para el modo Offline
        Util.setRouteFolder(_ctx);
       Offline.createDB(this);
        String jsonString;
//		Offline.fillPoisTable(this);
//		Offline.fillRoutesTable(this);
//		OfflineRoute.cargaListaRutasOffline (this);
//		OfflineRoute.fillRouteItem(this, "962");
 //       OfflineRoute.fillRoutesTable(this);
//OfflinePoi.fillPoisTable(this);
//		jsonString = Offline.extractJsonList (this, this.DRUPAL_TYPE_POI);
//		jsonString = Offline.extractJsonList (this, this.DRUPAL_TYPE_ROUTE);
//		jsonString = "";
    }

    public void setCapaBaseSeleccionadaPorDefecto() {
    }

    public void resetPoiFilters(){
        SharedPreferences.Editor edit = this.preferencias.edit();

        // Quitar el filtro de la ubicaci�n del usuario
        edit.remove(this.FILTER_KEY_LAST_LOCATION_LATITUDE);
        edit.remove(this.FILTER_KEY_LAST_LOCATION_LONGITUDE);
        edit.remove(this.FILTER_KEY_LAST_LOCATION_ALTITUDE);

        // Eliminar el filtro el radio de distancia
        if(this.preferencias.getInt(this.FILTER_KEY_POI_RADIO_DISTANCIA_KMS, 0) == 0 ){
            edit.remove(this.FILTER_KEY_POI_RADIO_DISTANCIA_KMS);
        }

        // Eliminar el filtro de categoria para los pois
        edit.remove(this.FILTER_KEY_POI_CATEGORY_TID);

        // Eliminar el filtro de texto para los pois
        edit.remove(this.FILTER_KEY_POI_TEXT);

        edit.commit();
    }

    public void resetRouteFilters(){
        SharedPreferences.Editor edit = this.preferencias.edit();

        // Quitar el filtro de la ubicaci�n del usuario
        edit.remove(this.FILTER_KEY_LAST_LOCATION_LATITUDE);
        edit.remove(this.FILTER_KEY_LAST_LOCATION_LONGITUDE);
        edit.remove(this.FILTER_KEY_LAST_LOCATION_ALTITUDE);

        // Eliminar el filtro el radio de distancia
        if(this.preferencias.getInt(this.FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS, 0) == 0 ){
            edit.remove(this.FILTER_KEY_ROUTE_RADIO_DISTANCIA_KMS);
        }

        // Eliminar el filtro de categoria para las rutas
        edit.remove(this.FILTER_KEY_ROUTE_CATEGORY_TID);

        // Eliminar el filtro de dificultad para las rutas
        edit.remove(this.FILTER_KEY_ROUTE_DIFFICULTY_TID);

        // Eliminar el filtro de texto para las rutas
        edit.remove(this.FILTER_KEY_ROUTE_TEXT);

        edit.commit();
    }

    public void resetExternalPoiFilters(){
        SharedPreferences.Editor edit = this.preferencias.edit();

        if(this.preferencias.getInt(this.FILTER_KEY_NUM_PANORAMIOS_CARGAR, -1) == -1){
            edit.putInt(this.FILTER_KEY_NUM_PANORAMIOS_CARGAR, 0);
        }

        if(this.preferencias.getInt(this.FILTER_KEY_NUM_WIKIPEDIAS_CARGAR, -1) == -1){
            edit.putInt(this.FILTER_KEY_NUM_WIKIPEDIAS_CARGAR, 0);
        }

        if(this.preferencias.getInt(this.FILTER_KEY_NUM_YOUTUBES_CARGAR, -1) == -1){
            edit.putInt(this.FILTER_KEY_NUM_YOUTUBES_CARGAR, 0);
        }
        edit.commit();
    }


    public void storeMainImage(Route route){
        dBHandler.storeMainImage(route);
    }

}
