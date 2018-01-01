package com.example.administrator.recyclerviewtest;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/12/30.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    //限制ImageView长度所能增加的最大值
    private double ICON_MAX_SIZE = 50;
    //ImageView的初始长宽
    private int fixedWidth = 150;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * 该方法用于返回可以滑动的方向
     * 如： 左右滑动， 上下拖动
     * 使用makeMovementFlags（）或makeFlag（）构造返回值
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //允许上下拖动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //允许从左往右侧滑
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 返回true允许长按拖动item
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 返回true允许item滑动
     * 并可以执行滑动删除操作
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * 拖动一个Item进行上下移动从旧的位置到新的位置时调用该方法
     * 在该方法内，调用Adapter的notifItemMoved（）方法交换两个ViewHolder的位置
     * 最后返回true，表示位置交换成功
     * 需支持上下拖动
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //调用onItemMove，接口的方法,交换位置
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 左右滑动Item到达删除条件是，调用该方法
     * 一般滑动距离达到RecyclerView宽度的一半时，
     * 松开手指，该item会继续向原先方向滑动并调用swipe（）方法删除
     * 否则滑动回到原先位置
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //接口方法
//        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 从静止状态变为拖拽或者滑动时会回调该方法
     * @param viewHolder
     * @param actionState  当前状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当用户操作完某个item并且其动画也结束后会调用该方法，
     * 一般我们在该方法内恢复ItemView的出事状态，防止由于复用而产生的现实错乱问题
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //重置改变
        viewHolder.itemView.setScrollX(0); //横向滚动偏移量
//        ((RVAdapter.NormalHolder)viewHolder).deleteTextView.setText("左滑删除");
    }

    /**
     * 在该方法内实现自定义的交互规则或动画效果
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //在侧滑状态下的效果做出改变
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            //
//            if (Math.abs(dX) <= viewHolder.itemView.getWidth()){
//                Log.e("TAG", "onChildDraw:  DX =====" + dX );
//                viewHolder.itemView.scrollTo(-(int) dX, 0);
//            } else {
//
//            }
//        } else {
//            //拖拽状态下不做改变，调用父类方法
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
    }


}
