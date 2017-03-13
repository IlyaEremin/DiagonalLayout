package com.ilyaeremin.diagonallayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ereminilya on 27/2/17.
 */

public class DiagonalLayout extends ViewGroup {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIRECTION_LEFT_TO_RIGHT, DIRECTION_RIGHT_TO_LEFT})
    public @interface Direction {
    }

    private static final int DIRECTION_LEFT_TO_RIGHT = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = -1;

    private int parentWidth;

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
        int viewHeight = 0;
        parentWidth = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // measure childrens and calculate all childers' height
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                viewHeight += child.getMeasuredHeight();
            }
        }
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(parentWidth, viewHeight);
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        /**
         * depends on layout filling direction this is left or right edge of the current child
         */
        int childEdge = 0;
        int bottomOfLastChild = 0;
        @Direction int direction = DIRECTION_LEFT_TO_RIGHT;

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
                    if (direction == DIRECTION_LEFT_TO_RIGHT) {
                        childEdge = right;
                    } else {
                        childEdge = 0;
                    }
                    childLeft = left;
                    childRight = right;
                    direction = opositeDirection(direction);
                } else {
                    if (direction == DIRECTION_LEFT_TO_RIGHT && childEdge + width > right) {
                        direction = opositeDirection(direction);
                        childEdge = right;
                    } else if (direction == DIRECTION_RIGHT_TO_LEFT && childEdge - width < 0) {
                        direction = opositeDirection(direction);
                        childEdge = 0;
                    }
                    if (direction == DIRECTION_LEFT_TO_RIGHT) {
                        childLeft = childEdge;
                        childRight = childEdge + width;
                        childEdge += width;
                    } else {
                        childRight = childEdge;
                        childLeft = childEdge - width;
                        childEdge -= width;
                    }
                }
                childBottom = childTop + getPaddingTop() + height;
                bottomOfLastChild += height;

                // Place the child.
                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
    }

    @Direction private int opositeDirection(@Direction int direction) {
        return direction == DIRECTION_LEFT_TO_RIGHT ? DIRECTION_RIGHT_TO_LEFT : DIRECTION_LEFT_TO_RIGHT;
    }

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