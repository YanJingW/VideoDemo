package com.yanjingw.video.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

import com.yanjingw.video.R;
import com.yanjingw.video.inter.PreviewPlayer;
import com.yanjingw.video.util.LogUtils;


public class PreviewVideoView extends FrameLayout {
    private View mRootView;
    private PreviewSurfaceView mSurfaceView;
    private ViewController mViewController;
    private PreviewPlayer previewPlayer;
    private String mVideoPath;

    public PreviewVideoView(@NonNull Context context) {
        this(context, null);
    }

    public PreviewVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_preview_video, this, false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);

        mSurfaceView = (PreviewSurfaceView) mRootView.findViewById(R.id.video_view);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mViewController = (ViewController) mRootView.findViewById(R.id.view_controller);

    }


    public void setDataSource(String videoPath) {
        this.mVideoPath = videoPath;

        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);

        previewPlayer = new PreviewPlayer();
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
            previewPlayer.setDataSource(mVideoPath);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtils.i("surfaceChanged");
        }

    };

    public void updateLayout(Configuration newConfig) {

        if (previewPlayer == null) {
            return;
        }
        //适配视频的高度
        int videoWidth = previewPlayer.getVideoWidth();
        int videoHeight = previewPlayer.getVideoHeight();
        mSurfaceView.updateLayout(newConfig, videoWidth, videoHeight);
    }

    public void destroy() {
        mViewController.destroy();
    }
}
