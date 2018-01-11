package com.yanjingw.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

/**
 */
public class Demo2Activity extends Activity {

    public static final String VIDEO_PATH = "video_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo2);

        String videoPath = getIntent().getStringExtra(VIDEO_PATH);

        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
    }

    public static void startActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, Demo2Activity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }
}
