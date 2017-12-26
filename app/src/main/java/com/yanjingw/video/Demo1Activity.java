package com.yanjingw.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.yanjingw.video.renderer.VideoRenderer;

/**
 * 使用SurfaceView播放一个视频流媒体。
 * 参考博客：http://www.cnblogs.com/plokmju/p/android_SurfaceView.html
 */
public class Demo1Activity extends Activity {

    public static final String VIDEO_PATH = "video_path";


    /**
     * 1、SurfaceView的刷新处于主动，有利于频繁的更新画面。
     * 2、SurfaceView的绘制在子线程进行，避免了UI线程的阻塞。
     * 3、SurfaceView在底层实现了一个双缓冲机制，效率大大提升。
     */
    private SurfaceView mSurfaceView;
    private VideoRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo1);

        String videoPath = getIntent().getStringExtra(VIDEO_PATH);

        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mRenderer = new VideoRenderer(this, mSurfaceView.getHolder(), videoPath);
    }

    public static void startActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, Demo1Activity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }
}
