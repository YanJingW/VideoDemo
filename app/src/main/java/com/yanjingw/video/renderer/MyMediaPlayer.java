package com.yanjingw.video.renderer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import com.yanjingw.video.util.LogUtil;

/**
 * Created by wangyanjing on 2017/12/26.
 */

public class MyMediaPlayer {

    private MediaPlayer mMediaPlayer;
    private final SurfaceHolder mSurfaceHolder;
    private String videoPath;
    private OnPreparedListener onMyPreparedListener;

    public MyMediaPlayer(SurfaceHolder mSurfaceHolder) {
        super();
        this.mSurfaceHolder = mSurfaceHolder;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /**
     * 记录当前的播放位置并停止播放
     */
    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play() {
        try {
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(videoPath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setLooping(true);//重复播放
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.i("mMediaPlayer-装载完成");
                    mMediaPlayer.start();
                    onMyPreparedListener.onPrepared(mp);
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    LogUtil.i("onError::::" + what + "--" + extra);
                    mMediaPlayer.reset();
                    play();
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: 2018/3/19 在什么时候此方法生效
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onMyPreparedListener = onPreparedListener;
    }

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer var1);
    }
}
