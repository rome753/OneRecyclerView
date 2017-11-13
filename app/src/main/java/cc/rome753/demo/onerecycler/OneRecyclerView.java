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

import java.util.List;

import cc.rome753.demo.R;


/**
 * RecyclerView的包装类，封装了RecyclerView、OneAdapter、SwipeRefreshLayout和emptyView
 * Created by crc on 2017/4/11.
 */

public class OneRecyclerView<S extends OneAdapter.VH<T>, T> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private OneLoadingLayout oneLoadingLayout;
    private RecyclerView recyclerView;
    private OneAdapter<S, T> adapter;
    private GridLayoutManager layoutManager;
    private View emptyView;

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
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_wrapper);
        swipeRefreshLayout.setOnRefreshListener(this);

        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        oneLoadingLayout = new OneLoadingLayout(getContext());
        oneLoadingLayout.setLayoutParams(params);

        recyclerView = (RecyclerView)findViewById(R.id.rv_wrapper);
        layoutManager = new GridLayoutManager(context, 1);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) > 0 ? 1 : layoutManager.getSpanCount();
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
                    onRefresh();
                }
            }
        });
    }

    public void init(OneAdapter.OnCreateVHListener<S> onCreateVHListener){
        init(onCreateVHListener, null, null);
    }

    public void init(OneAdapter.OnCreateVHListener<S> onCreateVHListener, SwipeRefreshLayout.OnRefreshListener onRefreshListener){
        init(onCreateVHListener, onRefreshListener, null);
    }

    public void init(OneAdapter.OnCreateVHListener<S> onCreateVHListener, SwipeRefreshLayout.OnRefreshListener onRefreshListener, OneLoadingLayout.OnLoadMoreListener onLoadMoreListener){
        if(onRefreshListener != null) {
            this.onRefreshListener = onRefreshListener;
        }else{
            swipeRefreshLayout.setEnabled(false);
        }

        if(onLoadMoreListener != null) {
            this.onLoadMoreListener = onLoadMoreListener;
            adapter = new OneAdapter<>(onCreateVHListener, oneLoadingLayout);
        }else{
            adapter = new OneAdapter<>(onCreateVHListener);
        }

        recyclerView.setAdapter(adapter);
        onRefresh();
    }

    public void setSpanCount(int spanCount){
        layoutManager.setSpanCount(spanCount);
    }

    public void addHeader(View header){
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(params);
        adapter.addHeader(header);
    }

    //下拉刷新回调
    @Override
    public void onRefresh() {
        if(onRefreshListener != null) {
            swipeRefreshLayout.setRefreshing(true);
            onRefreshListener.onRefresh();
        }
    }

    //加载更多回调
    public void onLoadMore() {
        if(onLoadMoreListener != null) {
            oneLoadingLayout.setLoading(true);
            onLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 获取到数据
     * @param data 数据
     */
    public void setData(List<T> data){
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(data);
        if(data == null || data.size() == 0) {
            emptyView.setVisibility(VISIBLE);
        }else{
            emptyView.setVisibility(GONE);
        }
    }

    public void addData(List<T> data){
        oneLoadingLayout.setLoading(false);
        if(data == null || data.size() == 0) {
            return;
        }
        adapter.addData(data);
    }

}
