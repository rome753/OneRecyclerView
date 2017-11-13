package cc.rome753.demo.onerecycler;

import android.view.ViewGroup;

public interface OnCreateVHListener<S extends OneVH>{
        S onCreateHolder(ViewGroup parent);
}