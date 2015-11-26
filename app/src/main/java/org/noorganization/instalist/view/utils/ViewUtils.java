package org.noorganization.instalist.view.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.noorganization.instalist.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;

/**
 * Some utility-functions.
 * <p/>
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
     *
     * @param _EditText the EditText that should be tested.
     * @return true, if filled, false if not.
     */
    public static boolean checkEditTextIsFilled(EditText _EditText) {
        if (_EditText.length() == 0
                || (_EditText.getText().toString().replaceAll("(\\s)*", "").length() == 0)) {

            _EditText.setError("Not filled");
            return false;
        }

        _EditText.setError(null);
        return true;
    }


    /**
     * Sets/unsets the Stroke fields for an view or removes them.
     *
     * @param _StrikeViews if true, all given view components are stroke. else they will be unstroke.
     * @param _Views       the list of views that should be stroke or unstroke.
     */
    public static void setStrokeView(boolean _StrikeViews, List<TextView> _Views) {
        if (_StrikeViews) {
            // element is striked
            for (TextView textView : _Views) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        } else {
            // element is unstriked
            for (TextView textView : _Views) {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }

    /**
     * Retrieve a KeyListener that accepts decimal numbers and dots as separator. Additionally the
     * decimal separator of the current language is accepted.
     *
     * @return An instance of a DigitKeyListener with additional separator.
     */
    public static KeyListener getNumberListener() {
        return DigitsKeyListener.getInstance(NUMBERS_AND_SEPARATOR);
    }

    /**
     * Converts a float to string with current locale.
     *
     * @param _toConvert The number to be converted.
     * @return The converted number with at max 3 digits after separator.
     */
    public static String formatFloat(float _toConvert) {
        NumberFormat formatter = new DecimalFormat("#.###");
        return formatter.format(_toConvert);
    }

    /**
     * Parses a float from a string. The languages separator will be also accepted.
     *
     * @param _toConvert The String to parse.
     * @return Either the parsed number or {@code 0.0f} if the string could not be parsed.
     */
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

    /**
     * Replaces the current fragment and adds it to back-stack.
     *
     * @param _activity    The activity where fragment should be attached. Must be capable of handling
     *                     the FragmentManager.
     * @param _newFragment The new fragment to hang in. Can also be a dialog fragment.
     */
    public static void addFragment(Activity _activity, Fragment _newFragment) {
        FragmentManager fragmentManager = _activity.getFragmentManager();
        String canonicalName = _newFragment.getClass().getCanonicalName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (_newFragment instanceof DialogFragment) {
            ((DialogFragment) _newFragment).show(transaction, canonicalName);
        } else {
            transaction.addToBackStack(canonicalName);
            transaction.replace(R.id.container, _newFragment, canonicalName);
            transaction.commit();
        }
    }

    /**
     * Removes a fragment from the activity, if attached.
     *
     * @param _activity    The activity to remove the Fragment from.
     * @param _oldFragment The Fragment to remove. Note: all Fragment on top on this will also be
     *                     removed.
     */
    public static void removeFragment(Activity _activity, Fragment _oldFragment) {
        FragmentManager fragmentManager = _activity.getFragmentManager();
        fragmentManager.popBackStack(_oldFragment.getClass().getCanonicalName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Validate the Input of the given EditText and retrieve its value.
     *
     * @param _Context  context of the app.
     * @param _EditText the EditText with the want data.
     * @return the inout string of the EditText field.
     */
    public static String validateAndGetResultEditText(Context _Context, EditText _EditText) {
        _EditText.setError(null);
        if (!ViewUtils.checkEditTextIsFilled(_EditText)) {
            _EditText.setError(_Context.getString(R.string.error_no_input));
            return null;
        }
        return _EditText.getText().toString();
    }

    /**
     * Hides the keyboard from screen.
     *
     * @param _Context  the current context.
     * @param _EditText the editText View with the current input focus.
     */
    public static void removeSoftKeyBoard(Context _Context, EditText _EditText) {
        InputMethodManager imm = (InputMethodManager) _Context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_EditText.getWindowToken(), 0);
    }

    /**
     * Show the {@link Snackbar} with the given message.
     *
     * @param _ParentView the current parent view.
     * @param _MessageId  the id of the message.
     * @param _Duration   the duration of showing the snackbar. {@link Snackbar#LENGTH_SHORT} or {@link Snackbar#LENGTH_LONG}.
     */
    public static void showSnackbar(View _ParentView, int _MessageId, int _Duration) {
        Snackbar.make(_ParentView, _MessageId, _Duration)
                .show();
    }

    /**
     * Show the {@link Snackbar} with the given message and a specified action.
     *
     * @param _ParentView     the current parent view.
     * @param _MessageId      the id of the message.
     * @param _Duration       the duration of showing the snackbar. {@link Snackbar#LENGTH_SHORT} or {@link Snackbar#LENGTH_LONG}.
     * @param _ActionStringId the id for the string that represents the string for the action to display.
     * @param _ClickListener  the click listener to react to an click on the action.
     */
    public static void showSnackbar(View _ParentView, int _MessageId, int _Duration,
                                    int _ActionStringId, View.OnClickListener _ClickListener) {
        Snackbar.make(_ParentView, _MessageId, _Duration)
                .setAction(_ActionStringId, _ClickListener)
                .show();
    }
}
