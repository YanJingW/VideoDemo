package com.yanjingw.video.inter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import com.yanjingw.video.util.LogUtils;

/**
 * Created by wangyanjing on 2017/12/26.
 */

public class PreviewPlayer extends Player {

    private SurfaceHolder mSurfaceHolder;
    private String videoPath;
    private OnAfterStartListener onAfterStartListener;
    private OnBeforeStartListener onBeforeStartListener;
    private OnCompletionListener onMyCompletionListener;

    /**
     * 标记下一次start开始时，处于播放还是暂停状态
     */
    private boolean mToPlay = true;

    static final Handler myHandler = new Handler(Looper.getMainLooper()) {
    };

    /**
     * 最后一次视频播放的位置
     */
    private int mLastVideoPosition = 0;

    public PreviewPlayer() {
//        super();
    }

    public void createMediaPlayer(SurfaceHolder holder) {
        stop();
        this.mSurfaceHolder = holder;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setLooping(true);//重复播放
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setDisplay(holder);
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer != null) {
            mToPlay = false;
            mMediaPlayer.pause();
            mLastVideoPosition = mMediaPlayer.getCurrentPosition();
        }
    }

    // TODO: 2018/3/19 在什么时候此方法生效
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mToPlay = false;
        //跳到视频最开始位置
        mLastVideoPosition = 0;
        mMediaPlayer.seekTo(mLastVideoPosition);
        onMyCompletionListener.onCompletion(mediaPlayer);
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        LogUtils.i("mMediaPlayer-装载完成");
//        play();
        //是否开始播放
//        if (!mToPlay) {
//            pause();
//        } else {
//        }
        mMediaPlayer.seekTo(mLastVideoPosition);
        mLastVideoPosition = 0;

//        //延时：避免出现上一个视频的画面闪屏
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //跳转指定位置
//                if (mLastVideoPosition > 0) {
//                    mMediaPlayer.seekTo(mLastVideoPosition);
//                    mLastVideoPosition = 0;
//                }
//            }
//        }, 0);

        onAfterStartListener.onAfterStart(mediaPlayer);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 发生错误重新播放
        super.onError(mp, what, extra);
        LogUtils.i("onError::::" + what + "--" + extra);
        mMediaPlayer.reset();
        play();
        return false;
    }


    public interface OnCompletionListener {
        void onCompletion(MediaPlayer var1);
    }

    public interface OnBeforeStartListener {
        void OnBeforeStart(MediaPlayer var1);
    }

    public interface OnAfterStartListener {
        void onAfterStart(MediaPlayer var1);
    }

    public void setOnAfterStartListener(OnAfterStartListener onAfterStartListener) {
        this.onAfterStartListener = onAfterStartListener;
    }

    public void setOnBeforeStartListener(OnBeforeStartListener onBeforeStartListener) {
        this.onBeforeStartListener = onBeforeStartListener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onMyCompletionListener = listener;
    }
}
