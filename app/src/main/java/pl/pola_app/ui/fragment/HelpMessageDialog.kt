package pl.pola_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import butterknife.ButterKnife
import butterknife.OnClick
import pl.pola_app.R

class HelpMessageDialog : DialogFragment() {
    private var onWantHelpButtonClickListener = OnWantHelpButtonClickListener.NULL
    fun setOnWantHelpButtonClickListener(onWantHelpButtonClickListener: OnWantHelpButtonClickListener?) {
        this.onWantHelpButtonClickListener =
            onWantHelpButtonClickListener ?: OnWantHelpButtonClickListener.NULL
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_help_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    @OnClick(R.id.help_dialog_want_to_help_button)
    fun onWantToHelpButtonClick() {
        onWantHelpButtonClickListener.onWantHelpButtonClick()
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @OnClick(R.id.help_dialog_next_time_button)
    fun onNextTimeButtonClick() {
        dismiss()
    }

    interface OnWantHelpButtonClickListener {
        fun onWantHelpButtonClick()

        companion object {
            val NULL: OnWantHelpButtonClickListener = object : OnWantHelpButtonClickListener{
                override fun onWantHelpButtonClick() {

                }

            }
        }
    }

    companion object {
        fun newInstance(): HelpMessageDialog {
            return HelpMessageDialog()
        }
    }
}