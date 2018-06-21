package com.angcyo.looprecyclerview.new_opt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.angcyo.looprecyclerview.MainActivity;
import com.angcyo.looprecyclerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/06/21 17:36
 * 修改人员：Robi
 * 修改时间：2018/06/21 17:36
 * 修改备注：
 * Version: 1.0.0
 */
public class NewActivity extends AppCompatActivity {

    List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_layout);


        RecyclerView loopRecyclerView = findViewById(R.id.recycler_view);

        for (int i = 0; i < 5; i++) {
            datas.add("测试数据位:" + i);
        }

        MainActivity.MyAdapter myAdapter = new MainActivity.MyAdapter();
        myAdapter.setDatas(datas);
        loopRecyclerView.setAdapter(myAdapter);
    }
}
