package pl.pola_app.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.pola_app.ui.activity.MainActivity;
import pl.pola_app.ui.keyboard.KeyboardListener;
import pl.pola_app.ui.keyboard.NumericKeyboard;

public class KeyboardFragment extends Fragment implements KeyboardListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final NumericKeyboard numericKeyboard = new NumericKeyboard(inflater.getContext());
        final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        numericKeyboard.setLayoutParams(layoutParams);
        numericKeyboard.setKeyboardListener(this);

        return numericKeyboard;
    }

    @Override
    public void onInputConfirmed(@NonNull String text) {
        final Activity activity = getActivity();
        ((MainActivity) activity).onBarcode(text, false);
        activity.getFragmentManager().popBackStack();
    }
}
