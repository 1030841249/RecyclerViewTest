package com.example.administrator.recyclerviewtest;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private List<String> mList;
    private RVAdapter adapter;
    private SwipeRefreshLayout refresh;
    private ItemTouchHelperAdapter touchHelperAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper()); //主线程
    private final int PAGE_COUNT = 10;
    private int lastVisibleItem = 0;    //表示当前可见范围内的最后一条item的位置
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initAdapter();
        initRefresh();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        //recyclerView.addItemDecoration(new SpacesItemDecoration(8));
        final GridLayoutManager manager = new GridLayoutManager(this, 1);
        //        GridLayoutManager manager = new GridLayoutManager(this, 4);
        //        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DivierGridItemDecoration(this));
        setItemTouch();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){    //在底部了
                    //如果还没有隐藏footView，name最后一条的位置，就比itemcount少1
                    if (adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //更新RecyclerView
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }

                    //隐藏了footView，又上拉加载时，最后一条比getItemCount少2
                    if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //更新
                                updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑动完成后，拿到最后一个可见的item位置
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });

    }
    public void initAdapter(){
        adapter = new RVAdapter(getLists(0, PAGE_COUNT), this,
                getLists(0, PAGE_COUNT).size() > 0 ? true : false);
        adapter.setItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

                Toast.makeText(MainActivity.this, "LongClick", Toast.LENGTH_SHORT).show();

                adapter.notifyItemRemoved(position);
            }
        });

    }



    public void initData(){
        mList = new ArrayList<>();
        for (int i = 1; i < 40; i++) {
                mList.add("条目" + i);
        }
    }

    private void initRefresh(){
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light);
        refresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        //设置可见
        refresh.setRefreshing(true);
        //重置adapter的数据位为空
        adapter.resetLists();
        //获取第0道第PAGE_COUNT(值为10)条的数据
        adapter.updateList(mList, true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
            }
        }, 1000);    //延迟1000s

    }

    private void setItemTouch(){
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void refreshList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initList();
                        adapter.notifyDataSetChanged();
                        refresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initList(){
        mList.clear();
        initData();
        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            int index = random.nextInt(mList.size());
            mList.add(mList.get(index));
        }
    }


    private List<String> getLists(final int firstIndex, final int lastIndex){
        List<String> resList = new ArrayList<>();
        //从first开始到lastIndex结束，一次添加对象到列表中
        for (int i = firstIndex; i < lastIndex; i++){
            //如果已经全部显示，则返回null
            if (i < mList.size()){
                resList.add(mList.get(i));
            }
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex){
        List<String> newLists = getLists(fromIndex, toIndex);
        //根据返回的数据判断，是否有更多的item
        if (newLists.size() > 0){
            adapter.updateList(newLists, true);
        } else {
            //size < 0 表示没有更多了，返回的value为null
            adapter.updateList(null, false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert:
                adapter.addData(1);
                break;
            case R.id.remove:
                adapter.reMove(1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * ItemDecoration  装饰
     */

    class SpacesItemDecoration extends RecyclerView.ItemDecoration{
        private int space;

        public SpacesItemDecoration(int space){
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            //判断上方是否有项目
            if (parent.getChildLayoutPosition(view) == 0){
                outRect.top = 0;
            } else{
                outRect.top = space;
            }
        }
    }
}
