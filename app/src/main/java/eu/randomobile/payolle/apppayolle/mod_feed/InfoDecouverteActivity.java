package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;

public class InfoDecouverteActivity extends Activity {

    private MainApp app;

    FeedInfoList listInfoAdapter;
    LinearLayout list_linearlayout;
    ImageButton btn_home;
    ImageButton btn_return;
    ImageButton btn_map;
    ImageButton btn_list;
    ImageButton btn_badges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_info_decouverte);
        listInfoAdapter = new FeedInfoList(this, app.getInfoListDE());
        list_linearlayout = (LinearLayout)findViewById(R.id.routes_list_linearlayout);
        final int adapterCount = listInfoAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = listInfoAdapter.getView(i, null, null);
            list_linearlayout.addView(item);
        }

        capturarControles();
        escucharEventos();
    }

    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_list = (ImageButton) findViewById(R.id.btn_footer_list);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_map = (ImageButton) findViewById(R.id.btn_footer_map);
        btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);


    }

    private void escucharEventos() {
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InfoDecouverteActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InfoDecouverteActivity.this.finish();
                    }
                });
        btn_map.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InfoDecouverteActivity.this, FeedRouteActivityDecouverte.class);
                        startActivity(intent);
                    }
                });
        btn_list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InfoDecouverteActivity.this, DecouverteRoutesListActivity.class);
                        startActivity(intent);
                    }
                });
        btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InfoDecouverteActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public  class FeedInfoList extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<FeedInfo> listaItems;

        public class ViewHolder {
            FrameLayout ItemContainer;
            TextView legende_title;
            ImageButton open_info_legende;
            FrameLayout legende_text;
            TextView legende_description;

            int index;
        }

        public FeedInfoList(Context _ctx, ArrayList<FeedInfo> _items) {
            this.listaItems = _items;
            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (listaItems != null) {
                return listaItems.size();

            } else {
                Log.d("Routes List", "liste vide");
                return 0;
            }
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
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LinearLayout mViewGroup = new LinearLayout(InfoDecouverteActivity.this);

            // Recoger el item
            FeedInfo item = listaItems.get(position);

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.feed_info_adapter, mViewGroup);

                holder.ItemContainer = (FrameLayout) convertView.findViewById(R.id.item_container);
                holder.legende_title = (TextView) convertView.findViewById(R.id.legende_title);
                holder.open_info_legende = (ImageButton) convertView.findViewById(R.id.open_info_legende);
                holder.legende_text = (FrameLayout) convertView.findViewById(R.id.legende_text);
                holder.legende_description = (TextView) convertView.findViewById(R.id.legende_description);
                holder.open_info_legende.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(  holder.legende_text.getVisibility()==View.GONE) {
                                    Log.d("Visiblility", "VISIBLE");
                                    holder.legende_text.setVisibility(View.VISIBLE);
                                    holder.open_info_legende.setImageResource(R.drawable.info_open_b_fondo);

                                    holder.legende_text.setPadding(((7 * holder.open_info_legende.getWidth())/100),holder.open_info_legende.getHeight(),((18 * holder.open_info_legende.getWidth())/100),0);
                                    Log.e("VH", "openInfoLegende.getWidth() = " + holder.open_info_legende.getWidth());
                                    Log.e("VH", "(18 * openInfoLegende.getWidth())/100 = " + (18 * holder.open_info_legende.getWidth())/100);
                                    holder.legende_description.getHeight();
                                }
                                else {
                                    Log.d("Visiblility", "GONE");
                                    holder.legende_text.setVisibility(View.GONE);
                                    holder.open_info_legende.setImageResource(R.drawable.info_closed);
                                }
                            }
                        });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.legende_title.setText(item.getTitle());
            holder.legende_description.setText(Html.fromHtml(item.getBody()));
            return convertView;
        }
    }
}


