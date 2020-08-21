package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.dongxl.camera.TextureCameraActivity;
import com.dongxl.library.utils.LogUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_STORAGE_PERMISSION = 101;
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

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TextureCameraActivity.class);
                startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                jsoupdata();
            }
        }).start();
        checkReadPermission();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "获取权限失败，请稍后在重试", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 请求权限弹窗 允许后回调返回的成功回调 在此写业务逻辑

                }
                break;
            default:
                break;
        }
    }


    /**
     * 请求读写权限
     */
    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //TODO 此处写第一次检查权限且已经拥有权限后的业务

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_STORAGE_PERMISSION);
            } else {
                LogUtils.e(TAG, "requestPermissions");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

}
