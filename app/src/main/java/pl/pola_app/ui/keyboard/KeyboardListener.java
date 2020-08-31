package pl.pola_app.ui.keyboard;

import androidx.annotation.NonNull;

public interface KeyboardListener {
    void onInputConfirmed(@NonNull final String text);
}
