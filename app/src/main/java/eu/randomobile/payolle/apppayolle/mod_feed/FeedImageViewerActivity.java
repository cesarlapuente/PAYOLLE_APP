package eu.randomobile.payolle.apppayolle.mod_feed;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import eu.randomobile.payolle.apppayolle.R;
import eu.randomobile.payolle.apppayolle.mod_global.libraries.bitmap_manager.BitmapManager;
import eu.randomobile.payolle.apppayolle.mod_global.model.ResourceFile;
import eu.randomobile.payolle.apppayolle.mod_imgmapping.ImageMap;

/**
 * Created by 44 screens on 28/09/2016.
 */
public class FeedImageViewerActivity extends Activity {
    public static final String PARAM_KEY_RECURSO = "recurso";

    // Argumentos
    public ResourceFile paramRecurso;
    ImageButton btn_return, btn_home;

    // Controles
    ImageView imageView;
    // Para el tratamiento del menu
    ImageMap mImageMap = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_zoom_imgs);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            paramRecurso = b.getParcelable(PARAM_KEY_RECURSO);
        }
//
//        mImageMap = (ImageMap) findViewById(R.id.galery_gridView);
//        mImageMap.setAttributes(true, false, (float) 1.0, "images");
//        mImageMap.setImageResource(R.drawable.foto);
        // Capturar controles
        this.capturarControles();

        // Escuchar eventos
        this.escucharEventos();

        // Configurar formulario
        this.configurarFormulario();
    }

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();

    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d("Milog", "Cambio la configuracion");
    }

    private void capturarControles() {
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    private void configurarFormulario() {
        if (paramRecurso != null) {
            BitmapManager.INSTANCE.loadBitmap(paramRecurso.getFileUrl(),
                    imageView, 1000, 700);
            if (paramRecurso.getFileBody() != null
                    && !paramRecurso.getFileBody().equals("")) {
                final Typeface font = Typeface.createFromAsset(
                        this.getAssets(), "fonts/Roboto-Light.ttf");
                final TextView txt = (TextView) this
                        .findViewById(R.id.txt_descripcion);
                txt.setTypeface(font);
                //txt.setText(paramRecurso.getFileBody());
                txt.setText(
                        Html.fromHtml(paramRecurso.getFileBody() + "<br>"),
                        TextView.BufferType.SPANNABLE);
                txt.setVisibility(LinearLayout.VISIBLE);
            }
            if (paramRecurso.getFileTitle() != null
                    && !paramRecurso.getFileTitle().equals("")) {
                final Typeface font = Typeface.createFromAsset(
                        this.getAssets(), "fonts/scala-sans-bold.ttf");
                final TextView txt = (TextView) this
                        .findViewById(R.id.txt_titulo);
                txt.setTypeface(font);
                txt.setText(paramRecurso.getFileTitle());
                txt.setVisibility(LinearLayout.VISIBLE);
            }
            if (paramRecurso.getCopyright() != null && !paramRecurso.getCopyright().equals("")) {
                String copyright = paramRecurso.getCopyright();
                if (!copyright.contains("\u00a9")) {
                    copyright = "\u00a9 " + copyright;
                }
                final Typeface font = Typeface.createFromAsset(
                        this.getAssets(), "fonts/scala-sans-bold.ttf");
                final TextView txt = (TextView) this
                        .findViewById(R.id.txt_copyright);
                txt.setTypeface(font);
                txt.setText(copyright);
                txt.setVisibility(LinearLayout.VISIBLE);
            }
        } else {
            //Util.mostrarMensaje(ImageViewerActivity.this, "Sin imagen",
             //       "No hay imagen que mostrar");
        }

    }



    private void escucharEventos() {

    }
}
