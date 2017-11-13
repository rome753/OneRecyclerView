package cc.rome753.demo.onerecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class OneVH<T> extends RecyclerView.ViewHolder {

    public OneVH(View itemView) {
        super(itemView);
    }

    public OneVH(ViewGroup parent, int layoutRes) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    public abstract void bindView(int position, T t);
}