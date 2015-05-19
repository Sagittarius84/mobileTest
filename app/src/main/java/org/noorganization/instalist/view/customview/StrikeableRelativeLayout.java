package org.noorganization.instalist.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.internal.widget.ViewUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
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
    protected void dispatchDraw(Canvas canvas) {

        // draw all other stuff inside this
        super.dispatchDraw(canvas);


        // get the width and height of this LinearLayout
        float screenWidth   = canvas.getWidth();
        float screenHeight  = canvas.getHeight();

        float startX        = mRelativePaddingLeftAndRight;
        float endX          = screenWidth - mRelativePaddingLeftAndRight;
        // strike through the middle of the layout
        float positionY     = (screenHeight + mLineThickness) / 2.0f;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(mLineThickness);

        canvas.drawLine(startX, positionY, endX, positionY, paint);

    }


    // ----------------------------------------------------------------------------------------------
    // priivate declared methods
    // ----------------------------------------------------------------------------------------------

    /**
     * initializes the default values. Also sets the custom values which are set by attributes.
     * @param _AttributeSet the set of the defined attributes in the view.
     */
    private void init(TypedArray _AttributeSet){
        mRelativePaddingLeftAndRight    = _AttributeSet.getFloat(R.styleable.StrikeableLinearLayoutOptions_strike_padding_left_and_right, mRelativePaddingLeftAndRight);
        mLineThickness                  = _AttributeSet.getFloat(R.styleable.StrikeableLinearLayoutOptions_strike_thickness, mLineThickness);
        mLineColor                      = _AttributeSet.getColor(R.styleable.StrikeableLinearLayoutOptions_strike_color , mLineColor);

    }
}
