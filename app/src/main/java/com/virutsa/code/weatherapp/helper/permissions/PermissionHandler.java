package com.virutsa.code.weatherapp.helper.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.virutsa.code.weatherapp.R;
import com.virutsa.code.weatherapp.WeatherActivity;
import com.virutsa.code.weatherapp.constants.Constant;
import com.virutsa.code.weatherapp.helper.notification.NotificationBuilder;
import com.virutsa.code.weatherapp.helper.notification.NotificationChannels;
import com.virutsa.code.weatherapp.util.Logger;


import java.util.ArrayList;
import java.util.List;


/**
 * Since API level 23 some permissions have to be requested during runtime.
 */
@TargetApi(23)
public class PermissionHandler {

    // TODO: refactor to not hold backreference to context
    @NonNull
    private Context context;

    public static final String TAG = PermissionHandler.class.getSimpleName();
    private final int REQUEST_CODE = 726;
    private static final int NOTIFICATION_ID = "permission".hashCode();
    final static String GROUP_KEY_GUEST = "permission_key_test";

    public PermissionHandler(Context context) {
        this.context = context;
    }


    /**
     * Check if a permission is granted by its name
     *
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check if a permission is granted by using the Permission Enum
     *
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(Permission permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        return (context.checkSelfPermission(permission.getName()) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check if ALL permissions in the Permission Enum are granted
     *
     * @return
     */
    public boolean isAllPermissionsGranted() {
        boolean isGranted = true;
        for (Permission permission : Permission.values()) {
            isGranted = isGranted && isPermissionGranted(permission.getName());
            Log.d(TAG, "isAllPermissionsGranted: " + permission.getName());
        }
        return isGranted;
    }

    /**
     * Check and ask for all Permissions that are marked as crucial
     *
     * @param activity
     */
    public void checkAndAskForCrucialPermissions(Activity activity) {
        Log.d(TAG, "checking crucial permissions");
        List<Permission> permissionList = getCrucialPermissionsList();
        if (permissionList.size() == 0) {
            hideNotification(activity);
            return;
        }

        String[] requiredPermissionNames = new String[permissionList.size()];

        for (int i = 0; i < permissionList.size(); i++) {
            requiredPermissionNames[i] = permissionList.get(i).getName();
        }

        activity.requestPermissions(requiredPermissionNames, REQUEST_CODE);
    }

    /**
     * Ask for all permissions in the Permission Enum
     */
    public void askForAllPermissions(final Activity activity) {
        List<Permission> requiredPermissions = getAllRequiredPermissions();
        if (requiredPermissions.size() == 0) {
            Logger.log(TAG, "All permissions granted");
            return;
        }

        String[] requiredPermissionNames = new String[requiredPermissions.size()];
        for (int i = 0; i < requiredPermissions.size(); i++) {
            requiredPermissionNames[i] = requiredPermissions.get(i).getName();
        }

        Logger.log(TAG, "Asking for permissions " + requiredPermissionNames);

        activity.requestPermissions(requiredPermissionNames, REQUEST_CODE);
    }

    /**
     * Ask for a specific Permission
     *
     * @param permission
     */
    public void askForPermission(Permission permission, final Activity activity) {
        String[] permissions = new String[1];
        permissions[0] = permission.getName();

        try{
        if (Build.VERSION.SDK_INT > 23 && activity.shouldShowRequestPermissionRationale(permission.getName())) {
            showPermissionRationale(activity);
        } else {
                activity.requestPermissions(permissions, REQUEST_CODE); }
        }
        catch (NoSuchMethodError e) {
            // A lenovo device did not have should showRequestRationale on API level 23...
           // Crashlytics.logException(e);
        }
    }

    /**
     * Ask for all permissions in the Permission Enum from the background
     *
     * @param context
     */
    public void askForAllPermissionsFromBackground(Context context) {

       /* if (!isRegistered(context)) {
            return;
        }*/
        String permission = "android.permission.CALL_PHONE";
        int res = context.checkCallingOrSelfPermission(permission);
//        Log.d("TAG",res == PackageManager.PERMISSION_GRANTED);
        if (res != PackageManager.PERMISSION_GRANTED) {
        } else {
            NotificationCompat.Builder builder =
                    new NotificationBuilder(context)
                            .setContentTitle(context.getString(R.string.permission_notification_title))
                            .setGroup(Constant.NOTIFICATION_KEY)
                            .setGroupSummary(true)
                            .setChannelId(NotificationChannels.notificationChannelId)
                            .setContentText(context.getString(R.string.permission_notification_text));
            Intent resultIntent = new Intent(context, WeatherActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(WeatherActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Get a list of all permissions that are crucial for the functionality of the app and not yet granted
     *
     * @return A list of all permissions that are crucial for funtionality and not granted
     */
    public List<Permission> getCrucialPermissionsList() {
        ArrayList<Permission> permissionList = new ArrayList<>();

        // no runtime permissions needed prior to Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return permissionList;
        }

        for (Permission permission : Permission.values()) {
            if (!isPermissionGranted(permission.getName()) && permission.isRequired()) {
                permissionList.add(permission);
                Log.d(TAG, "Permission missing:" + permission.getName());
            }
        }

        return permissionList;
    }


    private List<Permission> getAllRequiredPermissions() {

        ArrayList<Permission> permissionList = new ArrayList<>();

        // no runtime permissions needed prior to Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return permissionList;
        }

        for (Permission permission : Permission.values()) {
            if (!isPermissionGranted(permission.getName())) {
                permissionList.add(permission);
            }
        }

        return permissionList;
    }

    private boolean shouldRationaleBeShown(Activity activity) {
        boolean rationale = false;
        for (Permission permission : Permission.values()) {
            rationale = rationale || activity.shouldShowRequestPermissionRationale(permission.getName());
        }
        return rationale;
    }

    private void showPermissionRationale(final Activity activity) {

        StringBuilder stringBuilder = new StringBuilder();
        for (Permission permission : Permission.values()) {
            if (!isPermissionGranted(permission.getName())) {
                stringBuilder.append("\u2022");
                stringBuilder.append(activity.getString(R.string.perm_rationale)
                        .replace("$perm_name", activity.getString(permission.getNameResource()))
                        .replace("$perm_explan", activity.getString(permission.getExplanationResource())));
                stringBuilder.append("\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(context.getString(R.string.ok) , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                askForAllPermissions(activity);
            }
        });

        builder.setMessage(stringBuilder.toString());
        builder.create().show();
    }


    private boolean isCrucialPermissionMissing() {
        return getCrucialPermissionsList().size() > 0;
    }

    /**
     * This should be called from the activity callback, blocks the app if a crucial permission is denied
     ** @return
     */
    public boolean checkCrucialPermissionsGranted(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            // force only permissions that are required for app functionality
            if (!isCrucialPermissionMissing()) {
                hideNotification(context);
                return true;
            } else {
                if (!shouldRationaleBeShown(activity)) {
                    //User revoked permissions permanently, lock the app and show the error activity
                    Intent intent = new Intent(activity, WeatherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);

                } else {
                    // User revoked permissions, but not permanently, let's explain things and ask again
                    showPermissionRationale(activity);
                }
            }
        }
        return false;
    }


   /* private boolean (Context context) {
        return ( context.getApplicationContext()).getAppScopeComponents().getStateManager().isRegistered(context);
    }
*/
    private void hideNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}