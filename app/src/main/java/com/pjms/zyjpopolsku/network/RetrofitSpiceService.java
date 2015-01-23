package com.pjms.zyjpopolsku.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class RetrofitSpiceService extends RetrofitGsonSpiceService {
    private final static String BASE_URL = "https://zyjpopolsku.herokuapp.com/api/v1";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(Api.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}
