package pl.inz.costshare.mobile.service;

import android.app.Activity;
import android.util.Log;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Response;

public abstract class ResponseHandler<T> {

    private Activity activity = null;

    public ResponseHandler(Activity activity) {
        this.activity = activity;
    }

    public void handleResponse(final T data, final Response response, final FuelError error) {
        Log.d(this.getClass().getName(), "handleResponse start");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (error == null) {
                    success(data);
                } else {
                    Log.e("ResponseHandler", Log.getStackTraceString(error.getException()));
                    String errorMsg = new String(response.getData());
                    if (errorMsg == null || errorMsg.trim().length() == 0) {
                        errorMsg = "Error !";
                    }
                    failure(error, errorMsg);
                }
            }
        });
        Log.d(this.getClass().getName(), "handleResponse end");
    }

    public abstract void success(T value);

    public abstract void failure(FuelError error, String message);

}
