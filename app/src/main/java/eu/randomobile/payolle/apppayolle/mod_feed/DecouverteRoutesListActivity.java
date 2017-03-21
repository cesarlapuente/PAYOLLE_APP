package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.Util;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;

public class DecouverteRoutesListActivity extends Activity {

    private MainApp app;
    private ListView mListView;
    private ArrayList<Route> arrayRoutes = null;
    private ListRoutesAdapter listRoutesAdapter;
    private ImageButton btn_home;
    private ImageButton btn_return;
    private ImageButton btn_map;
    private ImageButton btn_info;
    //private ImageButton btn_badges;
    //private TextView btn_galerie;
    private ImageView game_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_routes_list_decouverte);

        mListView = (ListView) findViewById(R.id.routes_list_listview);

        arrayRoutes = app.getRoutesListDE();
        listRoutesAdapter = new ListRoutesAdapter(this, arrayRoutes);
        mListView.setAdapter(listRoutesAdapter);

        capturarControles();
        escucharEventos();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }


        mListView.invalidateViews();

    //    filterRoutes();

        mListView.setAdapter(listRoutesAdapter);
    }

    private void capturarControles() {
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_map = (ImageButton) findViewById(R.id.btn_footer_map);
        //btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);
        //btn_galerie = (TextView) findViewById(R.id.btn_galerie);
        game_image = (ImageView) findViewById(R.id.game_image);

    }

    private void escucharEventos() {
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DecouverteRoutesListActivity.this, InfoDecouverteActivity.class);
                startActivity(intent);
            }
        });
        btn_map.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DecouverteRoutesListActivity.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });
        /*btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DecouverteRoutesListActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });*/
        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DecouverteRoutesListActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DecouverteRoutesListActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
       // ctxList = getApplicationContext();
       // mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Route routePulsado = arrayRoutes.get(index);
                Log.d("JmLog","route pulsado Name :=>"+routePulsado.getTitle());
                app.setRoutesList(arrayRoutes);
                Intent intent = new Intent(DecouverteRoutesListActivity.this, FeedRouteDetailsDecouverte.class);

                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_NID, routePulsado.getNid());
                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_DISTANCE, routePulsado.getDistanceMeters());
                //intent.putExtra(RouteDetailActivity.PARAM_KEY_CATEGORY_ROUTE, routePulsado.getCategory().getName());
                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_TITLE_ROUTE, routePulsado.getTitle());
                intent.putExtra(FeedRouteDetailsDecouverte.PARAM_KEY_MAP_URL, routePulsado.getUrlMap());
               // intent.putExtra(RouteDetailActivity.PARAM_KEY_COLOR_ROUTE, Integer.toString(routePulsado.getColorForMap(ctxList)));

              //  BitmapManager.INSTANCE.cache.remove(routePulsado.getMainImage());

                startActivity(intent);
            }
        });

//        btn_galerie.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(DecouverteRoutesListActivity.this, GaleryActivity.class);
//                        startActivity(intent);
//                    }
//                });
    }

    public class ListRoutesAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<Route> listaItems;

        public class ViewHolder {
            LinearLayout routeItemContainer;
            ImageView routeItemImage;
            TextView routeItemTitle;
            //ImageView routeItemDifficulty;
            TextView routeItemDuration;
            TextView routeItemDistance;
            //ImageView routeItemRating;

            int index;
        }

        public ListRoutesAdapter(Context _ctx, ArrayList<Route> _items) {
            this.listaItems = _items;
            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (listaItems != null) {
                return listaItems.size();

            } else {
                Log.d("Routes List", "liste vide");
                return 0;
            }
        }

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            if(position==0){
                return 0;
            }
            else{
                return 1;
            }
        }
        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LinearLayout mViewGroup = new LinearLayout(DecouverteRoutesListActivity.this);

            // Recoger el item
            Route item = listaItems.get(position);


            if (convertView == null) {
                holder = new ViewHolder();

                if(position==0) {  //Pierre : Why doing this ? Just a different size ?
                    convertView = mInflater.inflate(R.layout.feed_activity_routes_list_firstitem, mViewGroup);
                }
                else {
                    convertView = mInflater.inflate(R.layout.feed_activity_routes_list_item, mViewGroup);
                }

                holder.routeItemContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
                holder.routeItemImage = (ImageView) convertView.findViewById(R.id.item_image);
                holder.routeItemTitle = (TextView) convertView.findViewById(R.id.item_title);
                //holder.routeItemDifficulty = (ImageView) convertView.findViewById(R.id.item_difficulty);
                holder.routeItemDuration = (TextView) convertView.findViewById(R.id.item_duration);
                holder.routeItemDistance = (TextView) convertView.findViewById(R.id.item_distance);
                //holder.routeItemRating = (ImageView) convertView.findViewById(R.id.item_rating);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Title
            holder.routeItemTitle.setText(item.getTitle());
            //holder.routeItemTitle.setSelected(true); //Bug with auto scroll

            // Image
            if (item.getMainImage() != null) {
                Log.d("MainImage", item.getMainImage());
                BitmapManager.INSTANCE.loadBitmap(item.getMainImage(),
                        holder.routeItemImage, 90, 90);
            } else {
                holder.routeItemImage.setImageResource(R.mipmap.ic_launcher);
            }

            // Difficulty
            String dificultad = item.getDifficulty_tid();
            Log.d("Debug", "difficult? :" + item.getDifficulty_tid());
            //Tr?s Facile
            /*if (dificultad.equals("18"))
                holder.routeItemDifficulty.setBackgroundResource(R.drawable.dificultad_1);
                //Facile
            else if (dificultad.equals("16"))
                holder.routeItemDifficulty.setBackgroundResource(R.drawable.dificultad_4);
                //Moyen
            else if (dificultad.equals("17"))
                holder.routeItemDifficulty.setBackgroundResource(R.drawable.dificultad_3);
                //Difficile
            else if (dificultad.equals("22"))
                holder.routeItemDifficulty.setBackgroundResource(R.drawable.dificultad_2);
            */
            // Duration
            holder.routeItemDuration.setText(item.timeToHoursMinutes(item.getEstimatedTime()));

            // Distance
            holder.routeItemDistance.setText(item.getRouteLengthMeters()/1000 + " Km");

            /*
            // Rating
            try {
                String valString = ctx.getResources().getString(R.string.mod_discover__nota);

                if (item.getVote() != null) {
                    if (item.getVote().getValue() <= 10) {
                        // Si es menor o igual a 0
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_0);
                    } else if (item.getVote().getValue() > 10 && item.getVote().getValue() < 30) {
                        // Si est? entre 1 y 24
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_1);
                    } else if (item.getVote().getValue() >= 30 && item.getVote().getValue() < 50) {
                        // Si est? entre 25 y 49
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_2);
                    } else if (item.getVote().getValue() >= 50 && item.getVote().getValue() < 70) {
                        // Si est? entre 50 y 74
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_3);
                    } else if (item.getVote().getValue() >= 70 && item.getVote().getValue() <= 90) {
                        // Si est? entre 75 y 90
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_4);
                    } else {
                        holder.routeItemRating.setImageResource(R.drawable.estrella_icono_5);
                    }
                } else {
                    holder.routeItemRating.setImageResource(R.drawable.estrella_icono_0);
                }
            } catch (Exception e) {
                holder.routeItemRating.setImageResource(R.drawable.estrella_icono_0);
            }
            */

            return convertView;
        }


    }
}


