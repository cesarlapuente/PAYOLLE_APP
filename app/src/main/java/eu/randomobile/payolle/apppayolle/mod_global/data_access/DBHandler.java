package eu.randomobile.payolle.apppayolle.mod_global.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.Normalizer;
import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.model.GeoPoint;
import eu.randomobile.payolle.apppayolle.mod_global.model.Page;
import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourcePoi;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.mod_global.model.taxonomy.RouteCategoryTerm;
import eu.randomobile.payolle.apppayolle.mod_imgmapping.DbBitmapUtility;

public class DBHandler extends SQLiteOpenHelper {
    MainApp app;

    // <---------->__LOCAL_DATABASE_CONFIGURATION_<---------->
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mastermain.db";
    // <---------->__TABLE_ROUTES_CONFIGURATION____<---------->
    private static final String TABLE_ROUTES = "routes";
    public static final String COLUMN_ROUTE_NID = "id";
    public static final String COLUMN_ROUTE_TITLE = "title";
    public static final String COLUMN_ROUTE_CATEGORY_TID = "category_tid";
    public static final String COLUMN_ROUTE_CATEGORY_NAME = "category_name";
    public static final String COLUMN_ROUTE_BODY = "body";
    public static final String COLUMN_ROUTE_DIFFICULTY = "difficulty";
    public static final String COLUMN_ROUTE_DIFFICULTY_TID = "difficulty_tid";
    public static final String COLUMN_ROUTE_CIRCULAR = "circular";
    public static final String COLUMN_ROUTE_DISTANCE = "distanceMeters";
    public static final String COLUMN_ROUTE_LENGTH = "routeLengthMeters";
    public static final String COLUMN_ROUTE_TIME = "estimatedTime";
    public static final String COLUMN_ROUTE_SLOPE = "slope";
    public static final String COLUMN_ROUTE_MAIN_PICTURE = "mainImage";
    public static final String COLUMN_ROUTE_TRACK = "track";
    public static final String COLUMN_ROUTE_MAP_URL = "url_map";
    public static final String COLUMN_ROUTE_MAP_DIRECTORY = "local_directory_map";
    public static final String COLUMN_ROUTE_POIS_LIST = "pois";
    public static final String COLUMN_ROUTE_TAGS_LIST = "tags";
    // <---------->__TABLE_PAGES_CONFIGURATION____<---------->
    private static final String TABLE_PAGES = "pages";
    public static final String COLUMN_NID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    // <---------->__TABLE_MAPS_CONFIGURATION____<---------->
    private static final String TABLE_MAPS = "maps";
    public static final String COLUMN_ROUTE_ID = "id";
    public static final String COLUMN_MAP_LOCAL_DIRECTORY = "directory";
    // <---------->__TABLE_POI____________<---------->
    private static final String TABLE_POI = "poi";
    public static final String COLUMN_POI_ID = "id";
    public static final String COLUMN_POI_BODY = "body";
    public static final String COLUMN_POI_TITLE = "title";
    public static final String COLUMN_POI_LAT = "lat";
    public static final String COLUMN_POI_LON = "lon";
    public static final String COLUMN_POI_GAME = "game";
    public static final String COLUMN_POI_CODE1 = "code1";
    // <---------->__TABLE_BADGES_CONFIGURATION____<---------->
    private static final String TABLE_BADGES = "badges";
    public static final String COLUMN_BADGE_ID = "id";
    public static final String COLUMN_BADGE_ROUTE_ID = "route_id";
    public static final String COLUMN_BADGE_SUCCESS = "success";
    // <---------->__CONFIGURATION_END____________<---------->

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, MainApp app) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        this.app = app;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_routes = "CREATE TABLE " + TABLE_ROUTES +
                "(" +
                COLUMN_ROUTE_NID + " INTEGER PRIMARY KEY, " +
                COLUMN_ROUTE_TITLE + " TEXT, " +
                COLUMN_ROUTE_CATEGORY_TID + " TEXT, " +
                COLUMN_ROUTE_CATEGORY_NAME + " TEXT, " +
                COLUMN_ROUTE_BODY + " TEXT, " +
                COLUMN_ROUTE_DIFFICULTY + " TEXT, " +
                COLUMN_ROUTE_DIFFICULTY_TID + " TEXT, " +
                COLUMN_ROUTE_CIRCULAR + " TEXT, " +
                COLUMN_ROUTE_DISTANCE + " TEXT, " +
                COLUMN_ROUTE_LENGTH + " TEXT, " +
                COLUMN_ROUTE_TIME + " TEXT, " +
                COLUMN_ROUTE_SLOPE + " TEXT, " +
                COLUMN_ROUTE_MAIN_PICTURE + " TEXT, " +
                COLUMN_ROUTE_TRACK + " TEXT, " +
                COLUMN_ROUTE_MAP_URL + " TEXT, " +
                COLUMN_ROUTE_MAP_DIRECTORY + " TEXT, " +
                COLUMN_ROUTE_POIS_LIST + " TEXT " +
                ");";
        db.execSQL(query_routes);

        String query_pages = "CREATE TABLE " + TABLE_PAGES +
                "(" +
                COLUMN_NID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_BODY + " TEXT " +
                ");";
        db.execSQL(query_pages);

        String query_maps = "CREATE TABLE " + TABLE_MAPS +
                "(" +
                COLUMN_ROUTE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_MAP_LOCAL_DIRECTORY + " TEXT " +
                ");";
        db.execSQL(query_maps);

        String query_poi = "CREATE TABLE " + TABLE_POI +
                "(" +
                COLUMN_POI_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_POI_BODY + " TEXT ," +
                COLUMN_POI_TITLE + " TEXT, " +
                COLUMN_POI_LAT + " TEXT," +
                COLUMN_POI_LON + " TEXT, " +
                COLUMN_POI_GAME + " TEXT, " +
                COLUMN_POI_CODE1 + " TEXT " +
                ");";
        db.execSQL(query_poi);

        String query_badges = "CREATE TABLE " + TABLE_BADGES +
                "(" +
                COLUMN_BADGE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_BADGE_ROUTE_ID + " INTEGER, " +
                COLUMN_BADGE_SUCCESS + " INTEGER " +
                ");";
        db.execSQL(query_badges);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (app.getNetStatus() != 0) {
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_ROUTES);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_POI);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_PAGES);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_MAPS);
            db.execSQL("DROP_TABLE IF EXIST " + TABLE_BADGES);

            onCreate(db);

        } else {
            // No hay red. No se pueden eliminar las tablas.
        }
    }

    // <-------------------->_ROUTES_METHODS_<-------------------->

    /**
     * This method receives a Route object and adds it to the local database if not exist or
     * replaces it in the local database if it already exists.
     *
     * @param route (Route) Route object
     */
    public void addOrReplaceRoute(Route route) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROUTE_NID, route.getNid());
        values.put(COLUMN_ROUTE_TITLE, route.getTitle());
        values.put(COLUMN_ROUTE_CATEGORY_TID, route.getCategory().getTid());
        values.put(COLUMN_ROUTE_CATEGORY_NAME, route.getCategory().getName());
        values.put(COLUMN_ROUTE_BODY, route.getBody());


        // values.put(COLUMN_ROUTE_DIFFICULTY, route.getDifficulty().getTid());

        values.put(COLUMN_ROUTE_DIFFICULTY_TID, route.getDifficulty_tid());
        values.put(COLUMN_ROUTE_DISTANCE, route.getDistanceMeters());
        values.put(COLUMN_ROUTE_LENGTH, route.getRouteLengthMeters());
        values.put(COLUMN_ROUTE_TIME, route.getEstimatedTime());
        values.put(COLUMN_ROUTE_SLOPE, route.getSlope());
        //values.put(COLUMN_ROUTE_MAIN_PICTURE, DbBitmapUtility.getBytes(route.getMainImage()));
        values.put(COLUMN_ROUTE_TRACK, route.getTrack());
        values.put(COLUMN_ROUTE_MAP_URL, route.getUrlMap());
        values.put(COLUMN_ROUTE_MAP_DIRECTORY, route.getLocalDirectoryMap());

        String poisList = "";
        for (int i = 0; i < route.getPois().size(); i++) {
            poisList += route.getPois().get(i).getTitle() + ";";
        }

        values.put(COLUMN_ROUTE_POIS_LIST, poisList);

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_ROUTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public void storeMainImage(Route route) {
        String id = route.getNid();
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "UPDATE " + TABLE_ROUTES + " SET " + COLUMN_ROUTE_MAIN_PICTURE + " = \'" + DbBitmapUtility.SaveImage(route.getMainImage(),id) +
                "\' WHERE " + COLUMN_ROUTE_NID + " = " + id;
        db.execSQL(selectQuery);
        db.close();
    }

    public void addOrReplacePoi(Poi poi) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_POI_ID, poi.getNid());
        values.put(COLUMN_POI_TITLE, poi.getTitle());
        values.put(COLUMN_POI_BODY, poi.getBody());
        values.put(COLUMN_POI_LAT, poi.getCoordinates().getLatitude());
        values.put(COLUMN_POI_LON,  poi.getCoordinates().getLongitude());
        values.put(COLUMN_POI_GAME, poi.getGame());
        values.put(COLUMN_POI_CODE1, poi.getCode1());

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_POI, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }
    public ArrayList<Poi> getPoiList(){
        SQLiteDatabase db = getWritableDatabase();

        String[] columns = new String[]{COLUMN_POI_ID, COLUMN_POI_TITLE, COLUMN_POI_BODY,COLUMN_POI_LAT,COLUMN_POI_LON,COLUMN_POI_GAME,COLUMN_POI_CODE1};

        Cursor c = db.query(TABLE_POI, columns, null, null, null, null, COLUMN_POI_ID);

        int iNid = c.getColumnIndex(COLUMN_POI_ID);
        int iTitle = c.getColumnIndex(COLUMN_POI_TITLE);
        int iBody = c.getColumnIndex(COLUMN_POI_BODY);
        int iLat = c.getColumnIndex(COLUMN_POI_LAT);
        int iLon = c.getColumnIndex(COLUMN_POI_LON);
        int iGame = c.getColumnIndex(COLUMN_POI_GAME);
        int iCode1 = c.getColumnIndex(COLUMN_POI_CODE1);


        ArrayList<Poi> result = new ArrayList<>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Poi item = new Poi();
            item.setNid(c.getString(iNid));
            item.setTitle(c.getString(iTitle));
            item.setBody(c.getString(iBody));
            GeoPoint gp = new GeoPoint();
            gp.setLatitude(Double.parseDouble(c.getString(iLat)));
            gp.setLongitude(Double.parseDouble(c.getString(iLon)));
            item.setCoordinates(gp);
            item.setGame(c.getString(iGame));
            item.setCode1(c.getString(iCode1));
            //Log.d("DebugOffline toto", "  item   "+item.getNid()+"      "+item.getTitle()+"     "+ item.getCoordinates().getLatitude());
            result.add(item);
        }
        /*Try to debug*/
        /*String selectQuery = "SELECT * FROM " + TABLE_POI;
        c = db.rawQuery(selectQuery, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Log.d("DebugOffline", "     "+c.getString(c.getColumnIndex(COLUMN_POI_TITLE))+"     "+ c.getString(c.getColumnIndex(COLUMN_POI_LAT)));

        }*/

        db.close();

        return result;
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Route identifier
     */
    public void deleteRoute(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_ROUTES + " WHERE " + COLUMN_NID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method take all Route objects in the local database
     *
     * @return (ArrayList<Route>) List of Routes.
     */
//    public ArrayList<Route> getRouteList() { // TODO looks like unused
//        SQLiteDatabase db = getWritableDatabase();
//
//        String[] columns = new String[]{COLUMN_ROUTE_NID, COLUMN_ROUTE_TITLE, COLUMN_ROUTE_CATEGORY_TID, COLUMN_ROUTE_CATEGORY_NAME, COLUMN_ROUTE_BODY,
//                COLUMN_ROUTE_DIFFICULTY_TID, COLUMN_ROUTE_DISTANCE, COLUMN_ROUTE_LENGTH, COLUMN_ROUTE_TIME, COLUMN_ROUTE_SLOPE, COLUMN_ROUTE_MAIN_PICTURE,
//                COLUMN_ROUTE_TRACK, COLUMN_ROUTE_MAP_URL, COLUMN_ROUTE_MAP_DIRECTORY, COLUMN_ROUTE_POIS_LIST};
//
//        Cursor c = db.query(TABLE_ROUTES, columns, null, null, null, null, COLUMN_ROUTE_ID);
//
//        int iNid = c.getColumnIndex(COLUMN_ROUTE_NID);
//        int iTitle = c.getColumnIndex(COLUMN_ROUTE_TITLE);
//        int iCategory_Tid = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_TID);
//        int iCategory_Name = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_NAME);
//        int iBody = c.getColumnIndex(COLUMN_ROUTE_BODY);
//        //
//        int iDifficulty_Tid = c.getColumnIndex(COLUMN_ROUTE_DIFFICULTY_TID);
//        int iDistance = c.getColumnIndex(COLUMN_ROUTE_DISTANCE);
//        int iLenght = c.getColumnIndex(COLUMN_ROUTE_LENGTH);
//        int iTime = c.getColumnIndex(COLUMN_ROUTE_TIME);
//        int iSlope = c.getColumnIndex(COLUMN_ROUTE_SLOPE);
//        int iMain_Picture = c.getColumnIndex(COLUMN_ROUTE_MAIN_PICTURE);
//        int iTrack = c.getColumnIndex(COLUMN_ROUTE_TRACK);
//        int iMap_URL = c.getColumnIndex(COLUMN_ROUTE_MAP_URL);
//        int iMap_Directory = c.getColumnIndex(COLUMN_ROUTE_MAP_DIRECTORY);
//        int iPoi_List = c.getColumnIndex(COLUMN_ROUTE_POIS_LIST);
//
//        Context ctx = app.getApplicationContext();
//
//        ArrayList<Route> result = new ArrayList<Route>();
//
//
//        /*POI recup part*/
//
//        String[] columns2 = new String[]{COLUMN_POI_ID, COLUMN_POI_TITLE, COLUMN_POI_LAT,COLUMN_POI_LON};
//
//        Cursor c2 = db.query(TABLE_POI, columns2, null, null, null, null, COLUMN_POI_ID);
//
//        int iNid2 = c.getColumnIndex(COLUMN_POI_ID);
//        int iTitle2 = c.getColumnIndex(COLUMN_POI_TITLE);
//        int iLat2 = c.getColumnIndex(COLUMN_POI_LAT);
//        int iLon2 = c.getColumnIndex(COLUMN_POI_LON);
//
//
//        /*End of POI part*/
//
//
//
//
//        int count_GR = 0;
//        int count_PR = 0;
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//            Route item = new Route();
//
//            item.setNid(c.getString(iNid));
//            item.setTitle(c.getString(iTitle));
//
//            RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
//            routeCatTerm.setTid(c.getString(iCategory_Tid));
//            routeCatTerm.setName(c.getString(iCategory_Name));
//            item.setCategory(routeCatTerm);
//
//            if (item.getCategory().getName().equals("PR")) {
//                int n = count_PR % 3;
//
//                switch (n) {
//                    case 0:
//                        item.setColor(ctx.getResources().getColor(R.color.pr1_route));
//                        break;
//                    case 1:
//                        item.setColor(ctx.getResources().getColor(R.color.pr2_route));
//                        break;
//                    case 2:
//                        item.setColor(ctx.getResources().getColor(R.color.pr3_route));
//                        break;
//                    default:
//                        break;
//                }
//
//                count_PR++;
//
//            } else if (item.getCategory().getName().equals("GR")) {
//                int n = count_GR % 2;
//
//                switch (n) {
//                    case 0:
//                        item.setColor(ctx.getResources().getColor(R.color.gr1_route));
//                        break;
//                    case 1:
//                        item.setColor(ctx.getResources().getColor(R.color.gr2_route));
//                        break;
//                    default:
//                        break;
//                }
//
//                count_GR++;
//
//            } else if (item.getCategory().getName().equals("CR")) {
//                item.setColor(ctx.getResources().getColor(R.color.cr1_route));
//            }
//
//            item.setBody(c.getString(iBody));
//            item.setDifficulty_tid(c.getString(iDifficulty_Tid));
//
//            double distanceKMDouble = Double.valueOf(c.getString(iDistance));
//            double distanceMDouble = distanceKMDouble * 1000;
//            item.setDistanceMeters(distanceMDouble);
//
//            item.setRouteLengthMeters(Double.valueOf(c.getString(iLenght)));
//            item.setEstimatedTime(Double.valueOf(c.getString(iTime)));
//            item.setSlope(Double.valueOf(c.getString(iSlope)));
//            item.setMainImageURL(c.getString(iMain_Picture));
//            item.setTrack(c.getString(iTrack));
//
//            item.setUrlMap(c.getString(iMap_URL));
//            item.setMapsLocalDirectory(c.getString(iMap_Directory));
//
//            String routeListTemp = c.getString(iPoi_List);
//            String routeList[] = routeListTemp.split(";");
//
//            ArrayList<ResourcePoi> listaPois = new ArrayList<>();
//
//            for (int i = 0; i < routeList.length; i++) {
//                ResourcePoi poi = new ResourcePoi();
//
//                String temp = routeList[i];
//                Log.d("****_****", temp);
//
//                if (!(temp.equals(""))) {
//                    if (!(temp.equals(";"))) {
//                        Log.d("********************", temp);
//                        poi.setNid(Integer.parseInt(temp));
//                        // TODO rebuild poi here
//                        for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
//                            //Log.d("Moonwalk", "Poi route : "+Integer.parseInt(temp)+"  Poi poi : "+Integer.parseInt(c.getString(iNid2)));
//                            if(Integer.parseInt(temp)==Integer.parseInt(c.getString(iNid2))) {
//                                poi.setTitle(c.getString(iTitle2));
//                                poi.setLatitude(c.getFloat(iLat2));
//                                poi.setLongitude(c.getFloat(iLon2));
//                            }
//                        }
//                    }
//                }
//
//                listaPois.add(poi);
//            }
//
//            item.setPois(listaPois);
//
//            result.add(item);
//        }
//
//        db.close();
//
//        return result;
//    }

    public ArrayList<Route> getRouteListByCateg(String name){
        SQLiteDatabase db = getWritableDatabase();

        String[] columns = new String[]{COLUMN_ROUTE_NID, COLUMN_ROUTE_TITLE, COLUMN_ROUTE_CATEGORY_TID, COLUMN_ROUTE_CATEGORY_NAME, COLUMN_ROUTE_BODY,
                COLUMN_ROUTE_DIFFICULTY_TID, COLUMN_ROUTE_DISTANCE, COLUMN_ROUTE_LENGTH, COLUMN_ROUTE_TIME, COLUMN_ROUTE_SLOPE, COLUMN_ROUTE_MAIN_PICTURE,
                COLUMN_ROUTE_TRACK, COLUMN_ROUTE_MAP_URL, COLUMN_ROUTE_MAP_DIRECTORY, COLUMN_ROUTE_POIS_LIST};

        Cursor c = db.query(TABLE_ROUTES, columns, null, null, null, null, COLUMN_ROUTE_ID);

        int iNid = c.getColumnIndex(COLUMN_ROUTE_NID);
        int iTitle = c.getColumnIndex(COLUMN_ROUTE_TITLE);
        int iCategory_Tid = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_TID);
        int iCategory_Name = c.getColumnIndex(COLUMN_ROUTE_CATEGORY_NAME);
        int iBody = c.getColumnIndex(COLUMN_ROUTE_BODY);
        //
        int iDifficulty_Tid = c.getColumnIndex(COLUMN_ROUTE_DIFFICULTY_TID);
        int iDistance = c.getColumnIndex(COLUMN_ROUTE_DISTANCE);
        int iLenght = c.getColumnIndex(COLUMN_ROUTE_LENGTH);
        int iTime = c.getColumnIndex(COLUMN_ROUTE_TIME);
        int iSlope = c.getColumnIndex(COLUMN_ROUTE_SLOPE);
        int iMain_Picture = c.getColumnIndex(COLUMN_ROUTE_MAIN_PICTURE);
        int iTrack = c.getColumnIndex(COLUMN_ROUTE_TRACK);
        int iMap_URL = c.getColumnIndex(COLUMN_ROUTE_MAP_URL);
        int iMap_Directory = c.getColumnIndex(COLUMN_ROUTE_MAP_DIRECTORY);
        int iPoi_List = c.getColumnIndex(COLUMN_ROUTE_POIS_LIST);

        Context ctx = app.getApplicationContext();

        ArrayList<Route> result = new ArrayList<Route>();

        int count_GR = 0;
        int count_PR = 0;

        /*POI recup part*/ //Realy not optimised

        String[] columns2 = new String[]{COLUMN_POI_ID, COLUMN_POI_TITLE, COLUMN_POI_BODY,COLUMN_POI_LAT,COLUMN_POI_LON};

        Cursor c2 = db.query(TABLE_POI, columns2, null, null, null, null, COLUMN_POI_ID);

        int iNid2 = c2.getColumnIndex(COLUMN_POI_ID);
        int iTitle2 = c2.getColumnIndex(COLUMN_POI_TITLE);
        int iBody2 = c2.getColumnIndex(COLUMN_POI_BODY);
        int iLat2 = c2.getColumnIndex(COLUMN_POI_LAT);
        int iLon2 = c2.getColumnIndex(COLUMN_POI_LON);


        ArrayList<Poi> poi_list = new ArrayList<>();

        for (c2.moveToFirst(); !c2.isAfterLast(); c2.moveToNext()) {
            Poi item = new Poi();
            item.setNid(c2.getString(iNid2));
            item.setTitle(c2.getString(iTitle2));
            item.setBody(c2.getString(iBody2));
            GeoPoint gp = new GeoPoint();
            gp.setLatitude(Double.parseDouble(c2.getString(iLat2)));
            gp.setLongitude(Double.parseDouble(c2.getString(iLon2)));
            item.setCoordinates(gp);
            poi_list.add(item);
        }


        /*End of POI part*/


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Route item = new Route();

            item.setNid(c.getString(iNid));
            item.setTitle(c.getString(iTitle));

            RouteCategoryTerm routeCatTerm = new RouteCategoryTerm();
            routeCatTerm.setTid(c.getString(iCategory_Tid));
            routeCatTerm.setName(c.getString(iCategory_Name));
            item.setCategory(routeCatTerm);

            if (item.getCategory().getName().equals("PR")) { //TODO is it used ?
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

            item.setBody(c.getString(iBody));
            item.setDifficulty_tid(c.getString(iDifficulty_Tid));

            double distanceKMDouble = Double.valueOf(c.getString(iDistance));
            double distanceMDouble = distanceKMDouble * 1000;
            item.setDistanceMeters(distanceMDouble);

            item.setRouteLengthMeters(Double.valueOf(c.getString(iLenght)));
            item.setEstimatedTime(Double.valueOf(c.getString(iTime)));
            item.setSlope(Double.valueOf(c.getString(iSlope)));
            if(c.getString(iMain_Picture) != null)
                item.setMainImage(DbBitmapUtility.getImage(c.getString(iMain_Picture)));
            item.setTrack(c.getString(iTrack));

            item.setUrlMap(c.getString(iMap_URL));
            item.setMapsLocalDirectory(c.getString(iMap_Directory));

            String routeListTemp = c.getString(iPoi_List);
            String routeList[] = routeListTemp.split(";");

            ArrayList<ResourcePoi> listaPois = new ArrayList<>();

            for (int i = 0; i < routeList.length; i++) {
                ResourcePoi poi = new ResourcePoi();

                String temp = routeList[i];
                //Log.d("****_****", temp);

                if (!(temp.equals(""))) {
                    if (!(temp.equals(";"))) {
                        //Log.d("***************NOOO", temp);
                        for (Poi tmppoi : poi_list){
                            String tmp1 = Normalizer.normalize(tmppoi.getTitle(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                            String tmp2 = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll(" ", "").toLowerCase();
                            if(tmp1.equals(tmp2)) {
                                poi.setNid(Integer.parseInt(tmppoi.getNid()));
                                poi.setTitle(tmppoi.getTitle());
                                poi.setLatitude(tmppoi.getCoordinates().getLatitude());
                                poi.setLongitude(tmppoi.getCoordinates().getLongitude());
                            }
                        }
                    }
                }

                listaPois.add(poi);
            }

            item.setPois(listaPois);
            if(item.getCategory().getName().equals(name)){
                result.add(item);
            }
        }

        db.close();

        return result;
    }
    /*





    // <-------------------->_PAGES_METHODS_<-------------------->

    /**
     * This method receives a Page object and adds it to the local database if not exist or
     * replaces it in the local database if it already exists.
     *
     * @param page (Page) Page object
     */
    public void addOrReplacePage(Page page) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_NID, page.getNid());
        values.put(COLUMN_TITLE, page.getTitle());
        values.put(COLUMN_BODY, page.getBody());

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_PAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    /**
     * This method receives a Page identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Page identifier
     */
    public void deletePage(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_PAGES + " WHERE " + COLUMN_NID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method receives a Page identifier, find that identification in the local database and
     * returns the Page object whose identifier matches the identifier received (if any).
     *
     * @param id (int) Page identifier
     * @return (Page) Or null if not found
     */
    public Page getPageById(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PAGES + " WHERE " + COLUMN_NID + " = " + String.valueOf(id);
        Cursor c = db.rawQuery(selectQuery, null);

        // Identifiers
        int iNid = c.getColumnIndex(COLUMN_NID);
        int iTitle = c.getColumnIndex(COLUMN_TITLE);
        int iBody = c.getColumnIndex(COLUMN_BODY);

        Page result = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Page page = new Page(Integer.parseInt(c.getString(iNid)), c.getString(iTitle), c.getString(iBody));
            result = page;
        }

        db.close();

        return result;
    }

    // <-------------------->_MAPS_METHODS_<-------------------->

    /**
     * @param id                 (int) Route id.
     * @param mapsLocalDirectory (String) Local directory where route map is.
     */
    public void addOrReplaceMap(int id, String mapsLocalDirectory) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROUTE_ID, id);
        values.put(COLUMN_MAP_LOCAL_DIRECTORY, mapsLocalDirectory);

        SQLiteDatabase db = getWritableDatabase();

        db.insertWithOnConflict(TABLE_MAPS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * delete it from the local database (if any).
     *
     * @param id (int) Route identifier
     */
    public void deleteMap(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_MAPS + " WHERE " + COLUMN_ROUTE_ID + "=\"" + id + "\";");

        db.close();
    }

    /**
     * This method receives a Route identifier, find that identification in the local database and
     * returns the route map's local directory whose identifier matches the identifier received (if any).
     *
     * @param id (int) Route identifier
     * @return (String) Or null if not found
     */
    public String getRouteMapsLocalDirectory(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_MAPS + " WHERE " + COLUMN_ROUTE_ID + " = " + String.valueOf(id);
        Cursor c = db.rawQuery(selectQuery, null);

        // Identifiers
        int colum_ID = c.getColumnIndex(COLUMN_ROUTE_ID);
        int colum_Diretory = c.getColumnIndex(COLUMN_MAP_LOCAL_DIRECTORY);

        String result = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(colum_Diretory);
        }

        db.close();

        return result;
    }

    // <-------------------->_BADGES_<-------------------->

    public Boolean getSuccessByRoute(String route){
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT " + COLUMN_BADGE_SUCCESS + " FROM " + TABLE_BADGES + " INNER JOIN " + TABLE_ROUTES +
                " ON " + TABLE_BADGES + "." + COLUMN_BADGE_ROUTE_ID + " = " + TABLE_ROUTES + "." + COLUMN_ROUTE_ID +
                " WHERE " +  COLUMN_ROUTE_TITLE + " = \"" + route + "\"";
        Cursor c = db.rawQuery(selectQuery, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if(c.getString(c.getColumnIndex(COLUMN_BADGE_SUCCESS)).equals("1"))
                return true;
        }

        db.close();

        return false;
    }

    public void setSuccessByRoute(String route){
        SQLiteDatabase db = getWritableDatabase();

        String selectQuery = "SELECT " + COLUMN_ROUTE_NID +
                " FROM " + TABLE_ROUTES +
                " WHERE " +  COLUMN_ROUTE_TITLE + " = \"" + route + "\"";
        Cursor c = db.rawQuery(selectQuery, null); //Find ID
        c.moveToFirst();

        selectQuery = "INSERT INTO " + TABLE_BADGES +" ("+ COLUMN_BADGE_ROUTE_ID + ", " + COLUMN_BADGE_SUCCESS + ")" +
                " SELECT " + c.getString(c.getColumnIndex(COLUMN_ROUTE_NID)) +", 1" +
                " WHERE NOT EXISTS(" +
                "SELECT 1 FROM "+ TABLE_BADGES + " WHERE " + COLUMN_BADGE_ROUTE_ID + " = " + c.getString(c.getColumnIndex(COLUMN_ROUTE_NID)) + ");";
        db.execSQL(selectQuery);

        db.close();
    }

    // <-------------------->_END_OF_FILE_<-------------------->
}
