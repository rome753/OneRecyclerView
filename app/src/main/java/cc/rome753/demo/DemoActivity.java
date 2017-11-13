package cc.rome753.demo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.rome753.demo.onerecycler.OnCreateVHListener;
import cc.rome753.demo.onerecycler.OneLoadingLayout;
import cc.rome753.demo.onerecycler.OneRecyclerView;
import cc.rome753.demo.onerecycler.OneVH;

/**
 * Created by chao on 17-10-10.
 */

public class DemoActivity extends AppCompatActivity {

    private OneRecyclerView orv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        orv = findViewById(R.id.orv);
        orv.init(
                new OnCreateVHListener() {
                    @Override
                    public OneVH onCreateHolder(ViewGroup parent) {
                        return new UserInfoVH(parent);
                    }
                },
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
                }
        );

//        orv.setSpanCount(3);

//        View header = View.inflate(this, R.layout.layout_header, null);
//        orv.addHeader(header);

    }

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

    private void requestData(final boolean append){
        orv.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<UserInfo> list = fetchData();
                if(append) {
                    orv.addData(list);
                }else{
                    orv.setData(list);
                }
            }
        }, 1000);
    }

    private List<UserInfo> fetchData() {
        List<UserInfo> list = new ArrayList<>();
        for(int i = 0; i < 26; i++){
            UserInfo userInfo = new UserInfo();
            userInfo.setName(String.valueOf((char)('A' + i)));
            list.add(userInfo);
        }

        return list;
    }
}
