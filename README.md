# 已废弃。请使用(OneAdapter)[https://github.com/rome753/OneAdapter]这个项目，有更好的功能和封装。

# OneRecyclerView
一句代码调用的RecyclerView，支持多种ViewType、SwipeRefreshLayout下拉刷新、加载更多、空数据EmptyView和多列布局

## 效果图
![refresh and loadmore](https://github.com/rome753/OneRecyclerView/raw/master/screenshots/orv_base.gif)
![multi-type](https://github.com/rome753/OneRecyclerView/raw/master/screenshots/orv_types.gif)
![multi-column](https://github.com/rome753/OneRecyclerView/raw/master/screenshots/orv_columns.gif)
![empty view](https://github.com/rome753/OneRecyclerView/raw/master/screenshots/orv_empty.gif)


## 使用方法

1. 在布局文件中添加OneRecyclerView
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <cc.rome753.demo.onerecycler.OneRecyclerView
        android:id="@+id/orv"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </cc.rome753.demo.onerecycler.OneRecyclerView>
</LinearLayout>
```

2. 实现自定义ViewHolder
```java

    class UserInfoVH extends OneVH<UserInfo> {

        public UserInfoVH(ViewGroup parent) {//1.设置item布局文件
            super(parent, R.layout.item_user_simple);
        }

        @Override
        public void bindView(int position, final UserInfo o) {//2.处理点击事件和设置数据
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), o.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            TextView tvName = itemView.findViewById(R.id.tv_name);
            tvName.setText(o.getName());
        }
    }
```

包括item的布局文件、给item设置数据和点击事件

3. 一句代码使用OneRecyclerView
```java
        mOneRecyclerView.init(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestData(false);
                    }
                },
                new OneLoadingLayout.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        requestData(true);
                    }
                },
                new OnCreateVHListener() {
                    @Override
                    public OneVH onCreateHolder(ViewGroup parent) {
                        return new UserInfoVH(parent);
                    }

                    @Override
                    public boolean isCreate(int position, Object o) {
                        return position % 3 > 0;
                    }
                }
        );
```
调用OneRecyclerView的init()方法，传入下拉刷新监听、加载更多监听和创建ViewHolder监听即可。

> OneRecyclerView的init()方法最后一个参数是可变参数，针对多种ViewType情况：

> 实现多个ViewHolder，用OnCreateVHListener包装并传入

4. 添加自定义header
```java
        View header = View.inflate(this, R.layout.layout_header, null);
        mOneRecyclerView.addHeader(header);
```

> 可添加多个header，多次调用addHader()方法即可，header的显示完全由自身控制

5. 设置多列显示的列数
```java
        mOneRecyclerView.setSpanCount(3);
```

> 不设置默认是1列；多列显示与多种ViewType一般不会同时用到，根据具体需求选择其一

[原理分析](http://www.jianshu.com/p/d73b937bcc0d)
