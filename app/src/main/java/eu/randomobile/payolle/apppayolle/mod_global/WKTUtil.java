package eu.randomobile.payolle.apppayolle.mod_global;

import android.graphics.Color;
import android.util.Log;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import java.util.ArrayList;


public class WKTUtil {





    public static PolylineOptions getPolylineFromWKTLineStringFieldFEED( String wktLineString){
        Log.d("Milog", "Inicio de parseo de wkt a coordenadas");
        PolylineOptions polylineOptions = null ;
        // Quitar la palabra LINESTRING
        String textoSustituido = wktLineString.replace("LINESTRING (", "");

        // Quitar los par�ntesis de cierre ))
        textoSustituido = textoSustituido.replace( ")", "");

        ArrayList<LatLng> puntosPolyline = null;
        Polyline polyline = null;

        String[] separadosComaEspacio = textoSustituido.split(", ");
        if(separadosComaEspacio != null){
            for(int i=0; i<separadosComaEspacio.length; i++){
                String coordStr = separadosComaEspacio[i];
                // Obtener latitud, longitud
                String[] latLonTexto = coordStr.split(" ");
                if(latLonTexto != null){
                    if(latLonTexto.length == 2){
                        String longitud = latLonTexto[0];
                        String latitud = latLonTexto[1];
                        LatLng coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                        if(puntosPolyline == null){
                            puntosPolyline = new ArrayList<LatLng>();
                        }
                        puntosPolyline.add(coordenadas);
                    }
                }
            }
        }

        if(puntosPolyline == null){
            String[] separadosComa = textoSustituido.split(",");
            if(separadosComa != null){
                for(int i=0; i<separadosComa.length; i++){
                    String coordStr = separadosComa[i];
                    // Obtener latitud, longitud
                    String[] latLonTexto = coordStr.split(" ");
                    if(latLonTexto != null){
                        if(latLonTexto.length == 2){
                            String longitud = latLonTexto[0];
                            String latitud = latLonTexto[1];
                            LatLng coordenadas = new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud));
                            if(puntosPolyline == null){
                                puntosPolyline = new ArrayList<LatLng>();
                            }
                            puntosPolyline.add(coordenadas);
                        }
                    }
                }
            }
        }

        Log.d("Milog", "Fin de parseo de wkt a coordenadas");

        Log.d("Milog", "Inicio de proyeccion en polyline");

        if(puntosPolyline != null) {
            polylineOptions = new PolylineOptions()
                    .addAll(puntosPolyline)
                    .color(Color.RED)
                    .width(3f);
        }

//			Log.d("Milog", "Puntos polyline no es nulo y tiene estos elementos: " + puntosPolyline.size());
//			polyline = new Polyline();
//			for(int i=0; i<puntosPolyline.size(); i++){
//				LatLng punto = puntosPolyline.get(i);
//				Point puntoProyectado = GeometryEngine.project(punto.getLongitude(), punto.getLatitude(), app.spatialReference );
//				//Log.d("Milog", "Voy a anadir el siguiente punto al polyline: " + punto.getLatitude() + "  " + punto.getLongitude());
//				if( i == 0){
//					polyline.startPath(puntoProyectado);
//				}else{
//					polyline.lineTo(puntoProyectado);
//				}
//
//			}
//		}else{
//			Log.d("Milog", "Puntos polyline es nulo");
//		}
//
//		Log.d("Milog", "Fin de proyeccion en polyline");

        return polylineOptions;
    }


}
