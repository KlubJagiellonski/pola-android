package pl.pola_app.internal.di;

import javax.inject.Singleton;

import dagger.Component;
import pl.pola_app.PolaApplication;
import pl.pola_app.ui.activity.MainActivity;
import pl.pola_app.ui.fragment.ProductDetailsFragment;
import pl.pola_app.ui.fragment.ProductsListFragment;
import pl.pola_app.ui.fragment.ScannerFragment;

@Singleton
@Component(modules = {NetworkModule.class, AppModule.class, OttoModule.class})
public interface PolaComponent {

    final class Initializer {
        public static PolaComponent init(PolaApplication app) {
            return DaggerPolaComponent.builder()
                    .appModule(new AppModule(app))
                    .build();
        }
    }

    void inject(MainActivity mainActivity);
    void inject(ScannerFragment scannerFragment);
    void inject(ProductsListFragment productsListFragment);
    void inject(ProductDetailsFragment productDetailsFragment);
}
