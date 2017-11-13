package cc.rome753.demo.onerecycler;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cc.rome753.demo.R;


/**
 * Created by crc on 2017/5/3.
 */

public class OneLoadingLayout extends FrameLayout {

    private boolean mLoading;

    public OneLoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public OneLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneLoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_one_loading, this);
        mLoading = false;
        setVisibility(GONE);
    }

    public boolean isLoading(){
        return mLoading;
    }

    public void setLoading(boolean loading){
        mLoading = loading;
        setVisibility(loading ? VISIBLE : GONE);
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }
}
