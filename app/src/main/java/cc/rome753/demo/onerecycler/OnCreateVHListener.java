package cc.rome753.demo.onerecycler;

import android.view.ViewGroup;

public interface OnCreateVHListener<S extends OneVH>{
        /**
         * 创建ViewHolder
         * @param parent RecyclerView
         * @return S extends OneVH
         */
        S onCreateHolder(ViewGroup parent);

        /**
         * 根据当前位置或数据判断是否创建S类型的ViewHolder
         * @param position
         * @param t
         * @return
         */
        boolean isCreate(int position, Object t);
}