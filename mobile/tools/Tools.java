package pl.inz.costshare.mobile.tools;

import android.content.Context;
import android.widget.Toast;

public class Tools {
    public static void info(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static void error(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
