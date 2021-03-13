package pl.pola_app.ui.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.pola_app.ui.activity.MainActivity
import pl.pola_app.ui.keyboard.KeyboardListener
import pl.pola_app.ui.keyboard.NumericKeyboard

class KeyboardFragment : Fragment(), KeyboardListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val numericKeyboard = NumericKeyboard(inflater.context)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        numericKeyboard.layoutParams = layoutParams
        numericKeyboard.setKeyboardListener(this)
        return numericKeyboard
    }

    override fun onInputConfirmed(text: String) {
        val activity = activity
        (activity as MainActivity).onBarcode(text, false)
        activity.getFragmentManager().popBackStack()
    }
}