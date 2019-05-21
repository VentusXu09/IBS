package com.ventus.ibs.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.ventus.ibs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ventus0905 on 04/01/2019
 */

public class PermissionHelper {

        public static final int PERMISSION_REQ_CODE = 123;
        private static final String TAG = "PermissionManager";
        private static final String KEY = "Perm";

        public interface PermissionListener {
            // when user allow permission
            void onPermissionGranted(@NonNull String... permission);

            // when user denied permission
            void onPermissionDenied(@NonNull String permission);

            void onPermissionDisable(@NonNull String permission);

        }

        private static boolean shouldAskPermission() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        }

        /**
         * check whether permission need to be ask
         * also check whether user checked "never ask"
         */

        @RequiresApi(api = Build.VERSION_CODES.M)
        private static boolean shouldAskPermission(@NonNull Activity activity, List<String> permissionList, @NonNull String permission) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    return false;
                }
            }
            return true;
        }

        public static void checkPermissions(@NonNull Activity activity, @NonNull PermissionListener listener, @NonNull String... permissions) {
            List<String> permissionList = new ArrayList<>();
            List<String> permissionsNeeded = new ArrayList<>();
            int size = permissions.length;
            for (String p : permissions) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldAskPermission(activity, permissionList, p)) {
                        permissionsNeeded.add(p);
                    }
                } else {
                    if (size == 1) {
                        listener.onPermissionGranted(p);
                    }else {
                        size--;
                    }
                }
            }
            if (permissionList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]));
                    return;
                }
                requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]));
                return;
            }
            listener.onPermissionGranted(permissions);
        }

        private static void requestPermissions(Activity activity, String[] strings) {
            ActivityCompat.requestPermissions(activity, strings, PERMISSION_REQ_CODE);
        }

        public static void onRequestPermissionResult(@NonNull PermissionListener listener, @NonNull String[] permissions, @NonNull int[] results) {
            if (shouldAskPermission()) {
                if (permissions.length == 0 && results.length == 0) {
                    return;
                }
                for (int i = 0; i < permissions.length; i++) {
                    if (results[i] == PackageManager.PERMISSION_DENIED) {
                        listener.onPermissionDenied(permissions[i]);
                        return;
                    }
                }
                listener.onPermissionGranted(permissions);
            }
        }

        /**
         * if user check never ask again then show dialog
         */
        public static void showPermissionDialog(@NonNull final Activity activity, @NonNull final String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message)
                    .setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goToSetting(activity);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private static void goToSetting(Activity activity) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }

        /**
         *
         * @param context the context of the activity,fragment of the app
         * @return a list of all granted permission for the app
         */
        public static @NonNull List<String> getGrantedPermissions(Context context) {
            List<String> granted = new ArrayList<>();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
                for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                    if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        granted.add(packageInfo.requestedPermissions[i]);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Granted permissions error", e);
            }
            return granted;
        }


}
