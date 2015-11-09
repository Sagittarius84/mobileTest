package org.noorganization.instalist.presenter.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.noorganization.instalist.R;

/**
 * StrikeableRelativeLayout is a LinearLayout that can be stroke through.
 * Created by TS on 19.05.2015.
 */
public class StrikeableRelativeLayout extends RelativeLayout {

    private float   mRelativePaddingLeftAndRight    = 48.0f;
    private float   mLineThickness                  = 8.0f;
    private int     mLineColor                      = Color.BLUE;
    private boolean mStroked                        = false;

    /**
     * Creates an StrikeableRelativeLayout and only uses default params.
     * @param context
     */
    public StrikeableRelativeLayout(Context context) {
        super(context);
    }

    public StrikeableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context.obtainStyledAttributes(attrs, R.styleable.StrikeableLinearLayoutOptions));
    }

    public StrikeableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context.obtainStyledAttributes(attrs, R.styleable.StrikeableLinearLayoutOptions));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StrikeableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context.obtainStyledAttributes(attrs, R.styleable.StrikeableLinearLayoutOptions));
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.state = mStroked ? 1 : 0;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setStroked(ss.state == 0 ? false : true);
    }

    static class SavedState extends BaseSavedState {
        int state;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Override
    protected void dispatchDraw(Canvas _Canvas) {

        // draw all other stuff inside this
        super.dispatchDraw(_Canvas);

        if(mStroked){
            drawStrokeText(_Canvas);
        }


    }

    /**
     * Set the StrikeableRelativeLayout as stroke.
     * @param _Stroked true if to stroke the item in list, false then not.
     */
    public void setStroked(boolean _Stroked){
        mStroked = _Stroked;
    }

    // ----------------------------------------------------------------------------------------------
    // private declared methods
    // ----------------------------------------------------------------------------------------------

    /**
     * initializes the default values. Also sets the custom values which are set by attributes.
     * @param _AttributeSet the set of the defined attributes in the view.
     */
    private void init(TypedArray _AttributeSet){
        mRelativePaddingLeftAndRight    = _AttributeSet.getFloat(R.styleable.StrikeableLinearLayoutOptions_strike_padding_left_and_right, mRelativePaddingLeftAndRight);
        mLineThickness                  = _AttributeSet.getFloat(R.styleable.StrikeableLinearLayoutOptions_strike_thickness, mLineThickness);
        mLineColor                      = _AttributeSet.getColor(R.styleable.StrikeableLinearLayoutOptions_strike_color , mLineColor);
        _AttributeSet.recycle();
    }

    /**
     * Draws a line over the whole layout.
     * @param _Canvas the canvas where the line should be drawn.
     */
    private void drawStrokeText(Canvas _Canvas){

        // get the width and height of this LinearLayout
        float screenWidth   = _Canvas.getWidth();
        float screenHeight  = _Canvas.getHeight();

        float startX        = mRelativePaddingLeftAndRight;
        float endX          = screenWidth - mRelativePaddingLeftAndRight;
        // strike through the middle of the layout
        float positionY     = (screenHeight + mLineThickness) / 2.0f;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(mLineThickness);
        paint.setColor(mLineColor);
        _Canvas.drawLine(startX, positionY, endX, positionY, paint);
    }
}
