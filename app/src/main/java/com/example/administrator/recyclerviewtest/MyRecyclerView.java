package com.example.administrator.recyclerviewtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/12/31.
 */

public class MyRecyclerView extends RecyclerView {
    private Scroller scroller;
    public static int state = 0; //菜单状态
    private View view;
    private RVAdapter adapter;
    private RVAdapter.NormalHolder viewHolder;
    private LinearLayout itemLayout;
    private TextView deleteTextView;
    private int mMaxLength;
    private int mLastX, mLastY;
    private ItemTouchHelperAdapter touchListener;
    private int curPosition;
    private boolean isItemMoving;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();


    public MyRecyclerView(Context context) {
        super(context);
        scroller = new Scroller(context, new LinearInterpolator());
        mVelocityTracker = VelocityTracker.obtain();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context, new LinearInterpolator());
        mVelocityTracker = VelocityTracker.obtain();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 重写该方法，自定义规则
     * state : 0 未打开菜单                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               1 打开菜单
     *
     * @param e  MotionEvent 通过它获取当前用户的触摸事件
     * @return  判断事件是否结束了（false为处理结束）
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mVelocityTracker.addMovement(e); //分析MotionEvent 对象在单位时间类发生的位移来计算速度

        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当用户“按下”时应该进行的操作和获取需要的数据
                if (state == 0) { //未打开菜单
                    // 根据用户点击的坐标，找到RecyclerView下的子View
                    view = findChildViewUnder(x, y);

                    //获得每一个Item的ViewHolder
                    viewHolder = (RVAdapter.NormalHolder) getChildViewHolder(view);
                    itemLayout = viewHolder.ll;

                    curPosition = viewHolder.getAdapterPosition();
                    deleteTextView = viewHolder.deleteTextView;
                    mMaxLength = deleteTextView.getWidth();
                    //“textView”被点击
                    deleteTextView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //删除item
                            Log.e("TAG", "MMMMMMonClick: " );

                            itemLayout.scrollTo(0, 0);
                            state = 0;

                        }
                    });

                } else if (state == 1) {

                    //弹性滑动
                    scroller.startScroll(view.getScrollX(), 0, -mMaxLength, 0, 500);
                    invalidate();
                    state = 0;
                    return false;
                } else {
                    return false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //用户“滑动”时，应该进行的操作和获取需要的数据
                //计算需要移动的距离
                int dX = mLastX - x;
                int dY = mLastY - y;

                int scrollX = itemLayout.getScrollX();

                if (Math.abs(dX) > Math.abs(dY)) {
                    isItemMoving = true;
                    if (scrollX + dX <= 0) { //左边界检测
                        itemLayout.scrollTo(0, 0);
                        return true;
                    } else if (scrollX + dX >= mMaxLength) { //右边界检测
                        // "-(当前滚动位置)" + "+（需要滚动的距离）"
                        itemLayout.scrollTo(mMaxLength, 0);
                    }
                    view.scrollBy(dX, 0); //item跟随手指滑动
                }

                break;
            case MotionEvent.ACTION_UP:
                //用户“抬起”时，应该进行的操作和获取需要的数据
                isItemMoving = false;

                mVelocityTracker.computeCurrentVelocity(1000); //计算手指滑动速度，初始化
                float xVelocity = mVelocityTracker.getXVelocity(); // 水平方向速度
                float yVelocity = mVelocityTracker.getXVelocity(); //垂直方向速度

                int deltaX = 0;
                int upScrollX = view.getScrollX();

                if (Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                    if (xVelocity <= -100) { //左滑速度大于100， 则删除按钮显示
                        deltaX = mMaxLength - upScrollX;
                        scrollX = 1;
                    } else if (xVelocity > 100) { //右滑速度大于100， 则删除按钮隐藏
                        deltaX = -upScrollX;
                        state = 0;
                    }
                } else {
                    if (upScrollX >= mMaxLength / 2) { //item的左滑距离大于删除按钮宽度的一半
                        deltaX = mMaxLength - upScrollX;
                        state = 1;
                    } else if (upScrollX < mMaxLength / 2) { //否则隐藏
                        deltaX = -upScrollX;
                        state = 0;
                    }
                }

                //item自动滑动到指定位置
                scroller.startScroll(upScrollX, 0, deltaX, 0, 200);
                invalidate();

                mVelocityTracker.clear();

                break;
            //上一次选择的item所在的view

        }
        mLastX = x;
        mLastY = y;
        Log.e("TAG", "onTouchEvent: Last X & Y :" + x + y);

        //返回调用父类的方法，来处理我们没有处理的操作，比如上下滑动操作
        return super.onTouchEvent(e);

    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            view.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                onTouchEvent(e);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchEvent(e);
                break;
            case MotionEvent.ACTION_UP:
                onTouchEvent(e);
                break;
        }
        return super.onInterceptTouchEvent(e);
    }
}
