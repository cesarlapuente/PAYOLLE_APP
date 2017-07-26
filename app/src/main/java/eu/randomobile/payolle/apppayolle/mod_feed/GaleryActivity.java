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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.randomobile.payolle.apppayolle.MainApp;
import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourceFile;
import eu.randomobile.payolle.apppayolle.mod_global.model.Route;
import eu.randomobile.payolle.apppayolle.utils.ContextWrapper;

public class GaleryActivity extends Activity {


    public static final String PARAM_KEY_ARRAY_RESOURCES = "array_recursos";
    public static final String PARAM_KEY_ROUTE_NID = "route_nid";

    MainApp app;
    GridView gridView;
    private ArrayList<ResourceFile> ImgArray;
    GalleryAdapter adapter;
    private ImageButton btn_home;
    private ImageButton btn_return;
    TextView emptyListView;

    Route route;
    String paramNid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_galery);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            ImgArray = b.getParcelableArrayList(PARAM_KEY_ARRAY_RESOURCES);
        }

        app = (MainApp) getApplication();

        this.capturarControles();
        this.escucharEventos();
        this.configurarFormulario();
    }

    private void capturarControles() {
        gridView = (GridView) findViewById(R.id.galery_gridView);
        emptyListView = (TextView) findViewById(android.R.id.empty);
        gridView.setAdapter(adapter);

        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_return = (ImageButton) findViewById(R.id.btn_return);

    }

    private void configurarFormulario() {

        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramNid = b.getString(PARAM_KEY_ROUTE_NID);
            for(Route r : app.getRoutesListDE()){
                if(r.getNid().equals(paramNid)){
                    this.route = r ;
                }
            }
        }
        if (ImgArray != null) {

            Log.d("Milog", "Numero de imagenes: " + ImgArray.size());

            adapter = new GalleryAdapter(this, ImgArray);
            gridView.setAdapter(adapter);
            try {
                adapter.notifyDataSetChanged();
            } catch (Exception ex) {
                Log.d("Milog", "Excepcion en notify dataSet imagenes changed "
                        + ex.toString());
            }

        } else {
            emptyListView.setVisibility(View.VISIBLE);
            //Util.mostrarMensaje(GridImagesActivity.this, "Sans images","Pas de images pour montrer");
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = ContextWrapper.wrap(newBase, MainApp.locale);
        super.attachBaseContext(context);
    }


    private void escucharEventos() {

        btn_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GaleryActivity.this.finish();
                    }
                });
        btn_home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GaleryActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

    }


    public class GalleryAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context ctx;
        private ArrayList<ResourceFile> listImage;

        class ViewHolder {
            ImageView imageview;
            int index;
        }

        public GalleryAdapter(Context _ctx, ArrayList<ResourceFile> _items) {
            this.listImage = _items;
            this.ctx = _ctx;
            mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return ImgArray.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.feed_activity_galery_adapter, null);

                holder.imageview = (ImageView) convertView.findViewById(R.id.galery_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Recoger el recurso
            final ResourceFile rec = ImgArray.get(position);

            if (rec != null) {
                Log.d("Milog", "Imagen: " + rec.getFileUrl());
                BitmapManager.INSTANCE.loadBitmap(rec.getFileUrl(),
                        holder.imageview, 100, 70);
            }

            holder.imageview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(GaleryActivity.this,
                            FeedImageViewerActivity.class);
                    intent.putExtra(FeedImageViewerActivity.PARAM_KEY_RECURSO, rec);
                    BitmapManager.INSTANCE.cache.remove(rec.getFileUrl());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}


