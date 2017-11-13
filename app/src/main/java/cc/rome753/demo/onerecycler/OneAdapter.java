package cc.rome753.demo.onerecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView.Adapter适配器的封装
 *
 * 泛型S是ViewHolder类型，泛型T是数据类型
 *
 * ViewType: footer=0, header=-1,-2,-3..., 普通item=1
 *
 * Created by chao on 2017/4/11.
 */

public class OneAdapter<S extends OneVH<T>, T> extends RecyclerView.Adapter<S> {

    private List<T> data;
    private OnCreateVHListener<S> onCreateVHListener;

    private List<OneVH<Object>> headerVHList;
    private OneVH<Object> footerVH;

    public OneAdapter(OnCreateVHListener<S> onCreateVHListener){
        this(onCreateVHListener, null);
    }

    public OneAdapter(OnCreateVHListener<S> onCreateVHListener, View oneLoadingLayout){
        this.onCreateVHListener = onCreateVHListener;
        if(oneLoadingLayout != null) {
            this.footerVH = new OneVH<Object>(oneLoadingLayout) {
                @Override
                public void bindView(int position, Object o) {
                }
            };
        }
        headerVHList = new ArrayList<>();
    }

    void setData(List<T> data){
        this.data = data;
        notifyDataSetChanged();
    }

    void addData(List<T> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    void addHeader(View header){
        headerVHList.add(new OneVH<Object>(header){
            @Override
            public void bindView(int position, Object t) {
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public S onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType < 0){
            return (S) headerVHList.get(-(viewType + 1));
        }
        if(viewType == 0){
            return (S) footerVH;
        }
        return onCreateVHListener.onCreateHolder(parent);
    }

    @Override
    public void onBindViewHolder(S holder, int position) {
        if(getItemViewType(position) < 0){//header
            return;
        }
        if(getItemViewType(position) == 0){//footer
            return;
        }

        int pos = position - headerVHList.size();
        T t = data.get(pos);
        holder.bindView(pos, t);
    }

    @Override
    public int getItemViewType(int position) {
        if(position < headerVHList.size()){
            return -position - 1;//-1,-2...
        }
        if(footerVH != null && position == getItemCount() - 1){
            return 0;
        }
        return position + 1;
    }

    @Override
    public int getItemCount() {
        if(data == null || data.size() == 0){
            return 0;
        }
        int footer = footerVH == null ? 0 : 1;
        return data.size() + footer + headerVHList.size();
    }

    /**
     * 当前位置是否是普通item，即不是header或footer
     * @param position
     * @return
     */
    boolean isNormalItem(int position){
        return getItemViewType(position) > 0;
    }

}
