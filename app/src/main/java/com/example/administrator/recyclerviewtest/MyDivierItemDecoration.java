package com.example.administrator.recyclerviewtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/12/23.
 */

public class MyDivierItemDecoration extends RecyclerView.ItemDecoration {

    private final String TAG = "PARENT";

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private int mOrientation;
    private Drawable mDivier;
    private int mDivieerHeight = 2; //px
    private Paint mPaint;

    /**
     * 绘制默认分割线
     * @param context
     * @param orientation
     */
    public MyDivierItemDecoration(Context context, int orientation) {
        final TypedArray ta = context.obtainStyledAttributes(ATTRS);
        mDivier = ta.getDrawable(0);
        ta.recycle();
        setOrientation(orientation);
    }

    public MyDivierItemDecoration(Context context, int orientation, int divierHeight, int dividerColor){
        this(context, orientation);
        mDivieerHeight = divierHeight;
        mPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void setOrientation(int orientation){
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST){
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent){
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        Log.e(TAG, "drawVertical:  right = " + parent.getWidth() + "-" + parent.getPaddingRight());
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++){
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //item距离底边的距离，和recyclerView列表的外边距
            final int top = child.getBottom() + params.bottomMargin;
            Log.e(TAG, "drawVertical: " + child.getBottom() +" "+ params.bottomMargin );
            final int bottom = top + mDivier.getIntrinsicHeight();
            mDivier.setBounds(left, top, right, bottom);
            mDivier.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent){
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivier.getIntrinsicHeight();
            Log.e(TAG, "drawHorizontal: " + left +"  " + mDivier.getIntrinsicHeight() );
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST){
            outRect.set(0, 0, 0, mDivieerHeight);
        } else{
            outRect.set(0, 0, mDivieerHeight, 0);
        }
    }
}
