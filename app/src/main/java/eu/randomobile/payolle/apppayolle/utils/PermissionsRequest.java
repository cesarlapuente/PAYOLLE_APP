package eu.randomobile.payolle.apppayolle.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import eu.randomobile.payolle.apppayolle.R;

/**
 * Created by bagneres01 on 30/08/2016.
 */
public class PermissionsRequest {

    // code to identify permission requested
    private static final int MY_PERMISSIONS_REQUEST = 2;

    // this method ask for the permission which is needed and show message if this permission was already denied
    public static void askForPermission(final Activity activity, final String groupPermission) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity, groupPermission) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission Request", "Permission not granted");
            // if permission already denied show an explanation and permit authorization
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, groupPermission)) {
                Log.d("Permission Request", "Permission explanation");
                Toast.makeText(activity, "Permission explanation", Toast.LENGTH_LONG);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.permission_message_explanation)
                        .setPositiveButton(R.string.permission_message_continue, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // redirect to permission request
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{groupPermission}, MY_PERMISSIONS_REQUEST);
                            }
                        })
                        .setNegativeButton(R.string.permission_message_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
                Log.d("Permission Request", "Permission explanation end");
            }
            // else we ask for permission
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, new String[]{groupPermission}, MY_PERMISSIONS_REQUEST);
            }
        }

    }

}
