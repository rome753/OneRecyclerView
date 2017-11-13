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
 * ViewType:
     * header1...-3
     * header2...-2
     * footer...-1
     * normal1...0
     * normal2...1
     * normal3...2
 *
 * Created by chao on 2017/4/11.
 */

public class OneAdapter<S extends OneVH<T>, T> extends RecyclerView.Adapter<S> {

    private static final int TYPE_FOOTER = -1;
    private static final int TYPE_HEADER_MAX = TYPE_FOOTER - 1;
    private static final int TYPE_NORMAL_MIN = TYPE_FOOTER + 1;

    private List<T> data;
    private List<OnCreateVHListener<S,T>> listeners;

    private List<OneVH<Object>> headerVHList;
    private OneVH<Object> footerVH;

    public OneAdapter(List<OnCreateVHListener<S,T>> listeners){
        this(listeners, null);
    }

    public OneAdapter(List<OnCreateVHListener<S,T>> listeners, View oneLoadingLayout){
        this.listeners = listeners;
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
    public int getItemViewType(int position) {
        if(position < headerVHList.size()){
            return TYPE_HEADER_MAX - position;
        }
        if(footerVH != null && position == getItemCount() - 1){
            return TYPE_FOOTER;
        }

        int pos = position - headerVHList.size();
        T t = data.get(pos);

        for(int i = 0; i < listeners.size(); i++){
            OnCreateVHListener<S,T> listener = listeners.get(i);
            if(listener.isCreate(pos, t)){
                return i;
            }
        }
        return TYPE_NORMAL_MIN;
    }

    @Override
    public S onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType <= TYPE_HEADER_MAX){
            return (S) headerVHList.get(TYPE_HEADER_MAX - viewType);
        }
        if(viewType == TYPE_FOOTER){
            return (S) footerVH;
        }
        return listeners.get(viewType).onCreateHolder(parent);
    }

    @Override
    public void onBindViewHolder(S holder, int position) {
        if(getItemViewType(position) <= TYPE_HEADER_MAX){//header
            return;
        }
        if(getItemViewType(position) == TYPE_FOOTER){//footer
            return;
        }

        int pos = position - headerVHList.size();
        T t = data.get(pos);
        holder.bindView(pos, t);
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
        return getItemViewType(position) >= TYPE_NORMAL_MIN;
    }

}
