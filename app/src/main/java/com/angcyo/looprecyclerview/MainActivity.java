package com.angcyo.looprecyclerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RLoopRecyclerView loopRecyclerView = (RLoopRecyclerView) findViewById(R.id.recycler_view);
        loopRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        for (int i = 0; i < 5; i++) {
            datas.add("测试数据位:" + i);
        }

        MyAdapter myAdapter = new MyAdapter();
        myAdapter.setDatas(datas);
        loopRecyclerView.setAdapter(myAdapter);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyAdapter extends RLoopRecyclerView.LoopAdapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.item_text_view, parent, false);
            return new MyViewHolder(inflate);
        }

        @Override
        public void onBindLoopViewHolder(MyViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.text_view);
            textView.setText(datas.get(position));
        }

    }


}
