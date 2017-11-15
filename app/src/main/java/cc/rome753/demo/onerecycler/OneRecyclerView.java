package cc.rome753.demo.onerecycler;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.rome753.demo.R;


/**
 * RecyclerView的包装类，封装了RecyclerView、OneAdapter、SwipeRefreshLayout和emptyView
 *
 * 泛型S是ViewHolder类型，泛型T是数据类型
 * Created by crc on 2017/4/11.
 */

public class OneRecyclerView<S extends OneVH<T>, T> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private OneLoadingLayout oneLoadingLayout;
    private RecyclerView recyclerView;
    private View emptyView;

    private OneAdapter<S, T> adapter;
    private GridLayoutManager layoutManager;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private OneLoadingLayout.OnLoadMoreListener onLoadMoreListener;

    public OneRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public OneRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_one_recycler, this);

        swipeRefreshLayout = findViewById(R.id.srl_wrapper);
        swipeRefreshLayout.setOnRefreshListener(this);

        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        oneLoadingLayout = new OneLoadingLayout(getContext());
        oneLoadingLayout.setLayoutParams(params);

        recyclerView = findViewById(R.id.rv_wrapper);
        layoutManager = new GridLayoutManager(context, 1);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isNormalItem(position) ? 1 : layoutManager.getSpanCount();
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItemPosition;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!oneLoadingLayout.isLoading() && !swipeRefreshLayout.isRefreshing() && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                    //加载更多
                    onLoadMore();
                }
            }
        });

        emptyView = findViewById(R.id.view_empty);
        emptyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!swipeRefreshLayout.isRefreshing()) {
                    emptyView.setVisibility(GONE);
                    onRefresh();
                }
            }
        });
    }

    /**
     * 初始化方法，传入需要的接口实现类
     * @param onCreateVHListeners 创建ViewHolder接口
     */
    public void init(OnCreateVHListener<S>... onCreateVHListeners){
        init(null, null, onCreateVHListeners);
    }

    /**
     * 初始化方法，传入需要的接口实现类
     * @param onCreateVHListeners 创建ViewHolder接口
     * @param onRefreshListener 下拉刷新接口
     */
    public void init(SwipeRefreshLayout.OnRefreshListener onRefreshListener, OnCreateVHListener<S>... onCreateVHListeners){
        init(onRefreshListener, null, onCreateVHListeners);
    }

    /**
     * 初始化方法，传入需要的接口实现类
     * @param onRefreshListener 下拉刷新接口
     * @param onLoadMoreListener 加载更多接口
     * @param onCreateVHListeners 创建ViewHolder接口
     */
    public void init(SwipeRefreshLayout.OnRefreshListener onRefreshListener, OneLoadingLayout.OnLoadMoreListener onLoadMoreListener, OnCreateVHListener<S>... onCreateVHListeners){
        if(onRefreshListener != null) {
            this.onRefreshListener = onRefreshListener;
        }else{
            swipeRefreshLayout.setEnabled(false);
        }

        List<OnCreateVHListener<S>> listeners = new ArrayList<>();
        listeners.addAll(Arrays.asList(onCreateVHListeners));

        if(onLoadMoreListener != null) {
            this.onLoadMoreListener = onLoadMoreListener;
            adapter = new OneAdapter<>(listeners, oneLoadingLayout);
        }else{
            adapter = new OneAdapter<>(listeners);
        }

        recyclerView.setAdapter(adapter);
        onRefresh();
    }

    /**
     * 设置列数
     * @param spanCount
     */
    public void setSpanCount(int spanCount){
        layoutManager.setSpanCount(spanCount);
    }

    /**
     * 添加一个header，如一个BannerView
     * 多次调用可添加多个
     * header的数据和view由自身控制
     * @param header headerView
     */
    public void addHeader(View header){
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(params);
        adapter.addHeader(header);
    }

    /**
     * 下拉刷新回调
     */
    @Override
    public void onRefresh() {
        if(onRefreshListener != null) {
            swipeRefreshLayout.setRefreshing(true);
            onRefreshListener.onRefresh();
        }
    }

    /**
     * 加载更多回调
     */
    public void onLoadMore() {
        if(onLoadMoreListener != null) {
            oneLoadingLayout.setLoading(true);
            onLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 刷新数据
     * @param data 数据
     */
    public void setData(List<Object> data){
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(data);
        if(data == null || data.size() == 0) {
            emptyView.setVisibility(VISIBLE);
        }else{
            emptyView.setVisibility(GONE);
        }
    }

    /**
     * 添加数据
     * @param data 数据
     */
    public void addData(List<Object> data){
        oneLoadingLayout.setLoading(false);
        if(data == null || data.size() == 0) {
            return;
        }
        adapter.addData(data);
    }

}
