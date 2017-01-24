package pl.chipsoft.gesturewand.logic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by macie on 24.01.2017.
 */

public final class AppUtils {
    public static boolean openApp(Context context, String appName) {
        PackageManager manager = context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(getPackageName(context, appName));
        if (intent == null) {
            return false;
        }
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(intent);

        return true;
    }

    public static String getPackageName(Context context, String appName){
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        return Stream.of(packages).filter(p -> p.applicationInfo.loadLabel(
                context.getPackageManager()).toString().equals(appName)).map(pa -> pa.packageName).
                findFirst().get();
    }

    public static void call(Activity activity, String phone){
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        activity.startActivity(callIntent);
    }
}
