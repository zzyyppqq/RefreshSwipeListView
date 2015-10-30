package com.example.zhangyipeng.demoapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private PullToRefreshSwipeListView refreshableView;
    private SwipeMenuListView mListView;
    private MyAdapter myAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addInit();




        refreshableView = (PullToRefreshSwipeListView) findViewById(R.id.plv);
        refreshableView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshableView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多...");
        refreshableView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载更多...");
        refreshableView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开已刷新...");

        refreshableView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SwipeMenuListView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<SwipeMenuListView> refreshView) {
                new Handler().postDelayed(new Runnable(){

                    public void run() {
                        if(refreshableView.isRefreshing()){

                        }else{

                        }
                        list.clear();
                        addInit();
                        myAdapter.notifyDataSetChanged();
                        refreshView.onRefreshComplete();

                    }

                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<SwipeMenuListView> refreshView) {
                new Handler().postDelayed(new Runnable(){

                    public void run() {
                        if(refreshableView.isRefreshing()){

                        }else{

                        }
                        addMore();
                        myAdapter.notifyDataSetChanged();
                        refreshView.onRefreshComplete();

                    }

                }, 2000);
            }
        });

        mListView = refreshableView.getRefreshableView();


        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {

                }
            }
        });

//        TextView tv = new TextView(MainActivity.this);
//        tv.setText("加载更多");
//        mListView.addFooterView(tv);

        mListView.setDivider(new ColorDrawable(Color.rgb(0x00,
                0x3F, 0x25)));
        mListView.setDividerHeight(5);
        mListView.setSelector(android.R.color.transparent);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_launcher);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        myAdapter = new MyAdapter();

        refreshableView.setAdapter(myAdapter);



        refreshableView.setRefreshing(true);






        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
                return false;
            }
        });





        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this,TargetActivity.class));

            }
        });


    }

    void addInit(){
        for (int i = 0;i<3;i++){
            list.add("条目"+i);
        }

    }
    void addMore(){
        for (int i = 4;i<9;i++){
            list.add("条目"+i);
        }

    }



    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view =LayoutInflater.from(MainActivity.this).inflate(R.layout.item,null);
           TextView tv = (TextView) view.findViewById(R.id.tv);

            tv.setText(list.get(position));

            return view;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }



}
