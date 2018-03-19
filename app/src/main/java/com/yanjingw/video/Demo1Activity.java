package com.yanjingw.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yanjingw.video.renderer.MyMediaPlayer;
import com.yanjingw.video.util.LogUtil;
import com.yanjingw.video.util.PlayerUtils;

import static android.content.ContentValues.TAG;

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
    private SurfaceView mSurfaceView;
    private MyMediaPlayer myMediaPlayer;
    private Activity activity;


    static final Handler myHandler = new Handler(Looper.getMainLooper()) {
    };
    private float mediaPlayerX;
    private float mediaPlayerY;
    private int playerViewW;
    private int playerViewH;

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
                fitVideoSize();
            }
        });


        //存储控件的位置信息
        // TODO: 2018/3/19 为什么要在这个实际获取控件信息
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayerX = mSurfaceView.getX();
                mediaPlayerY = mSurfaceView.getY();
                playerViewW = mSurfaceView.getWidth();
                playerViewH = mSurfaceView.getHeight();
            }
        }, 0);
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

    //默认宽高比16:9
    private int defaultWidthProportion = 16;
    private int defaultHeightProportion = 9;

    // TODO: 2018/3/19 此方法什么时候被调用
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.i("onConfigurationChanged");

        int screenWidth = PlayerUtils.getScreenWidth(activity);
        int screenHeight = PlayerUtils.getScreenHeight(activity);
        ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();

        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //计算视频的大小16：9
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * defaultHeightProportion / defaultWidthProportion;
            mSurfaceView.setX(mediaPlayerX);
            mSurfaceView.setY(mediaPlayerY);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            layoutParams.width = screenWidth;
            layoutParams.height = screenHeight;

            mSurfaceView.setX(0);
            mSurfaceView.setY(0);
        }
        mSurfaceView.setLayoutParams(layoutParams);

        playerViewW = screenWidth;
        playerViewH = layoutParams.height;

        //适配大小
        fitVideoSize();

    }


    private void fitVideoSize() {
        if (myMediaPlayer == null) {
            return;
        }
        //适配视频的高度
        int videoWidth = myMediaPlayer.getVideoWidth();
        int videoHeight = myMediaPlayer.getVideoHeight();
        int parentWidth = playerViewW;
        int parentHeight = playerViewH;
        int screenWidth = PlayerUtils.getScreenWidth(activity);
        int screenHeight = PlayerUtils.getScreenHeight(activity);

        //判断视频宽高和父布局的宽高
        int surfaceViewW;
        int surfaceViewH;
        if ((float) videoWidth / (float) videoHeight > (float) parentWidth / (float) parentHeight) {
            surfaceViewW = parentWidth;
            surfaceViewH = videoHeight * surfaceViewW / videoWidth;
        } else {
            surfaceViewH = parentHeight;
            surfaceViewW = videoWidth * parentHeight / videoHeight;
        }

        Log.i(TAG, "fitVideoSize---" +
                "videoWidth：" + videoWidth + ",videoHeight:" + videoHeight +
                ",parentWidth:" + parentWidth + ",parentHeight:" + parentHeight +
                ",screenWidth:" + screenWidth + ",screenHeight:" + screenHeight +
                ",surfaceViewW:" + surfaceViewW + ",surfaceViewH:" + surfaceViewH
        );
        //改变surfaceView的大小
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        params.height = surfaceViewH;
        params.width = surfaceViewW;
        mSurfaceView.setLayoutParams(params);
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
