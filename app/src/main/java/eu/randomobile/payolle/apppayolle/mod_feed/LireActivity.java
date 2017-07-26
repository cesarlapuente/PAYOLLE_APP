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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.utils.ContextWrapper;

public class LireActivity extends Activity {

    private MainApp app;
    private ImageButton openInfoRecit;
    private ImageView openInfoAutres;
    private FrameLayout recitText;
    private FrameLayout autresText;
    private TextView recitDescription;
    private TextView recitTitle;

    private ImageButton btn_home;
    private ImageButton btn_return;

    private ImageButton btn_read;
    private ImageButton btn_game;
    private ImageButton btn_info;
    //private ImageButton btn_badges;

    FeedLireAdapter listLireAdapter;
    LinearLayout list_linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.app = (MainApp) getApplication();

        setContentView(R.layout.feed_activity_lire);

        listLireAdapter = new FeedLireAdapter(this, app.getInfoListLI());
        list_linearlayout = (LinearLayout)findViewById(R.id.list_lire_layout);
        final int adapterCount = listLireAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = listLireAdapter.getView(i, null, null);
            list_linearlayout.addView(item);
        }
        capturarControles();


        escucharEventos();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase, MainApp.locale);
        super.attachBaseContext(context);
    }


    private void capturarControles() {
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);

        btn_read = (ImageButton) findViewById(R.id.btn_footer_read);
        btn_game = (ImageButton) findViewById(R.id.btn_footer_game);
        btn_info = (ImageButton) findViewById(R.id.btn_footer_info);
        //btn_badges = (ImageButton) findViewById(R.id.btn_footer_passport);

    }

    private void escucharEventos() {




        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LireActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LireActivity.this.finish();
                    }
                });


        /*btn_read.setOnClickListener(new View.OnClickListener() { //Not usefull, we are already here
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LireActivity.this, LireActivity.class);
                startActivity(intent);
            }
        });*/
        btn_game.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LireActivity.this, GameActivity.class);
                        startActivity(intent);
                    }
                });
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LireActivity.this, InfoDecouverteActivity.class);
                startActivity(intent);
            }
        });
        /*btn_badges.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LireActivity.this, BadgesActivity.class);
                        startActivity(intent);
                    }
                });*/
    }
    public  class FeedLireAdapter extends BaseAdapter {
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

        public FeedLireAdapter(Context _ctx, ArrayList<FeedInfo> _items) {
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
            LinearLayout mViewGroup = new LinearLayout(LireActivity.this);

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
            holder.legende_description.setText(item.getBody());
            return convertView;
        }
    }
}


