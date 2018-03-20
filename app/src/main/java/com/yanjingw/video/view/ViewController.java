/*
 * <copyright file="ViewController.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yanjingw.video.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanjingw.video.R;
import com.yanjingw.video.inter.Player;
import com.yanjingw.video.util.LogUtils;
import com.yanjingw.video.util.PlayerUtils;
import com.yanjingw.video.util.TimeUtils;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * ViewController class.
 */
public class ViewController extends FrameLayout {

    private Player mVideoPlayer;
    private Player mAudioPlayer;
    private Timer mTimer;

    private View mRootView;

    private TextView mCurrentPositionTextView;
    private TextView mDurationTextView;
    private ProgressBar mSeekBar;

    private int mDisplayTime = 3000;
    private boolean mIsTouch = false;
    private boolean mHideProgressBar = false;
    private int playerViewW;
    private int playerViewH;
    private ImageView controlImageView;

    public ViewController(@NonNull Context context) {
        super(context);
        initView();
    }

    public ViewController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewController);
        if (typedArray != null) {
            mHideProgressBar = typedArray.getBoolean(R.styleable.ViewController_hide_progressbar, false);
            typedArray.recycle();
        }
        initView();
    }

    public ViewController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDisplayTime = 3000;
        if (mRootView.getVisibility() == INVISIBLE) {
            mRootView.setVisibility(VISIBLE);
        }

        return false;
    }

    public void setVideoPlayer(Player player) {
        mVideoPlayer = player;
    }

    public void setAudioPlayer(Player player) {
        mAudioPlayer = player;
    }

    public void destroy() {
        mTimer.cancel();
    }

    public void startPlaySrc() {
        mVideoPlayer.play();
        mAudioPlayer.play();
    }

    public void endPlaySrc() {
        mVideoPlayer.back2Start();
        mAudioPlayer.back2Start();
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.media_player, this, false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);

        controlImageView = (ImageView) mRootView.findViewById(R.id.iv_control);
        controlImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoPlayer != null) {
                    if (mVideoPlayer.isPlaying()) {
                        controlImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play));
                        mVideoPlayer.pause();
                        if (mAudioPlayer != null) {
                            mAudioPlayer.pause();
                        }
                    } else {
                        controlImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_pause));
                        mVideoPlayer.play();
                        if (mAudioPlayer != null) {
                            mAudioPlayer.play();
                        }
                    }
                }
            }
        });

        mCurrentPositionTextView = (TextView) mRootView.findViewById(R.id.tv_position);
        mDurationTextView = (TextView) mRootView.findViewById(R.id.tv_duration);
        mSeekBar = (ProgressBar) mRootView.findViewById(R.id.progressbar);

        if (mHideProgressBar) {
            mCurrentPositionTextView.setVisibility(INVISIBLE);
            mDurationTextView.setVisibility(INVISIBLE);
            mSeekBar.setVisibility(INVISIBLE);
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg;
                msg = mHandler.obtainMessage();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }, 0, 50);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mIsTouch) {
                return;
            }
            if (null != mVideoPlayer) {
                int currentPosition = (int) ((float) mVideoPlayer.getCurrentPosition() / 1000.0f);

                int duration = (int) ((float) mVideoPlayer.getDuration() / 1000.0f);
                if (null != mPositionChangeListener) {
                    mPositionChangeListener.positionChanged(mVideoPlayer.getCurrentPosition(), mVideoPlayer.getDuration());
                }

                if (null != mPlayStateChangedListener) {
                    mPlayStateChangedListener.playStateChanged(mVideoPlayer.isPlaying());
                }
                if (currentPosition >= 0 && duration >= 0l) {
                    int progress = getCurrentProgress(currentPosition, duration);
                    mSeekBar.setProgress(progress);
                    mCurrentPositionTextView.setText(TimeUtils.formatNumberToMinuteSecond((double) currentPosition));
                    mDurationTextView.setText(TimeUtils.formatNumberToMinuteSecond((double) duration));
                    LogUtils.v("actual progress is " + progress + "%");
                }
            }

            mDisplayTime -= 50;
            if (mDisplayTime <= 0 && mRootView.getVisibility() == VISIBLE) {
                mRootView.setVisibility(INVISIBLE);
            }
        }
    };

    private int getCurrentProgress(float currentTime, float totalTime) {
        float totalProcess = 100;
        return (int) (currentTime / totalTime * totalProcess);
    }

    private int getCurrentTime(float currentProcess, float totalTime) {
        float totalProcess = 100;
        return (int) (currentProcess / totalProcess * totalTime);
    }

    private onPositionChangedListener mPositionChangeListener;

    public void pause() {
        controlImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play));
    }

    public interface onPositionChangedListener {
        public void positionChanged(long position, long duration);
    }

    public void setPositionChangeListener(onPositionChangedListener positionChangeListener) {
        mPositionChangeListener = positionChangeListener;
    }

    private onPlayStateChangedListener mPlayStateChangedListener;

    public interface onPlayStateChangedListener {
        public void playStateChanged(boolean isPlaying);
    }

    public void setPlayStateChangedListener(onPlayStateChangedListener playStateChangedListener) {
        mPlayStateChangedListener = playStateChangedListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        playerViewW = this.getWidth();
        playerViewH = this.getHeight();
    }

    public void fitVideoSize(int videoWidth, int videoHeight) {
        int parentWidth = playerViewW;
        int parentHeight = playerViewH;
        int screenWidth = PlayerUtils.getScreenWidth(this.getContext());
        int screenHeight = PlayerUtils.getScreenHeight(this.getContext());

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
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = surfaceViewH;
        params.width = surfaceViewW;
        this.setLayoutParams(params);
    }
}
