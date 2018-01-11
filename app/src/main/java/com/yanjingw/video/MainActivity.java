package com.yanjingw.video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yanjingw.video.util.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    File demo1File = new File(FileUtils.ROOT, "demo1.MP4");
    File demo2File = new File(FileUtils.ROOT, "demo1.MP4");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        Button bu_demo1 = findViewById(R.id.demo1);
        bu_demo1.setOnClickListener(this);
        Button bu_demo2 = findViewById(R.id.demo2);
        bu_demo2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.demo1:
                if (demo1File.exists()) {
                    //文件存在
                    Demo1Activity.startActivity(MainActivity.this, demo1File.getAbsolutePath());
                } else {
                    //文件不存在
                    Toast.makeText(MainActivity.this, "示例文件不存在", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.demo2:
                if (demo2File.exists()) {
                    //文件存在
                    Demo2Activity.startActivity(MainActivity.this, demo2File.getAbsolutePath());
                } else {
                    //文件不存在
                    Toast.makeText(MainActivity.this, "示例文件不存在", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
