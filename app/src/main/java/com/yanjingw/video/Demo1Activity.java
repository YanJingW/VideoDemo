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

import com.yanjingw.video.inter.PreviewPlayer;
import com.yanjingw.video.util.LogUtils;
import com.yanjingw.video.view.PreviewVideoView;
import com.yanjingw.video.view.ViewController;

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
    private PreviewVideoView mSurfaceView;
    private PreviewPlayer previewPlayer;
    private Activity activity;
    private String videoPath;

    //标记暂停和播放状态
    private boolean isPlaying = true;
    private ViewController mViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo1);
        activity = Demo1Activity.this;

        videoPath = getIntent().getStringExtra(VIDEO_PATH);

        if (TextUtils.isEmpty(videoPath)) {
            return;
        }


        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);

        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);

        previewPlayer = new PreviewPlayer();

        mViewController = findViewById(R.id.view_controller);
        mViewController.setVideoPlayer(previewPlayer);
    }


    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtils.i("surfaceDestroyed");

            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
//            myMediaPlayer.stop();
            //保存播放位置
            if (previewPlayer != null) {
                previewPlayer.pause();
            }
            mViewController.pause();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.i("surfaceCreated");

            previewPlayer.createMediaPlayer(holder);
            previewPlayer.setOnCompletionListener(new PreviewPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer var1) {
                    mViewController.pause();
                }
            });
            previewPlayer.setOnAfterStartListener(new PreviewPlayer.OnAfterStartListener() {
                @Override
                public void onAfterStart(MediaPlayer mp) {
                    if (previewPlayer == null) {
                        return;
                    }
                    //适配视频的高度
                    int videoWidth = previewPlayer.getVideoWidth();
                    int videoHeight = previewPlayer.getVideoHeight();
                    mSurfaceView.fitVideoSize(videoWidth, videoHeight);
                    mViewController.fitVideoSize(videoWidth, videoHeight);
                }
            });
            previewPlayer.setDataSource(videoPath);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.i("surfaceChanged");
        }

    };


    // TODO: 2018/3/19 此方法什么时候被调用
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.i("onConfigurationChanged");

        if (previewPlayer == null) {
            return;
        }
        //适配视频的高度
        int videoWidth = previewPlayer.getVideoWidth();
        int videoHeight = previewPlayer.getVideoHeight();
        mSurfaceView.updateLayout(newConfig, videoWidth, videoHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewController.destroy();
    }

    public static void startActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, Demo1Activity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }
}
