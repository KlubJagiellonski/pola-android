package pl.pola_app.internal.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

@Module
public class ScannerViewModule {
    private final Context context;

    public ScannerViewModule(Context context) {
        this.context = context;
    }

    @Provides
    ZXingScannerView provideZXingScannerView() {
        return new ZXingScannerView(context);
    }
}
