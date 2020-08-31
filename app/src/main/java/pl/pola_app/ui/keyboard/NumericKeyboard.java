package pl.pola_app.ui.keyboard;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import pl.pola_app.R;
import pl.pola_app.helpers.EventLogger;

/**
 * Simple numeric keypad view containing digit buttons, delete and done.
 */
public class NumericKeyboard extends LinearLayout {
    private final EditText keyboardInput;
    @Nullable
    private KeyboardListener keyboardListener;

    public NumericKeyboard(Context context) {
        this(context, null, 0);
    }

    public NumericKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumericKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.keyboard_layout, this, true);
        keyboardInput = (EditText) findViewById(R.id.keyboard_input);

        final KeyboardClickListener onClickListener = new KeyboardClickListener();
        findViewById(R.id.key_0).setOnClickListener(onClickListener);
        findViewById(R.id.key_1).setOnClickListener(onClickListener);
        findViewById(R.id.key_2).setOnClickListener(onClickListener);
        findViewById(R.id.key_3).setOnClickListener(onClickListener);
        findViewById(R.id.key_4).setOnClickListener(onClickListener);
        findViewById(R.id.key_5).setOnClickListener(onClickListener);
        findViewById(R.id.key_6).setOnClickListener(onClickListener);
        findViewById(R.id.key_7).setOnClickListener(onClickListener);
        findViewById(R.id.key_8).setOnClickListener(onClickListener);
        findViewById(R.id.key_9).setOnClickListener(onClickListener);
        findViewById(R.id.key_0).setOnClickListener(onClickListener);
        findViewById(R.id.key_backspace).setOnClickListener(onClickListener);
        findViewById(R.id.key_ok).setOnClickListener(onClickListener);
    }

    public void setKeyboardListener(@Nullable KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    private void onKeyPressed(int keyCode) {
        keyboardInput.onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
    }

    private void onConfirm() {
        final String text = keyboardInput.getText().toString();
        if (keyboardListener != null) {
            keyboardListener.onInputConfirmed(text);
        }
    }

    private class KeyboardClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.key_0:
                    onKeyPressed(KeyEvent.KEYCODE_0);
                    break;
                case R.id.key_1:
                    onKeyPressed(KeyEvent.KEYCODE_1);
                    break;
                case R.id.key_2:
                    onKeyPressed(KeyEvent.KEYCODE_2);
                    break;
                case R.id.key_3:
                    onKeyPressed(KeyEvent.KEYCODE_3);
                    break;
                case R.id.key_4:
                    onKeyPressed(KeyEvent.KEYCODE_4);
                    break;
                case R.id.key_5:
                    onKeyPressed(KeyEvent.KEYCODE_5);
                    break;
                case R.id.key_6:
                    onKeyPressed(KeyEvent.KEYCODE_6);
                    break;
                case R.id.key_7:
                    onKeyPressed(KeyEvent.KEYCODE_7);
                    break;
                case R.id.key_8:
                    onKeyPressed(KeyEvent.KEYCODE_8);
                    break;
                case R.id.key_9:
                    onKeyPressed(KeyEvent.KEYCODE_9);
                    break;
                case R.id.key_backspace:
                    onKeyPressed(KeyEvent.KEYCODE_DEL);
                    break;
                case R.id.key_ok:
                    onConfirm();
                    break;
                default:
                    new EventLogger(getContext()).logException(new UnsupportedOperationException("Unsupported view: " + v.toString()));
            }
        }
    }
}
