package org.noorganization.instalist.view.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.widget.EditText;

import org.noorganization.instalist.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by tinos_000 on 18.05.2015.
 */
public class ViewUtils {

    /**
     * NUMBERS_AND_SEPARATOR is a workaround for a Bug in EditText's, existing since Android 1.5!
     * Accept local decimal separator and dot as decimal separator - for usability and compatibility
     * reasons.
     * {@see https://code.google.com/p/android/issues/detail?id=2626}
     */
    private static final String NUMBERS_AND_SEPARATOR = "0123456789." +
            DecimalFormatSymbols.getInstance().getDecimalSeparator();

    /**
     * Checks if the given EditText is filled with some text. If it is not filled then there
     * will be set a error message.
     * @param _EditText the EditText that should be tested.
     * @return true, if filled, false if not.
     */
    public static boolean checkEditTextIsFilled(EditText _EditText){
        if(_EditText.length() == 0
                || (_EditText.getText().toString().replaceAll("(\\s)*","").length() == 0) ){

            _EditText.setError("Not filled");
            return false;
        }

        _EditText.setError(null);
        return true;
    }

    public static KeyListener getNumberListener() {
        return DigitsKeyListener.getInstance(NUMBERS_AND_SEPARATOR);
    }

    public static String formatFloat(float _toConvert) {
        NumberFormat formatter = new DecimalFormat("#.###");
        return formatter.format(_toConvert);
    }

    public static float parseFloatFromLocal(String _toConvert) {
        String toConvert = "0" + _toConvert.
                replace('.', DecimalFormatSymbols.getInstance().getDecimalSeparator());
        NumberFormat formatter = DecimalFormat.getInstance();
        try {
            return formatter.parse(toConvert).floatValue();
        } catch (Exception _notUsed) {
            return 0.0f;
        }
    }

    public static void addFragment(Activity _activity, Fragment _newFragment) {
        FragmentManager fragmentManager = _activity.getFragmentManager();
        String canonicalName = _newFragment.getClass().getCanonicalName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(canonicalName);
        if (oldFragment != null) {
            fragmentManager.popBackStack(canonicalName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.remove(oldFragment);
        }
        if (_newFragment instanceof DialogFragment) {
            ((DialogFragment) _newFragment).show(transaction, canonicalName);
        } else {
            transaction.addToBackStack(canonicalName);
            transaction.add(R.id.container, _newFragment, canonicalName);
            transaction.commit();
        }
    }

    public static void removeFragment(Activity _activity, Fragment _oldFragment) {
        FragmentManager fragmentManager = _activity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(_oldFragment.getClass().getCanonicalName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.remove(_oldFragment);
        transaction.commit();
    }
}
