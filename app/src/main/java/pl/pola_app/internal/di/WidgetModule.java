package pl.pola_app.internal.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.pola_app.helpers.ProductsListLinearLayoutManager;

@Module
public class WidgetModule {
    private final Context context;

    public WidgetModule(Context context) {
        this.context = context;
    }

    @Provides
    ProductsListLinearLayoutManager provideProductsListLinearLayoutManager() {
        return new ProductsListLinearLayoutManager(context);
    }
}
