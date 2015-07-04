package org.noorganization.instalist.view.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.inputmethod.InputMethodManager;
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
        if (_newFragment instanceof DialogFragment) {
            ((DialogFragment) _newFragment).show(transaction, canonicalName);
        } else{
            transaction.addToBackStack(canonicalName);
            transaction.replace(R.id.container, _newFragment, canonicalName);
            transaction.commit();
        }
    }

    public static void removeFragment(Activity _activity, Fragment _oldFragment) {
        FragmentManager fragmentManager = _activity.getFragmentManager();
        fragmentManager.popBackStack(_oldFragment.getClass().getCanonicalName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Validate the Input of the given EditText and retrieve its value.
     * @param _Context context of the app.
     * @param _EditText the EditText with the want data.
     * @return the inout string of the EditText field.
     */
    public static String validateAndGetResultEditText(Context _Context, EditText _EditText){
        _EditText.setError(null);
        if (! ViewUtils.checkEditTextIsFilled(_EditText)) {
            _EditText.setError(_Context.getString(R.string.drawer_layout_custom_no_input));
            return null;
        }
        return _EditText.getText().toString();
    }

    public static void removeSoftKeyBoard(Context _Context, EditText _EditText){
        InputMethodManager imm = (InputMethodManager) _Context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_EditText.getWindowToken(), 0);
    }
}
