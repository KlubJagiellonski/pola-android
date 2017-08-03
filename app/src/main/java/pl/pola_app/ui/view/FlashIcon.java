package pl.pola_app.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;

import pl.pola_app.R;

public class FlashIcon extends android.support.v7.widget.AppCompatImageView {
    private boolean isFlashOn;
    private FlashIconStateListener flashIconStateListener;

    public FlashIcon(Context context) {
        super(context);
        setOnClickListener(v -> toggleFlash());
    }

    public void attachToToolbar(@NonNull Toolbar toolbar) {
        final Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        params.width = getResources().getDimensionPixelSize(R.dimen.flash_icon_size);
        params.height = getResources().getDimensionPixelSize(R.dimen.flash_icon_size);
        toolbar.addView(this, params);
    }

    public void setFlashOn() {
        setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_flash_off_white_48dp));
        if (flashIconStateListener != null && !isFlashOn) {
            flashIconStateListener.onFlashOn();
        }
        isFlashOn = true;
    }

    public void setFlashOff() {
        setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_flash_on_white_48dp));
        if (flashIconStateListener != null && isFlashOn) {
            flashIconStateListener.onFlashOff();
        }
        isFlashOn = false;
    }

    public void toggleFlash() {
        if (isFlashOn) {
            setFlashOff();
        } else {
            setFlashOn();
        }
    }

    public void setFlashIconStateListener(FlashIconStateListener flashIconStateListener) {
        this.flashIconStateListener = flashIconStateListener;
    }

    public interface FlashIconStateListener {
        void onFlashOn();

        void onFlashOff();
    }

}
