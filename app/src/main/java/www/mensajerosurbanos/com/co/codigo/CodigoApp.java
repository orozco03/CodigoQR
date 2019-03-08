package www.mensajerosurbanos.com.co.codigo;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;

public class CodigoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }
}
