package pl.pola_app.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import pl.pola_app.Config;
import roboguice.util.temp.Ln;

public class PolaSpiceService extends RetrofitGsonSpiceService {
    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(Api.class);
        Ln.getConfig().setLoggingLevel(Config.SPICE_LOG_LEVEL);
    }

    @Override
    protected String getServerUrl() {
        return Config.POLA_API_URL;
    }
}
