package com.ilyaeremin.diagonallayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ereminilya on 27/2/17.
 */

public class DiagonalLayout extends ViewGroup {

    int parentWidth;

    public DiagonalLayout(Context context) {
        super(context);
    }

    public DiagonalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiagonalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DiagonalLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();


        // Measurement will ultimately be computing these values.
        int maxHeight = 0;

        direction = 1;
        edge = 0;
        bottomOfLastChild = 0;
        parentWidth = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                maxHeight += child.getMeasuredHeight();
            }
        }
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(parentWidth, maxHeight);
    }

    int direction         = 1;
    int edge              = 0;
    int bottomOfLastChild = 0;

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childTop = bottomOfLastChild;
                int childLeft;
                int childRight;
                int childBottom;
                if (width == parentWidth) {
                    if (direction > 0) {
                        edge = right;
                    } else {
                        edge = 0;
                    }
                    childLeft = left;
                    childRight = right;
                    direction *= -1;
                } else {
                    if (direction > 0 && edge + width > right) {
                        direction = -1;
                        edge = right;
                    } else if (direction < 0 && edge - width < 0) {
                        direction = 1;
                        edge = 0;
                    }
                    if (direction > 0) {
                        childLeft = edge;
                        childRight = edge + width;
                        edge += width;
                    } else {
                        childRight = edge;
                        childLeft = edge - width;
                        edge -= width;
                    }
                }
                childBottom = childTop + getPaddingTop() + height;
                bottomOfLastChild += height;

                // Place the child.
                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new DiagonalLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.TOP | Gravity.START;

        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT   = 1;
        public static int POSITION_RIGHT  = 2;

        public int position = POSITION_MIDDLE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.DiagonalLayout);
            gravity = a.getInt(R.styleable.DiagonalLayout_android_layout_gravity, gravity);
            position = a.getInt(R.styleable.DiagonalLayout_layout_position, position);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}