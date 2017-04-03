package eu.randomobile.payolle.apppayolle.mod_imgmapping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by 44Screens on 2017-03-31.
 */

public class DbBitmapUtility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
//        Bitmap.Config configBmp = Bitmap.Config.valueOf(bitmap.getConfig().name());
//        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
//        ByteBuffer buffer = ByteBuffer.wrap(image);
//        bitmap_tmp.copyPixelsFromBuffer(buffer);
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap getImage(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String root = Environment.getExternalStorageDirectory().toString();
        return BitmapFactory.decodeFile(root +"/saved_images/"+fileName, options);
    }

    public static String SaveImage(Bitmap finalBitmap, String id) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String fname = "Image-"+ id +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fname;
    }
}
