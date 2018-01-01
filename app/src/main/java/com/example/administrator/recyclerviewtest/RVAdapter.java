package com.example.administrator.recyclerviewtest;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2017/12/17.
 */

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<String> mList;  //数据源
    private Context mContext;

    //通过getviewtype返回需要的类型，从而判断需要显示的view
    private int normalType = 0;  //第一种ViewType，正常的item
    private int footType = 1;    //第二种ViewType，底部的提示View

    private boolean hasMore = true; //变量，是否有更多数据
    private boolean fadeTips = false; //变量，是否隐藏了底部的提示

    private SwipeRefreshLayout refresh;
    private int randomHeight;
    private OnItemClickListener onItemClickListener;
    private int mLastPosition;
    private android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //交换位置
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        //删除数据
        mList.remove(position);
        notifyItemRemoved(position);
    }



    public static interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setItemClickListener(OnItemClickListener clickListener){
        onItemClickListener = clickListener;
    }
    public RVAdapter(List<String> list, Context context, boolean hasMore){
        //初始化变量
        mList = list;
        this.mContext = context;
        this.hasMore = hasMore;
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }


    //自定义method ， 获取列表中数据源的最后一个位置， 比getItemCount少1，因为不计上footview
    public int getRealLastPosition(){
        return mList.size();
    }

    //根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1){
            return footType;
        } else {
            return normalType;
        }
    }

    /**
     * 正常item的ViewHolder，用以缓存findView操作
     */
    public class NormalHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView deleteTextView;
        LinearLayout ll;
        public NormalHolder(View view){
            super(view);
            ll = (LinearLayout) view.findViewById(R.id.ll_item);
            textView = (TextView) view.findViewById(R.id.tx_num);
            deleteTextView = (TextView) view.findViewById(R.id.content_delete);

            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(getAdapterPosition());
                    Log.e("TAG", "onClick: " );
                }
            });

        }
    }

    public class FootHolder extends RecyclerView.ViewHolder{
        private TextView tips;
        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据返回的ViewType，绑定不同的布局文件，这里有两种
        if (viewType == normalType){
            return new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_item, null));
        } else {
            return new FootHolder(LayoutInflater.from(mContext).inflate(R.layout.footview, null));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //如果是正常的item， 直接设置TextView的值
        if (holder instanceof NormalHolder){
            ((NormalHolder) holder).textView.setText(mList.get(position));
        } else {
            //设置为可见，因为我在没有更多数据时会隐藏这个footView
            ((FootHolder)holder).tips.setVisibility(View.VISIBLE);
            //获取数据为空时，hasMore为false，则显示加载更多
            if (hasMore == true){
                //不隐藏footView提示
                fadeTips = false;
                if (mList.size() > 0){
                    //如果查询数据增加后，显示加载更多
                    ((FootHolder) holder).tips.setText("正在加载更多");
                }
            } else {
                //没有更多了
                if (mList.size() > 0){
                    //查询到数据没有增加，显示没有数据了
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    //模拟网络延迟
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //隐藏tips
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTips = true;
                            //hasMore为true是为了，再次拉到底时，显示加载跟多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    public void addData(int position){
        mList.add(position, "Insert One");
        notifyItemInserted(position);
    }

    public void reMove(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void resetLists() {
        mList = new ArrayList<>();
    }

    public void updateList(List<String> newLists, boolean hasMore){
        //在原有数据上增加新数据
        if (newLists != null){
            mList.addAll(newLists);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public boolean isFadeTips(){
        return fadeTips;
    }



}
