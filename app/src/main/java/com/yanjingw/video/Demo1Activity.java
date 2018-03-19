package com.yanjingw.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.yanjingw.video.renderer.MyMediaPlayer;
import com.yanjingw.video.util.LogUtil;
import com.yanjingw.video.view.VideoSurfaceView;

/**
 * 使用SurfaceView播放一个视频流媒体。
 * 参考博客：http://www.cnblogs.com/plokmju/p/android_SurfaceView.html
 */
public class Demo1Activity extends Activity implements MediaPlayer.OnVideoSizeChangedListener {

    public static final String VIDEO_PATH = "video_path";

    /**
     * 1、SurfaceView的刷新处于主动，有利于频繁的更新画面。
     * 2、SurfaceView的绘制在子线程进行，避免了UI线程的阻塞。
     * 3、SurfaceView在底层实现了一个双缓冲机制，效率大大提升。
     */
    private VideoSurfaceView mSurfaceView;
    private MyMediaPlayer myMediaPlayer;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo1);
        activity = Demo1Activity.this;

        String videoPath = getIntent().getStringExtra(VIDEO_PATH);

        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);

        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);
        myMediaPlayer = new MyMediaPlayer(mSurfaceHolder);
        myMediaPlayer.setVideoPath(videoPath);
        myMediaPlayer.setOnPreparedListener(new MyMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                if (myMediaPlayer == null) {
                    return;
                }
                //适配视频的高度
                int videoWidth = myMediaPlayer.getVideoWidth();
                int videoHeight = myMediaPlayer.getVideoHeight();
                mSurfaceView.fitVideoSize(videoWidth, videoHeight);
            }
        });
    }


    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.i("surfaceDestroyed");

            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            myMediaPlayer.stop();


        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.i("surfaceCreated");

            myMediaPlayer.play();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.i("surfaceChanged");
        }

    };


    // TODO: 2018/3/19 此方法什么时候被调用
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.i("onConfigurationChanged");

        if (myMediaPlayer == null) {
            return;
        }
        //适配视频的高度
        int videoWidth = myMediaPlayer.getVideoWidth();
        int videoHeight = myMediaPlayer.getVideoHeight();
        mSurfaceView.updateLayout(newConfig, videoWidth, videoHeight);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        LogUtil.i("onVideoSizeChanged:::::::");
    }

    public static void startActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, Demo1Activity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }
}
