/*
 * <copyright file="Player.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yanjingw.video.inter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.yanjingw.video.util.LogUtils;

import java.io.IOException;

/**
 * Player interface.
 */
public abstract class Player implements MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener {

    protected boolean mIsPrepared = false;
    protected MediaPlayer mMediaPlayer;
    protected float mVolumeValue = 1.0f;
    protected String mDataSource;
    protected int mVideoWidth = 0;
    protected int mVideoHeight = 0;

    public Player() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setLooping(true);
    }

    public void setDataSource(String dataSource) {
        stop();
        if (mMediaPlayer != null && !mIsPrepared && !TextUtils.isEmpty(dataSource)) {
            try {
                mDataSource = dataSource;
                mMediaPlayer.setDataSource(mDataSource);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                LogUtils.d(e.getMessage());
            }
        }
    }

    public void stop() {
        if (mMediaPlayer != null && mIsPrepared) {
            mIsPrepared = false;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    public void back2Start() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.stop();
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void play() {
        if (mMediaPlayer != null && mIsPrepared && !isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.seekTo(msec);
            }
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public void release() {
        if (mMediaPlayer != null) {
            mIsPrepared = false;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void setVolume(float volume) {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mVolumeValue = volume;
                mMediaPlayer.setVolume(mVolumeValue, mVolumeValue);
            }
        }
    }

    public float getVolume() {
        return mVolumeValue;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int extra) {
        LogUtils.v("Player.onBufferingUpdate() - extra = " + extra);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtils.v("Player.onCompletion()");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.e("Player.onError()");
        mIsPrepared = false;
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        LogUtils.v("Player.onInfo()");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtils.d("Player.onPrepared()");
        mIsPrepared = true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        LogUtils.d("Player.onSeekComplete()");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        LogUtils.d("Player.onVideoSizeChanged()");
        mVideoWidth = width;
        mVideoHeight = height;
    }
}
