package pl.pola_app.ui.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import pl.pola_app.R
import pl.pola_app.helpers.EventLogger

/**
 * Simple numeric keypad view containing digit buttons, delete and done.
 */
class NumericKeyboard @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val keyboardInput: EditText
    private var keyboardListener: KeyboardListener? = null
    fun setKeyboardListener(keyboardListener: KeyboardListener?) {
        this.keyboardListener = keyboardListener
    }

    private fun onKeyPressed(keyCode: Int) {
        keyboardInput.onKeyDown(keyCode, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
    }

    private fun onConfirm() {
        val text = keyboardInput.text.toString()
        keyboardListener?.run {
            this.onInputConfirmed(text)
        }
        if (keyboardListener != null) {

        }
    }

    private inner class KeyboardClickListener : OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.key_0 -> onKeyPressed(KeyEvent.KEYCODE_0)
                R.id.key_1 -> onKeyPressed(KeyEvent.KEYCODE_1)
                R.id.key_2 -> onKeyPressed(KeyEvent.KEYCODE_2)
                R.id.key_3 -> onKeyPressed(KeyEvent.KEYCODE_3)
                R.id.key_4 -> onKeyPressed(KeyEvent.KEYCODE_4)
                R.id.key_5 -> onKeyPressed(KeyEvent.KEYCODE_5)
                R.id.key_6 -> onKeyPressed(KeyEvent.KEYCODE_6)
                R.id.key_7 -> onKeyPressed(KeyEvent.KEYCODE_7)
                R.id.key_8 -> onKeyPressed(KeyEvent.KEYCODE_8)
                R.id.key_9 -> onKeyPressed(KeyEvent.KEYCODE_9)
                R.id.key_backspace -> onKeyPressed(KeyEvent.KEYCODE_DEL)
                R.id.key_ok -> onConfirm()
                else -> EventLogger(context).logException(UnsupportedOperationException("Unsupported view: $v"))
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.keyboard_layout, this, true)
        keyboardInput = findViewById<View>(R.id.keyboard_input) as EditText
        val onClickListener = KeyboardClickListener()
        findViewById<View>(R.id.key_0).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_1).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_2).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_3).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_4).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_5).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_6).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_7).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_8).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_9).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_0).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_backspace).setOnClickListener(onClickListener)
        findViewById<View>(R.id.key_ok).setOnClickListener(onClickListener)
    }
}