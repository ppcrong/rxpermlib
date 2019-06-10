package com.ppcrong.rxpermlib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * RxPermission Library
 */
public class RxPermLib {

    public static final int SETTINGS_REQ_CODE = 16061;

    // region [Interface]
    public interface CheckPermListener {
        void grantedAll();
    }
    // endregion [Interface]

    // region [Public Function]
    public static void checkPermissions(AppCompatActivity activity,
                                        CheckPermListener listener,
                                        String... permissions) {

        // RxPermissions
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.setLogging(true);

        rxPermissions
                .requestEachCombined(permissions)
                .subscribe(permission -> {

                    KLog.i(permission);
                    if (permission.granted) {

                        // All permissions are granted !
                        KLog.i("All permissions are granted !");

                        listener.grantedAll();
                    } else if (permission.shouldShowRequestPermissionRationale) {

                        // At least one denied permission without ask never again
                        KLog.i("At least one denied permission without ask never again");

                        new AlertDialog.Builder(activity)
                                .setMessage(R.string.rationale)
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                                    // request again
                                    checkPermissions(activity, listener, permissions);
                                })
                                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                                    // back
                                    activity.onBackPressed();
                                })
                                .create()
                                .show();
                    } else {

                        // At least one denied permission with ask never again
                        // Need to go to the settings
                        KLog.i("At least one denied permission with ask never again");
                        KLog.i("Need to go to the settings");

                        new AlertDialog.Builder(activity)
                                .setMessage(R.string.perm_tip)
                                .setPositiveButton(R.string.setting, (dialog, which) -> {

                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                    intent.setData(uri);
                                    startAppSettingsScreen(activity, intent);
                                })
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                                    // back
                                    activity.onBackPressed();
                                })
                                .create()
                                .show();
                    }
                });
    }
    // endregion [Public Function]

    // region [Private Function]
    @TargetApi(11)
    private static void startAppSettingsScreen(Object object,
                                               Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }
    }
    // endregion [Private Function]
}
