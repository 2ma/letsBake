package hu.am2.letsbake;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    public static final String EXTRA_RECIPE_ID = "hu.am2.letsbake.extra.RECIPE_ID";
    public static final String EXTRA_STEP_POSITION = "hu.am2.letsbake.extra.STEP_POSITION";

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
