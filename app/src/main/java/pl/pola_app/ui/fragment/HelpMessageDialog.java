package pl.pola_app.ui.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.pola_app.R;

public class HelpMessageDialog extends DialogFragment {

    public OnWantHelpButtonClickListener onWantHelpButtonClickListener = OnWantHelpButtonClickListener.NULL;



    public static HelpMessageDialog newInstance() {
        return new HelpMessageDialog();
    }

    public void setOnWantHelpButtonClickListener(OnWantHelpButtonClickListener onWantHelpButtonClickListener) {
        this.onWantHelpButtonClickListener = onWantHelpButtonClickListener != null ? onWantHelpButtonClickListener : OnWantHelpButtonClickListener.NULL;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_help_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.help_dialog_want_to_help_button)
    public void onWantToHelpButtonClick(){
        onWantHelpButtonClickListener.onWantHelpButtonClick();
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.help_dialog_next_time_button)
    public void onNextTimeButtonClick(){
        dismiss();
    }

    public interface OnWantHelpButtonClickListener {

        OnWantHelpButtonClickListener NULL = () -> { /*Do nothing */ };

        void onWantHelpButtonClick();
    }
}
