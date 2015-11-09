package org.noorganization.instalist.presenter.customview;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.utils.ViewUtils;

import java.text.DecimalFormat;

/**
 * A picker for amounts. This is a compound view made with a LinearLayout, an EditText and two
 * buttons for incrementing and decrementing.
 * Created by michi on 08.06.15.
 */
public class AmountPicker extends LinearLayout {

    private ImageButton mDecreaseButton;
    private ImageButton mIncreaseButton;
    private EditText    mValueField;

    private float       mStep;

    private static final Integer DECREASE_TAG = -1;
    private static final Integer INCREASE_TAG =  1;

    private IValueChangeListener mListener;

    /**
     * Default constructor as described at the android docs.
     * {@see http://developer.android.com/guide/topics/ui/custom-components.html}
     */
    public AmountPicker(Context _context, AttributeSet _attributes) {
        super(_context, _attributes);

        mDecreaseButton = new ImageButton(_context);
        mIncreaseButton = new ImageButton(_context);
        mValueField     = new EditText(_context);

        IndecreaseListener changeListener = new IndecreaseListener();
        mDecreaseButton.setBackground(null);
        mDecreaseButton.setTag(DECREASE_TAG);
        mDecreaseButton.setOnClickListener(changeListener);

        mIncreaseButton.setBackground(null);
        mIncreaseButton.setTag(INCREASE_TAG);
        mIncreaseButton.setOnClickListener(changeListener);

        mValueField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mValueField.setKeyListener(ViewUtils.getNumberListener());
        mValueField.setHint(R.string.product_details_amount_hint);
        mValueField.addTextChangedListener(new TextValueChangedListener());
        mValueField.setGravity(View.TEXT_ALIGNMENT_CENTER);

        if (getOrientation() == LinearLayout.HORIZONTAL) {
            mDecreaseButton.setImageResource(R.mipmap.ic_remove_black_24dp);
            mIncreaseButton.setImageResource(R.mipmap.ic_add_black_24dp);

            addView(mDecreaseButton);
            addView(mValueField);
            addView(mIncreaseButton);
        } else {
            mDecreaseButton.setImageResource(R.mipmap.ic_expand_more_black_24dp);
            mIncreaseButton.setImageResource(R.mipmap.ic_expand_less_black_24dp);

            addView(mIncreaseButton);
            addView(mValueField);
            addView(mDecreaseButton);
        }

        mStep = 1.0f;
    }

    /**
     * Sets a callback for changes. It will be called when a change was made via the UI. So calling
     * {@link #setValue(float)} should have no effect.
     * @param _newListener Either a listener or null to remove the listener. The given listener will
     *                     replace the old one (if one was set).
     */
    public void setChangeListener(IValueChangeListener _newListener) {
        mListener = _newListener;
    }

    /**
     * Sets the new value. Does not change anything if the new value is not conform.
     * @param _NewValue The new value, not lesser than 0.0f, not NaN and not infinite.
     */
    public void setValue(float _NewValue) {
        if (_NewValue < 0.0f || Float.isNaN(_NewValue) || Float.isInfinite(_NewValue)) {
            return;
        }

        mValueField.setText(new DecimalFormat("#.###").format(_NewValue));
    }

    /**
     * Reads the current value from the integrated textfield.
     * @return The value or 0.0f, if nothing set.
     */
    public float getValue() {
        return ViewUtils.parseFloatFromLocal(mValueField.getText().toString());
    }

    /**
     * Sets the step for incrementation or decrementation buttons.
     * @param _NewStep The new step-size. Must be a positive, finite number.
     */
    public void setStep(float _NewStep) {
        if (_NewStep < 0.0f || Float.isNaN(_NewStep) || Float.isInfinite(_NewStep)) {
            return;
        }

        mStep = _NewStep;
    }

    public float getStep() {
        return mStep;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parentState = super.onSaveInstanceState();

        SavedState rtn = new SavedState(parentState);
        rtn.mValue = getValue();

        return rtn;
    }

    @Override
    public void onRestoreInstanceState(Parcelable _restoreData) {
        if (!(_restoreData instanceof SavedState)) {
            super.onRestoreInstanceState(_restoreData);
            return;
        }

        SavedState savedData = (SavedState) _restoreData;
        super.onRestoreInstanceState(savedData.getSuperState());

        mValueField.setText(savedData.mValue + "");
    }

    public interface IValueChangeListener {
        void onValueChanged(AmountPicker _picker, float _newValue);
    }

    private static class SavedState extends BaseSavedState {
        public float mValue;

        public SavedState(Parcelable _superState) {
            super(_superState);
        }

        private SavedState(Parcel _loadParcel) {
            super(_loadParcel);
            mValue = _loadParcel.readFloat();
        }

        @Override
        public void writeToParcel(Parcel _output, int _flags) {
            super.writeToParcel(_output, _flags);
            _output.writeFloat(mValue);
        }

        /**
         * needed for making Parcels to Parcelables.
         * {@see http://stackoverflow.com/a/3542895/1354246}
         */
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private class IndecreaseListener implements View.OnClickListener {
        @Override
        public void onClick(View _view) {
            if (_view.getTag() instanceof Integer) {
                Integer tag = (Integer) _view.getTag();

                if (tag.compareTo(INCREASE_TAG) == 0) {
                    float newValue = getValue() + mStep;
                    setValue(newValue);
                    if (mListener != null) {
                        mListener.onValueChanged(AmountPicker.this, newValue);
                    }
                } else if (tag.compareTo(DECREASE_TAG) == 0) {
                    float newValue = Math.max(0.0f, getValue() - mStep);
                    setValue(newValue);
                    if (mListener != null) {
                        mListener.onValueChanged(AmountPicker.this, newValue);
                    }
                }

            }
        }
    }

    private class TextValueChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not used.
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not used.
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mListener != null) {
                float newValue = getValue();
                mListener.onValueChanged(AmountPicker.this, newValue);
            }
        }
    }

}
