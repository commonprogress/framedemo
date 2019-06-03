package com.example.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private RecyclerView recyclerView;
    private TestRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.textview);
        recyclerView = findViewById(R.id.recycleview);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new TestRecyclerAdapter();
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        testData();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                jsoupdata();
            }
        }).start();
    }

    private void testData() {
        List<String> list = new ArrayList<>();
        int max = 100000;
        for (int i = 0; i < max; i++) {
            String str = "====" + i + "==测试数据==" + i + "===";
//            Log.e("dongxl", "testData==" + str);
            list.add(str);
        }
//        recyclerView.setItemViewCacheSize(list.size());
        adapter.setTestList(list);
        adapter.notifyDataSetChanged();
    }

    private String url = "https://m.toutiaocdn.com/i6691045323411816972/?app=news_article_lite&is_hit_share_recommend=0&tt_from=copy_link&utm_source=copy_link&utm_medium=toutiao_ios&utm_campaign=client_share";

    private void jsoupdata() {
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("head");
            Elements titleLinks = links.get(0).select("title");
            String titleStr = titleLinks.get(0).text();
            showText(titleStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

}
