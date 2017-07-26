package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.ContextWrapper;


public class BadgesActivity extends Activity {
    private MainApp app;
    ImageButton btn_home;
    ImageButton btn_return;
    private ListView mListView;
    private ArrayList<Route> arrayRoutes = null;
    private BadgesActivity.ListBadgesAdapter listBadgesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_badges);

        this.app = (MainApp) getApplication();

        arrayRoutes = app.getRoutesListCO();
        mListView = (ListView) findViewById(R.id.badges_list_listview);
        listBadgesAdapter = new BadgesActivity.ListBadgesAdapter(this, arrayRoutes);
        mListView.setAdapter(listBadgesAdapter);

        capturarControles();
        escucharEventos();
    }

    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase, MainApp.locale);
        super.attachBaseContext(context);
    }

    private void escucharEventos() {

        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BadgesActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BadgesActivity.this.finish();
                    }
                });

    }

    public class ListBadgesAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<Route> listaItems;

        public class ViewHolder {

            ImageView badgeProItemImage;
            ImageView badgeAmaterItemImage;
            ImageView badgeNoviceItemImage;
            TextView badgeItemTitle;

            int index;
        }

        public ListBadgesAdapter(Context _ctx, ArrayList<Route> _items) {
            this.listaItems = _items;
            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (listaItems != null) {
                return listaItems.size();

            } else {
                return 0;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
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

            BadgesActivity.ListBadgesAdapter.ViewHolder holder;
            LinearLayout mViewGroup = new LinearLayout(BadgesActivity.this);

            // Recoger el item
            Route item = listaItems.get(position);


            if (convertView == null) {
                holder = new BadgesActivity.ListBadgesAdapter.ViewHolder();

                convertView = mInflater.inflate(R.layout.feed_activity_badges_item, mViewGroup);


                holder.badgeItemTitle = (TextView) convertView.findViewById(R.id.badge_item_title);
                holder.badgeNoviceItemImage = (ImageView) convertView.findViewById(R.id.badge_item_novice);
                holder.badgeAmaterItemImage = (ImageView) convertView.findViewById(R.id.badge_item_amater);
                holder.badgeProItemImage = (ImageView) convertView.findViewById(R.id.badge_item_pro);

                convertView.setTag(holder);
            } else {
                holder = (BadgesActivity.ListBadgesAdapter.ViewHolder) convertView.getTag();
            }

            // Title
            holder.badgeItemTitle.setText(item.getTitle());

            switch (app.getSuccessByRoute(item.getTitle())) {
                case 1:
                    holder.badgeNoviceItemImage.setImageResource(R.drawable.badges_debutant_actif3x);
                    break;
                case 2:
                    holder.badgeNoviceItemImage.setImageResource(R.drawable.badges_debutant_actif3x);
                    holder.badgeAmaterItemImage.setImageResource(R.drawable.badges_amateur_actif3x);
                    break;
                case 3:
                    holder.badgeNoviceItemImage.setImageResource(R.drawable.badges_debutant_actif3x);
                    holder.badgeAmaterItemImage.setImageResource(R.drawable.badges_amateur_actif3x);
                    holder.badgeProItemImage.setImageResource(R.drawable.badges_pro_actif3x);
                    break;
                default:
                    break;
            }

            return convertView;
        }
    }
}
