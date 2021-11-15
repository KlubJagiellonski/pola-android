package pl.pola_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import pl.pola_app.databinding.DialogHelpInfoBinding;

public class HelpMessageDialog extends DialogFragment {

    private DialogHelpInfoBinding binding;

    public OnWantHelpButtonClickListener onWantHelpButtonClickListener = OnWantHelpButtonClickListener.NULL;

    public static HelpMessageDialog newInstance() {
        return new HelpMessageDialog();
    }

    public void setOnWantHelpButtonClickListener(OnWantHelpButtonClickListener onWantHelpButtonClickListener) {
        this.onWantHelpButtonClickListener =
                onWantHelpButtonClickListener != null ? onWantHelpButtonClickListener : OnWantHelpButtonClickListener.NULL;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DialogHelpInfoBinding.inflate(inflater, container, false);
        binding.helpDialogWantToHelpButton.setOnClickListener(this::onWantToHelpButtonClick);
        binding.helpDialogNextTimeButton.setOnClickListener(this::onNextTimeButtonClick);
        return binding.getRoot();
    }

    public void onWantToHelpButtonClick(View view) {
        onWantHelpButtonClickListener.onWantHelpButtonClick();
        dismiss();
    }

    public void onNextTimeButtonClick(View view) {
        dismiss();
    }

    public interface OnWantHelpButtonClickListener {

        OnWantHelpButtonClickListener NULL = () -> { /*Do nothing */ };

        void onWantHelpButtonClick();
    }
}
