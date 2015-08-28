package pl.pola_app.internal.di;

import javax.inject.Singleton;

import dagger.Component;
import pl.pola_app.PolaApplication;
import pl.pola_app.network.PolaSpiceService;
import pl.pola_app.ui.activity.MainActivity;

@Singleton
@Component(modules = {NetworkModule.class, SystemServicesModule.class, ScannerViewModule.class})
public interface PolaComponent {

    final class Initializer {
        public static PolaComponent init(PolaApplication app) {
            return DaggerPolaComponent.builder()
                    .systemServicesModule(new SystemServicesModule(app))
                    .scannerViewModule(new ScannerViewModule(app))
                    .build();
        }
    }

    void inject(MainActivity mainActivity);
}
