package pl.pola_app.internal.di;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import pl.pola_app.PolaApplication;
import pl.pola_app.ui.activity.MainActivity;
import pl.pola_app.ui.activity.VideoMessageActivity;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ScannerFragment;
import pl.pola_app.ui.fragment.VideoCaptureFragment;

@Singleton
@Component(modules = {OttoModule.class, WidgetModule.class, SharedPrefsModule.class})
public interface PolaComponent {

    final class Initializer {

        public static PolaComponent init(PolaApplication app) {
            return DaggerPolaComponent.builder()
                    .widgetModule(new WidgetModule(app))
                    .sharedPrefsModule(new SharedPrefsModule(app))
                    .build();
        }

    }
    void inject(MainActivity mainActivity);
    void inject(ScannerFragment scannerFragment);
    void inject(VideoCaptureFragment videoCaptureFragment);
    void inject(ProductDetailsFragment productDetailsFragment);
    void inject(VideoMessageActivity videoMessageActivity);
}
