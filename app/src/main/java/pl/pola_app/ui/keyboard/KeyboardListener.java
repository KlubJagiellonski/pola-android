package pl.pola_app.ui.keyboard;

import android.support.annotation.NonNull;

public interface KeyboardListener {
    void onInputConfirmed(@NonNull final String text);
}
