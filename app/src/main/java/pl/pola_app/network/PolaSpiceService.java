package pl.pola_app.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import pl.pola_app.R;

public class PolaSpiceService extends RetrofitGsonSpiceService {
    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(Api.class);
    }

    @Override
    protected String getServerUrl() {
        return getResources().getString(R.string.pola_api_url);
    }
}
